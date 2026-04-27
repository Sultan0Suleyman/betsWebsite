import {
  Component,
  DoCheck, OnDestroy,
  OnInit,
} from '@angular/core';
import {CouponService} from "../../../services/CouponService/coupon.service";
import {AuthService} from "../../../services/AuthService/auth.service";
import {HttpClient} from "@angular/common/http";
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-coupon-bets-dialog',
  templateUrl: './coupon-bets-dialog.component.html',
  styleUrls: ['./coupon-bets-dialog.component.css']
})
export class CouponBetsDialogComponent implements OnInit,DoCheck,OnDestroy {
  private apiUrl = `${environment.apiUrl}/bet`;

  couponBets: any[] = []
  message: string = ""
  errorMessage: string = ""
  betAmount: number = 0
  totalOdds: number = 0
  possibleWin: number = 0
  isBetPlaced: boolean = false
  couponCount: number = 0
  userId = this.authService.getCurrentUser().value.username

  constructor(public couponService: CouponService,
              private authService: AuthService,
              private http: HttpClient) {}

  ngOnInit(): void {
    this.couponBets = this.couponService.getCouponBets();
  }

  ngDoCheck(): void {
    this.calculateTotalOddsAndPossibleWin();
    if (this.betAmount < 0) this.possibleWin = 0
  }
  ngOnDestroy(): void {
    if(this.isBetPlaced){
      this.couponService.clearCoupon()
      this.couponBets = []
      this.isBetPlaced = false
    }
  }

  makeBet(): void {
    if (this.authService.isAuthenticated()) {
      const betsToSend = this.couponBets.map(bet => ({ matchId: bet.matchId, type: bet.type, coefficient: bet.value}));
      const requestBody = {
        bets: betsToSend,
        finalCoefficient: this.totalOdds,
        betAmount: this.betAmount,
        userId: this.userId
      }
      this.http.post<any>(this.apiUrl+'/place',requestBody).subscribe({
        next: (response)=>{
          this.errorMessage = ''
          this.message = response.message
        },
        error: (errorResponse) => {
          // Access the error object and display the appropriate message
          if (errorResponse && errorResponse.error && errorResponse.error.error) {
            this.message = ''
            this.errorMessage = errorResponse.error.error
          } else {
            // Handle other types of errors or display a generic message
            this.message = ''
            this.errorMessage = 'An error occurred during withdrawal request.'
          }
        }
      })
      this.isBetPlaced = true
      this.couponCount = this.couponBets.length
    } else {
      this.errorMessage="You have to authorise"
    }
  }

  public calculateTotalOddsAndPossibleWin(): void {
    if (this.couponBets.length > 0) {
      this.totalOdds = this.couponBets.reduce((total, bet) => total * bet.value, 1)
      this.possibleWin = this.betAmount * this.totalOdds
    } else {
      this.totalOdds = 0
      this.possibleWin = 0
    }
  }

  onKeyPress(event: KeyboardEvent) {
    const input = event.currentTarget as HTMLInputElement;
    const currentValue = input.value;

    // Регулярное выражение для проверки ввода - не более 2 знаков после запятой
    const regex = /^\d*\.?\d{0,1}$/;

    // Проверяем текущее значение поля ввода
    if (!regex.test(currentValue)) {
      event.preventDefault(); // Запрещаем ввод, если значение не соответствует регулярному выражению
    }
  }
}
