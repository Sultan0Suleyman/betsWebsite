package com.sobolbetbackend.backendprojektbk1.dto.linemakerBetsStats;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object containing the results of bet calculation processing.
 * This DTO holds comprehensive statistics about processed bets including
 * win/loss counts, financial summaries, and any processing errors that occurred.
 * Used as a response object for bet processing operations to provide
 * detailed feedback to linemakers and system administrators.
 */
@Setter
@Getter
public class BetCalculationResultDTO {

    /** Total number of individual bets processed in the operation */
    private int totalBetsProcessed;

    /** Number of bets that resulted in wins */
    private int winningBets;

    /** Number of bets that resulted in losses */
    private int losingBets;

    /** Number of bets that were refunded */
    private int refundedBets;

    /** Total amount paid out to players in the operation */
    private double totalPayouts;

    /** Bookmaker's profit or loss from the processed bets */
    private double bookmakerProfit;

    /** List of error messages encountered during processing */
    private List<String> errors;

    /**
     * Default constructor that initializes an empty errors list.
     */
    public BetCalculationResultDTO() {
        this.errors = new ArrayList<>();
    }

    /**
     * Constructor with all parameters for creating a complete result object.
     *
     * @param totalBetsProcessed total number of bets processed
     * @param winningBets number of winning bets
     * @param losingBets number of losing bets
     * @param refundedBets number of refunded bets
     * @param totalPayouts total amount paid to players
     * @param bookmakerProfit bookmaker's profit from the operation
     */
    public BetCalculationResultDTO(int totalBetsProcessed, int winningBets,
                                   int losingBets, int refundedBets,
                                   double totalPayouts, double bookmakerProfit) {
        this();
        this.totalBetsProcessed = totalBetsProcessed;
        this.winningBets = winningBets;
        this.losingBets = losingBets;
        this.refundedBets = refundedBets;
        this.totalPayouts = totalPayouts;
        this.bookmakerProfit = bookmakerProfit;
    }

    /**
     * Adds an error message to the list of processing errors.
     *
     * @param error description of the error that occurred
     */
    public void addError(String error) {
        this.errors.add(error);
    }

    /**
     * Increments the count of winning bets by one.
     */
    public void incrementWinningBets() {
        this.winningBets++;
    }

    /**
     * Increments the count of losing bets by one.
     */
    public void incrementLosingBets() {
        this.losingBets++;
    }

    /**
     * Increments the count of refunded bets by one.
     */
    public void incrementRefundedBets() {
        this.refundedBets++;
    }
}