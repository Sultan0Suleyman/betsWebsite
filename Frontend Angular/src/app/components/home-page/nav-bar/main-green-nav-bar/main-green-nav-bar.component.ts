import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import { AuthService } from "../../../../services/AuthService/auth.service";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-main-green-nav-bar',
  templateUrl: './main-green-nav-bar.component.html',
  styleUrls: ['./main-green-nav-bar.component.css']
})
export class MainGreenNavBarComponent implements OnInit{
  @Output() betHistoryClicked: EventEmitter<void> = new EventEmitter<void>()

  playerId: number = 0
  playerBalance: number = 0

  constructor(public authService: AuthService,
              private http:HttpClient
              ) {
  }

  ngOnInit(): void {
    if(this.authService.isAuthenticated()){
      this.playerId=this.authService.getCurrentUser().value.username
      this.getPlayerBalanceFromBackend()
    }
  }

  getPlayerBalanceFromBackend(){
    console.log("Request to get PlayerBalance")
    const apiUrl = `http://localhost:8080/player/data/balance/${this.playerId}`
    // Выполнение GET-запроса
    this.http.get<number>(apiUrl).subscribe({
      next: (data: number) => {
        // Обработка данных, полученных от бэкенда
        this.playerBalance = data
      },
      error: (error) => {
        // Обработка ошибок
        console.error(error);
        this.authService.logout()
      }
    });
  }
  refreshBalance() {
    this.getPlayerBalanceFromBackend()
    const refreshButton = document.querySelector('.refresh-button')
    if (refreshButton) {
      refreshButton.classList.add('rotate-animation')
      setTimeout(() => {
        refreshButton.classList.remove('rotate-animation')
      }, 300); // Задаем тот же период, что и в CSS-анимации
    }
  }

  onBetHistoryClicked(): void {
    // Вызовите событие при нажатии "Bet history"
    this.betHistoryClicked.emit()
  }
}
