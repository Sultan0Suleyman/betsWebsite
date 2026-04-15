export interface FinishGameRequest {
  homeScore: number | null;
  awayScore: number | null;

  extraTimeHomeScore: number | null;
  extraTimeAwayScore: number | null;

  penaltyHomeScore: number | null;
  penaltyAwayScore: number | null;
}
