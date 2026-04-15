export interface SettlementMatch {
  id: number;

  sport: string;
  country: string;
  league: string;

  homeTeam: string;
  awayTeam: string;

  dateOfMatch: string;

  scoreHome: number | null;
  scoreAway: number | null;

  extraTimeHomeScore: number | null;
  extraTimeAwayScore: number | null;

  penaltyHomeScore: number | null;
  penaltyAwayScore: number | null;

  gameEnded: boolean;
  resultsProcessed: boolean;
}
