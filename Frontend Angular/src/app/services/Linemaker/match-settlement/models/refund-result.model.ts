export interface RefundResult {
  totalBetsRefunded: number;
  refundedBets: number;
  totalRefundAmount: number;
  errors: string[];
}
