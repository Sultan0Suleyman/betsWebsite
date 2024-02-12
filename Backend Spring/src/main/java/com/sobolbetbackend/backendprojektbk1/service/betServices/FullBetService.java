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

@Service
public class FullBetService {
    private final FullBetRepo fullBetRepo;
    private final OrdinaryBetRepo ordinaryBetRepo;
    private final PlayerRepo playerRepo;
    private final GameRepo gameRepo;

    @Autowired
    public FullBetService(FullBetRepo fullBetRepo, OrdinaryBetRepo ordinaryBetRepo, PlayerRepo playerRepo, GameRepo gameRepo) {
        this.fullBetRepo = fullBetRepo;
        this.ordinaryBetRepo = ordinaryBetRepo;
        this.playerRepo = playerRepo;
        this.gameRepo = gameRepo;
    }

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

    public List<FullBetDTO> getListOfFullBets(Long userId){
        List<FullBetDTO> listFull = new ArrayList<>();
        for(FullBet fullBet: playerRepo.findByUserId(userId).getBets()){
            listFull.add(new FullBetDTO(fullBet.getId(),fullBet.getFinalCoefficient(),
                    fullBet.getBetAmount(), fullBet.getFinalBetPayout(), fullBet.getBetStatus(),
                    fullBet.getBets().size()));
        }
        return listFull;
    }

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

    @Transactional
    public void sellBet(Long fullBetId){
        FullBet fullBet = fullBetRepo.findById(fullBetId).orElseThrow();
        if(fullBet.getFinalBetPayout()==null) {
            fullBet.setFinalBetPayout(fullBet.getBetAmount() * 0.9);
            fullBetRepo.findById(fullBetId).orElseThrow().getPlayer()
                    .updateBalance(fullBetRepo.findById(fullBetId).orElseThrow().getFinalBetPayout());
        }
    }
}
