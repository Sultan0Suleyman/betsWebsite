import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SettlementMatch } from './models/settlement-match.model';
import { FinishGameRequest } from './models/finish-game-request.model';
import { BetCalculationResult } from './models/bet-calculation-result.model';
import { RefundResult } from './models/refund-result.model';

@Injectable({
  providedIn: 'root'
})
export class MatchSettlementService {
  private readonly baseUrl = 'http://localhost:8080/linemaker/settlement';

  constructor(private http: HttpClient) {}

  getMatches(): Observable<SettlementMatch[]> {
    return this.http.get<SettlementMatch[]>(`${this.baseUrl}/games`);
  }

  getMatch(gameId: number): Observable<SettlementMatch> {
    return this.http.get<SettlementMatch>(`${this.baseUrl}/games/${gameId}`);
  }

  saveScore(gameId: number, body: FinishGameRequest): Observable<SettlementMatch> {
    return this.http.post<SettlementMatch>(`${this.baseUrl}/games/${gameId}/save-score`, body);
  }

  finishAndSettle(gameId: number, body: FinishGameRequest): Observable<BetCalculationResult> {
    return this.http.post<BetCalculationResult>(`${this.baseUrl}/games/${gameId}/finish`, body);
  }

  cancelMatch(gameId: number): Observable<RefundResult> {
    return this.http.post<RefundResult>(`${this.baseUrl}/games/${gameId}/cancel`, {});
  }
}
