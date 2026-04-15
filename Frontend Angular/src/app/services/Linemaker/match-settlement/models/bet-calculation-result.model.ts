export interface BetCalculationResult {
  totalBetsProcessed: number;
  winningBets: number;
  losingBets: number;
  totalPayouts: number;
  bookmakerProfit: number;
  errors: string[];
}
