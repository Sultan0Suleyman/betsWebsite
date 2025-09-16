package com.sobolbetbackend.backendprojektbk1.dto.linemakerBetsStats;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object containing the results of game cancellation and bet refund operations.
 * This DTO holds statistics about refunded bets when a game is cancelled due to
 * unforeseen circumstances such as weather conditions, player injuries, or other
 * force majeure events. Provides detailed feedback about the refund process
 * including financial summaries and any processing errors.
 */
@Setter
@Getter
public class RefundResultDTO {

    /** Total number of individual bets that were refunded */
    private int totalBetsRefunded;

    /** Number of full bet slips (single or express) that were processed for refund */
    private int refundedBets;

    /** Total monetary amount refunded to players */
    private double totalRefundAmount;

    /** List of error messages encountered during the refund process */
    private List<String> errors;

    /**
     * Default constructor that initializes an empty errors list.
     */
    public RefundResultDTO() {
        this.errors = new ArrayList<>();
    }

    /**
     * Increments the count of refunded bet slips by one.
     * This tracks the number of full bets (single or express) that were processed.
     */
    public void incrementRefundedBets() {
        this.refundedBets++;
    }

    /**
     * Adds an error message to the list of refund processing errors.
     *
     * @param error description of the error that occurred during refund processing
     */
    public void addError(String error) {
        this.errors.add(error);
    }
}