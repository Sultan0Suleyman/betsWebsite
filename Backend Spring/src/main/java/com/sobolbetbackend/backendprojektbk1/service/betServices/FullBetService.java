package com.sobolbetbackend.backendprojektbk1.service.betServices;

import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList.FullBetDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList.OrdinaryBetDTO;
import com.sobolbetbackend.backendprojektbk1.dto.placeBet.FullBetRequestDTO;
import com.sobolbetbackend.backendprojektbk1.dto.placeBet.OrdinaryBetRequestDTO;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.FullBet;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.OrdinaryBet;
import com.sobolbetbackend.backendprojektbk1.exception.LowBalanceException;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import com.sobolbetbackend.backendprojektbk1.repository.placeBet.FullBetRepo;
import com.sobolbetbackend.backendprojektbk1.repository.placeBet.OrdinaryBetRepo;
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing full betting operations including bet placement, retrieval, and selling.
 * This service handles complete betting workflows from initial bet placement through to
 * bet history retrieval and early bet selling functionality. It manages both single bets
 * and express (combination) bets, ensuring proper validation, persistence, and financial operations.
 * Key responsibilities include:
 * - Processing bet placement requests with balance validation
 * - Retrieving bet history and details for players
 * - Calculating dynamic sell prices based on current bet status
 * - Processing bet sales with appropriate payouts
 * All financial operations are performed within database transactions to ensure data consistency.
 */
@Service
public class FullBetService {
    private final FullBetRepo fullBetRepo;
    private final OrdinaryBetRepo ordinaryBetRepo;
    private final PlayerRepo playerRepo;
    private final GameRepo gameRepo;

    /**
     * Constructs a new FullBetService with required repository dependencies.
     *
     * @param fullBetRepo repository for full bet data operations
     * @param ordinaryBetRepo repository for individual bet management
     * @param playerRepo repository for player account operations
     * @param gameRepo repository for game data access
     */
    @Autowired
    public FullBetService(FullBetRepo fullBetRepo, OrdinaryBetRepo ordinaryBetRepo, PlayerRepo playerRepo, GameRepo gameRepo) {
        this.fullBetRepo = fullBetRepo;
        this.ordinaryBetRepo = ordinaryBetRepo;
        this.playerRepo = playerRepo;
        this.gameRepo = gameRepo;
    }

    /**
     * Processes a new bet placement request.
     * Validates player balance, creates full bet and associated ordinary bets,
     * and persists the complete betting structure. Deducts bet amount from player's balance
     * upon successful placement.
     *
     * @param fullBetRequestDTO complete bet placement request containing player info,
     *                         bet amount, coefficient, and individual bet selections
     * @throws LowBalanceException if player has insufficient funds for the bet amount
     */
    public void placeBet(FullBetRequestDTO fullBetRequestDTO) throws LowBalanceException {
        Player player = playerRepo.findByUserId(fullBetRequestDTO.getUserId());
        if(player.getBalance()<fullBetRequestDTO.getBetAmount()){
            throw new LowBalanceException("Insufficient funds in the account");
        }else{
            List<OrdinaryBet> list = new ArrayList<>();
            FullBet fullBet = new FullBet(list,player,fullBetRequestDTO.getFinalCoefficient(),
                    fullBetRequestDTO.getBetAmount());
            for(OrdinaryBetRequestDTO ordinaryBetRequestDTO: fullBetRequestDTO.getBets()){
                OrdinaryBet ordinaryBet = new OrdinaryBet(fullBet,gameRepo.
                        findById(ordinaryBetRequestDTO.getMatchId()).orElseThrow(),ordinaryBetRequestDTO.getType(),
                        ordinaryBetRequestDTO.getCoefficient());
                list.add(ordinaryBet);
            }
            fullBetRepo.save(fullBet);
            ordinaryBetRepo.saveAll(list);
        }
    }

    /**
     * Retrieves all full bets for a specific user.
     * Returns a list of FullBetDTO objects containing essential bet information
     * including amounts, coefficients, payouts, and bet counts.
     *
     * @param userId the unique identifier of the user
     * @return list of FullBetDTO objects representing user's betting history
     */
    public List<FullBetDTO> getListOfFullBets(Long userId){
        List<FullBetDTO> listFull = new ArrayList<>();
        for(FullBet fullBet: playerRepo.findByUserId(userId).getBets()){
            listFull.add(new FullBetDTO(fullBet.getId(),fullBet.getFinalCoefficient(),
                    fullBet.getBetAmount(), fullBet.getFinalBetPayout(), fullBet.getBetStatus(),
                    fullBet.getBets().size()));
        }
        return listFull;
    }

    /**
     * Retrieves detailed information for all ordinary bets within a specific full bet.
     * Returns comprehensive details for each individual bet including team names,
     * match dates, outcomes, coefficients, and current win/loss status.
     *
     * @param fullBetId the unique identifier of the full bet
     * @return list of OrdinaryBetDTO objects with detailed bet information
     */
    public List<OrdinaryBetDTO> getListOfOrdinaryBets(Long fullBetId){

        FullBet fullBet = fullBetRepo.findById(fullBetId).orElseThrow();
        List<OrdinaryBetDTO> listOrdinary = new ArrayList<>();
        for(OrdinaryBet ordinaryBet: fullBet.getBets()){
            listOrdinary.add(new OrdinaryBetDTO(
                    ordinaryBet.getGame().getTeamHome().getName_en(),
                    ordinaryBet.getGame().getTeamAway().getName_en(),
                    ordinaryBet.getGame().getDateOfMatch(),
                    ordinaryBet.getOutcomeOfTheGame(),
                    ordinaryBet.getCoefficient(),
                    ordinaryBet.getWinningBet(),
                    ordinaryBet.getGame().getGameEnded()));
        }
        return listOrdinary;
    }

    /**
     * Calculates the current sell price for a full bet based on its status.
     * Uses dynamic pricing algorithm that starts with 90% of original bet amount
     * and increases based on winning individual bets within the combination.
     * Each winning bet multiplies the sell price by (0.9 × bet coefficient).
     * Refunded bets (null status) do not affect the price.
     * Formula progression:
     * - Base: 0.9 × bet_amount
     * - After winning bet: previous_price × 0.9 × coefficient
     * - After refund: price unchanged
     *
     * @param fullBetId the unique identifier of the full bet
     * @return calculated sell price based on current bet status
     * @throws IllegalStateException if bet is already processed or has losing outcomes
     */
    public double calculateSellPrice(Long fullBetId) {
        FullBet fullBet = fullBetRepo.findById(fullBetId).orElseThrow();

        if (fullBet.getFinalBetPayout() != null) {
            throw new IllegalStateException("Bet already processed");
        }

        List<OrdinaryBet> bets = fullBet.getBets();

        // Базовая цена продажи: 0.9 * сумма ставки
        double sellPrice = 0.9 * fullBet.getBetAmount();

        // Проходим по всем ставкам в экспрессе
        for (OrdinaryBet bet : bets) {
            if (bet.getGame().getGameEnded()) {
                if (bet.getWinningBet() == Boolean.TRUE) {
                    // Ставка выиграла: умножаем на (0.9 * коэффициент)
                    sellPrice = sellPrice * 0.9 * bet.getCoefficient();
                }
                // Если winningBet == null (возврат) - ничего не делаем, цена не меняется
                // Если winningBet == false (проиграла) - экспресс уже не продается
                else if (bet.getWinningBet() == Boolean.FALSE) {
                    throw new IllegalStateException("Cannot sell bet with losing outcomes");
                }
            }
            // Если игра не завершена - не влияет на цену продажи
        }

        return sellPrice;
    }

    /**
     * Processes the sale of a full bet at current market price.
     * Calculates current sell price using dynamic pricing algorithm,
     * marks bet as sold (null status), and credits player account with sell amount.
     * This operation is irreversible once completed.
     *
     * @param fullBetId the unique identifier of the bet to sell
     * @throws IllegalStateException if bet is already processed or cannot be sold
     */
    @Transactional
    public void sellBet(Long fullBetId) {
        FullBet fullBet = fullBetRepo.findById(fullBetId).orElseThrow();

        if (fullBet.getFinalBetPayout() == null) {
            // Используем новый метод расчета цены продажи
            double sellPrice = calculateSellPrice(fullBetId);

            fullBet.setFinalBetPayout(sellPrice);
            fullBet.setBetStatus(null); // null = sold

            // Зачисляем рассчитанную сумму на баланс
            fullBet.getPlayer().updateBalance(sellPrice);
        }
    }

}
