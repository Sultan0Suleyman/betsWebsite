import { Component, Input, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";

interface FullBet {
  id: number;
  betAmount: number;
  finalCoefficient: number;
  countOfOrdinaryBets: number;
  betStatus?: string;
  finalBetPayout?: number;
}

@Component({
  selector: 'app-bet-history-content',
  templateUrl: './bet-history-content.component.html',
  styleUrls: ['./bet-history-content.component.css']
})
export class BetHistoryContentComponent implements OnInit {
  @Input() userId: number = 0
  unsettledBets: FullBet[] = []
  paidBets: FullBet[] = []
  message = ''
  ordinaryBets: any = []
  parentBetId!: number
  sellButtonMessage = ''
  selectedBet = 0
  isBetClicked = false
  buttonAppearanceChecker = true

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getBetsFromBackend();
  }

  getBetsFromBackend() {
    const apiUrl = `http://localhost:8080/bet/list/fullBet/${this.userId}`;

    this.http.get<FullBet[]>(apiUrl).subscribe({
      next: (response) => {
        response.sort((a, b) => b.id - a.id);

        this.unsettledBets = response.filter(bet => bet.finalBetPayout === null);
        this.paidBets = response.filter(bet => bet.finalBetPayout !== null);
        if (response.length === 0) {
          this.message = "History is empty";
        }
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  onBetClick(fullBetId: number,betAmount?: number) {
    const apiUrl = `http://localhost:8080/bet/list/ordinaryBets/${fullBetId}`;
    this.isBetClicked = !this.isBetClicked

      if (this.selectedBet !== fullBetId || this.isBetClicked) {
        this.buttonAppearanceChecker = true
        this.selectedBet = fullBetId

        this.parentBetId = fullBetId
        this.http.get<any>(apiUrl).subscribe({
          next: (response) => {
            console.log(response)
            this.ordinaryBets = response
            if (typeof betAmount !== 'undefined') {
              this.sellButtonMessage = 'Sell for ' + (betAmount * 0.9).toFixed(2);
            }
          },
          error: (error) => {
            console.log(error)
          }
        });
      } else if (!this.isBetClicked) {
        this.selectedBet = 0
        this.ordinaryBets = []
        this.buttonAppearanceChecker = false
      }

  }

  getCurrentDate(): Date {
    return new Date()
  }

  shouldShowButton(): boolean {
    for (let i = 0; i < this.ordinaryBets.length; i++) {
      const dateOfMatch = new Date(this.ordinaryBets[i].dateOfMatch)
      const currentDateInSameTimeZone = new Date(this.getCurrentDate().toLocaleString('en-US', { timeZone: 'UTC' }))
      if (dateOfMatch < currentDateInSameTimeZone && !this.ordinaryBets[i].isGameEnded) {
        this.buttonAppearanceChecker = false
      }
    }
    return this.buttonAppearanceChecker
  }

  onSellButtonClick(fullBetId: number): void {
    const apiUrl = `http://localhost:8080/bet/sell`;
    const confirmed = window.confirm('Are you sure, that you want to sell a bet?')

    if (confirmed) {
      // Здесь вызывайте функцию для обработки продажи ставки
      this.http.put(apiUrl,fullBetId).subscribe({
        next:(response:any)=>{
          alert(response.message)
          this.getBetsFromBackend()
        },
        error:(errorResponse)=>{
          if (errorResponse && errorResponse.error && errorResponse.error.error) {
            alert(errorResponse.error.error)
          }
        }
      })
    }
  }

}
