import { AuthService } from "../../services/AuthService/auth.service";
import { Injectable } from "@angular/core";
import {Observable} from "rxjs";
import {RedirectService} from "../../services/RedirectService/redirect.service";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root',
})
export class DefaultGuard {
  constructor(private authService: AuthService,
              private redirectService:RedirectService,
              private router:Router
              ) {
  }

  canActivate(): Observable<boolean> | boolean {
    if (!this.authService.isAuthenticated()) {
      // Разрешаем доступ для неавторизованных пользователей
      return true;
    } else {
      // Авторизованные пользователи не могут получить доступ, перенаправляем их
      this.router.navigate(['/player/main-page']) // Замените '/player' на нужный путь
      return false;
    }
  }
}
