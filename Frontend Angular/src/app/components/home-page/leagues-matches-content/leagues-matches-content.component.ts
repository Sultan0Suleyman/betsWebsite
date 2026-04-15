import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from '@angular/router';
import {CouponService} from "../../../services/CouponService/coupon.service";

@Component({
  selector: 'app-leagues-matches-content',
  templateUrl: './leagues-matches-content.component.html',
  styleUrls: ['./leagues-matches-content.component.css']
})
export class LeaguesMatchesContentComponent implements OnChanges {
  @Input() selectedCountry: string = '';
  @Input() selectedSport: string = '';
  @Input() selectedLeague: string = '';
  gamesData: any;
  // selectedIdOfMatch: number = 0;
  @Output() idOfMatchSelected: EventEmitter<{ gameId:number }> = new EventEmitter();

  constructor(private http: HttpClient,
              public couponService: CouponService) {}

  formatDate(dateString: string): string {
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' };
    return new Date(dateString).toLocaleDateString('en-US', options);
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Обработка изменений входных данных
    if (changes['selectedSport'] || changes['selectedCountry'] || changes['selectedLeague']) {
      this.getDataFromBackend();
    }
  }

  getDataFromBackend() {
    // Формирование URL с использованием выбранных параметров
    const apiUrl = `http://localhost:8080/list/games/${this.selectedSport}/${this.selectedCountry}/${this.selectedLeague}`;

    // Выполнение GET-запроса
    this.http.get(apiUrl).subscribe({
      next: (data: any) => {
        this.gamesData = data;
        console.log('gamesData:', this.gamesData);

        this.gamesData.forEach((game: any) => {
          console.log('GAME', game.id, game.teamHome, game.teamAway);
          console.log('BETS', game.bets);
        });
      },
      error: (error) => {
        // Обработка ошибок
        console.error(error);
      }
    });
  }

  goToGamePage(gameId: number) {
    // this.selectedIdOfMatch = (this.selectedIdOfMatch === gameId) ? 0 : gameId;
    this.idOfMatchSelected.emit({gameId: gameId });
  }

  getBetValue(game: any, type: string): number | null {
    if (!Array.isArray(game.bets)) return null;

    const normalizedType = type.trim().toLowerCase();

    const bet = game.bets.find((b: any) =>
      String(b.type).trim().toLowerCase() === normalizedType
    );

    return bet ? bet.value : null;
  }
}
