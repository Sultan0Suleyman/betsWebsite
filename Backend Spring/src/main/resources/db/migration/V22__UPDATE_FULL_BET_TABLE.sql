ALTER TABLE full_bet
DROP COLUMN win_amount,
    DROP COLUMN winning_bet,
    DROP COLUMN is_bet_sold,
    DROP COLUMN sell_amount,
    ADD COLUMN bet_status BOOLEAN,
    ADD COLUMN final_bet_payout DOUBLE