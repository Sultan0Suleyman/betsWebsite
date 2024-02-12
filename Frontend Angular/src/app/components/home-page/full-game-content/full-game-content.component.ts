import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CouponService} from "../../../services/CouponService/coupon.service";

@Component({
  selector: 'app-full-game-content',
  templateUrl: './full-game-content.component.html',
  styleUrls: ['./full-game-content.component.css']
})
export class FullGameContentComponent implements OnInit{
  @Input() selectedGameId: number = 0
  gameBetsData: any

  constructor(private http: HttpClient,
              public couponService: CouponService) {}

  ngOnInit(): void {
    this.getDataFromBackend()
  }

  getDataFromBackend() {
    // Формирование URL с использованием выбранных параметров
    const apiUrl = `http://localhost:8080/list/gameBets/${this.selectedGameId}`;

    // Выполнение GET-запроса
    this.http.get(apiUrl).subscribe({
      next:(data) => {
        // Обработка данных, полученных от бэкенда
        this.gameBetsData = data;
        console.log(this.gameBetsData);
      },
      error:(error) => {
        // Обработка ошибок
        console.error(error);
      }
  });
  }


}
