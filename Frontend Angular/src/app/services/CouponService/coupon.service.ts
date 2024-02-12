import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private readonly couponKey = 'couponBets';
  private couponBets: any[] = [];

  constructor() {
    // При инициализации сервиса, попробуем загрузить данные из sessionStorage
    const storedCouponData = sessionStorage.getItem(this.couponKey);
    if (storedCouponData) {
      this.couponBets = JSON.parse(storedCouponData);
    }
  }

  getCouponLength(): number {
    return this.couponBets.length;
  }

  getCouponBets(): any[] {
    return this.couponBets;
  }

  handleBetClick(bet: any, gameData: any) {
    console.log("Selected Bet:", bet);
    console.log("Match ID", gameData.id);
    const matchInfo = {
      matchId: gameData.id,
      teamHome: gameData.teamHome,
      teamAway: gameData.teamAway,
      dateOfMatch: gameData.dateOfMatch,
    };

    const betWithMatchInfo = { ...bet, ...matchInfo };
    console.log("Инфа о матче", betWithMatchInfo);

    this.addOrUpdateBetToCoupon(betWithMatchInfo);
  }

  addOrUpdateBetToCoupon(bet: any): void {
    const existingBetIndex = this.couponBets.findIndex(b => b.matchId === bet.matchId);

    if (existingBetIndex !== -1) {
      // Если ставка существует, обновляем ее
      this.couponBets[existingBetIndex] = bet;
    } else {
      // Если ставка не существует, добавляем новую
      this.couponBets.push(bet);
    }

    // Сохраняем данные купона в sessionStorage
    this.saveCouponToSessionStorage();
  }

  clearCoupon(): void {
    this.couponBets = [];
    // Очищаем также данные в sessionStorage
    sessionStorage.removeItem(this.couponKey);
  }

  public saveCouponToSessionStorage(): void {
    sessionStorage.setItem(this.couponKey, JSON.stringify(this.couponBets));
  }

  removeBet(index: number): void {
    this.couponBets.splice(index, 1);
    this.saveCouponToSessionStorage()
  }

}
