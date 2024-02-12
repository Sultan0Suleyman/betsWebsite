import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor() {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const accessToken = this.getAccessTokenFromLocalStorage()
    const refreshToken = this.getRefreshTokenFromLocalStorage()

    if (accessToken) {
      // Если токен действителен, добавьте его к заголовкам запроса
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
    }
    if(refreshToken){
      request = request.clone({
        setHeaders: {
          RefreshToken: `Bearer ${refreshToken}`,
        },
      });
    }
    return next.handle(request);
  }

  private getAccessTokenFromLocalStorage(): string | null {
    return localStorage.getItem('accessToken');
  }
  private getRefreshTokenFromLocalStorage(): string|null{
    return localStorage.getItem('refreshToken');
  }
}
