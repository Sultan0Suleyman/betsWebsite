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

        console.log('Paid bets:', this.paidBets);
        this.paidBets.forEach(bet => {
          console.log(`Bet ${bet.id}: betStatus = ${bet.betStatus} (type: ${typeof bet.betStatus})`);
        });

        if (response.length === 0) {
          this.message = "History is empty";
        }
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  onBetClick(fullBetId: number, betAmount?: number) {
    const apiUrl = `http://localhost:8080/bet/list/ordinaryBets/${fullBetId}`;
    this.isBetClicked = !this.isBetClicked

    if (this.selectedBet !== fullBetId || this.isBetClicked) {
      this.buttonAppearanceChecker = true
      this.selectedBet = fullBetId

      this.parentBetId = fullBetId
      this.http.get<any>(apiUrl).subscribe({
        next: (response) => {
          console.log('=== ORDINARY BETS DEBUG ===');
          console.log('Full response:', response);
          console.log('Response length:', response.length);

          // Детальная отладка каждой ставки
          response.forEach((bet: any, index: number) => {
            console.log(`\n--- Ordinary bet ${index + 1} ---`);
            console.log('Team match:', `${bet.teamHome} vs ${bet.teamAway}`);
            console.log('winningBet:', bet.winningBet, '(type:', typeof bet.winningBet, ')');
            console.log('isGameEnded:', bet.isGameEnded, '(type:', typeof bet.isGameEnded, ')');
            console.log('dateOfMatch:', bet.dateOfMatch);
            console.log('outcomeOfTheGame:', bet.outcomeOfTheGame);
            console.log('coefficient:', bet.coefficient);

            // Проверка логики статуса
            let expectedStatus = '';
            let expectedColor = '';

            if (bet.winningBet === null && !bet.isGameEnded) {
              expectedStatus = 'Unsettled';
              expectedColor = 'orange';
            } else if (bet.winningBet === null && bet.isGameEnded) {
              expectedStatus = 'Refund';
              expectedColor = 'green';
            } else if (bet.winningBet === true && bet.isGameEnded) {
              expectedStatus = 'Win';
              expectedColor = 'green';
            } else if (bet.winningBet === false && bet.isGameEnded) {
              expectedStatus = 'Lose';
              expectedColor = 'red';
            } else {
              expectedStatus = 'UNKNOWN CASE';
              expectedColor = 'black';
            }

            console.log('Expected status:', expectedStatus);
            console.log('Expected color:', expectedColor);
          });

          console.log('=== END DEBUG ===\n');

          this.ordinaryBets = response;

          // Запросить актуальную цену продажи с бэкенда
          this.http.get<number>(`http://localhost:8080/bet/sell-price/${fullBetId}`).subscribe({
            next: (sellPrice) => {
              this.sellButtonMessage = `Sell for ${sellPrice.toFixed(2)}€`;
              console.log('Sell price from backend:', sellPrice);
            },
            error: (error) => {
              console.log('Error fetching sell price:', error);
              this.sellButtonMessage = 'Cannot sell now';
            }
          });
        },
        error: (error) => {
          console.log('Error fetching ordinary bets:', error)
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
