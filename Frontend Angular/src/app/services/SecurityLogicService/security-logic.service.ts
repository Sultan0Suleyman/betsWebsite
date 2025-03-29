import {Injectable, OnInit} from '@angular/core';
import {AuthService} from "../AuthService/auth.service";

@Injectable({
  providedIn: 'root'
})
export class SecurityLogicService implements OnInit{
  private readonly inactivityThreshold = 15 * 60 * 1000; // 15 минут в миллисекундах
  private readonly lastActivityKey = 'lastActivity';

  constructor(
    private authService:AuthService
  ) {

  }
  refreshAccessToken() {
    const currentTime = Date.now();
    const storedDate = localStorage.getItem('dateOfRefresh');

    if (storedDate) {
      const dateOfRefresh = parseInt(storedDate, 10); // Преобразование в число для сравнения
      if (currentTime >= dateOfRefresh) {
        this.authService.refreshTokenIfNeeded();
      }
    }
  }
  updateLastActivity() {
    localStorage.setItem(this.lastActivityKey, Date.now().toString());
  }

  checkInactivity() {
    const lastActivity = parseInt(localStorage.getItem(this.lastActivityKey) || '0', 10);
    if(lastActivity){
      const currentTime = Date.now();
      if (currentTime - lastActivity > this.inactivityThreshold) {
        console.log('User came from nonactive period');
        this.authService.refreshRefreshToken()
        this.updateLastActivity()
      }
    }
  }

  ngOnInit(): void {
    setInterval(() => {
      this.updateLastActivity()
    }, 60*1000);
    setInterval(() => {
      this.refreshAccessToken()
    }, 7.5*60*1000);
    // Периодическая проверка неактивности
    this.checkInactivity()
    this.refreshAccessToken()
  }

}
