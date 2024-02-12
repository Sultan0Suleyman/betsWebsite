import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { JwtDecodeService } from '../JwtDecodeService/jwt-decode.service';
import {ActivationStart, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any> = new BehaviorSubject<any>({});
  private token: string | null = null;
  private lastRefreshedToken: string | null = null;

  private readonly loginUrl = 'http://localhost:8080/login-endpoint';
  private readonly refreshAccessTokenUrl = 'http://localhost:8080/jwt-refresh-token-logic/refresh-access-token-endpoint';
  private readonly refreshRefreshTokenUrl = 'http://localhost:8080/jwt-refresh-token-logic/check-refresh-token-endpoint';
  private readonly revokeRefreshTokenUrl = 'http://localhost:8080/jwt-refresh-token-logic/revoke-refresh-token-endpoint';


  constructor(
    private http: HttpClient,
    private jwtDecodeService: JwtDecodeService,
    private router: Router,
  ) {
    console.log(localStorage.getItem('accessToken'))
    console.log(localStorage.getItem('refreshToken'))
    this.loadUserFromLocalStorage();
    console.log('User loaded');

    this.router.events.subscribe(event => {
      if (event instanceof ActivationStart) {
        if (this.isAuthenticated()) {
          const currentDate = new Date();
          const storedDate = localStorage.getItem('dateOfLogout');
          if (storedDate) {
            const dateOfLogout = new Date(storedDate);
            if (currentDate >= dateOfLogout) {
              this.logout();
            }
          }
        }
      }
    });
  }
  public login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post(this.loginUrl, credentials).pipe(
      map((response:any) => {
        if (response && 'accessToken' in response && 'refreshToken' in response) {
          this.setLoggedInUser(response);
          localStorage.setItem('refreshToken',response.refreshToken)
        }
        return response;
      }),
      catchError(error => {
        console.error('Login failed:', error);
        if (this.isAuthenticated()) {
          this.logout();
        }
        return throwError(() => new Error('Wrong login or password'));
      })
    );
  }

  public refreshTokenIfNeeded(): void {
    console.log("refreshed Access Token")
    const token = localStorage.getItem('accessToken');
    if (token) {
      this.http.post(this.refreshAccessTokenUrl, {}).pipe(
        map((response: any) => this.handleRefreshResponse(response)),
        catchError(error => this.handleError(error))
      ).subscribe();
    }
  }
  public refreshRefreshToken(): void {
    console.log("refreshed Refresh Token")
    const token = localStorage.getItem('refreshToken');
    if (token) {
      this.http.post(this.refreshRefreshTokenUrl, {}).pipe(
        map((response: any) => this.handleRefreshResponse(response)),
        catchError(error => this.handleError(error))
      ).subscribe();
    }
  }
  private loadUserFromLocalStorage(): void {
    const user = localStorage.getItem('user');
    if (user) {
      const parsedUser = JSON.parse(user);
      this.currentUserSubject.next(parsedUser);
    }
  }
  private saveTokenToLocalStorage(token: string): void {
    this.token = token;
    localStorage.setItem('accessToken', token);
  }

  private handleError(error: any): Observable<never> {
    console.error('API request failed:', error);
    if(this.isAuthenticated()){
      this.logout()
    }
    // Возможно, здесь можно предоставить пользователю опцию обработки ошибки.
    // Например, бросить ошибку (throw) или вывести сообщение об ошибке.
    return throwError(() => new Error('Something went wrong. Please try again later.'));
  }

  private setLoggedInUser(response: any): void {
    const decodedToken = this.jwtDecodeService.decodeJwt(response.accessToken);
    const user = {
      username: decodedToken.sub,
      role: decodedToken.role ? decodedToken.role[0] : 'defaultRole',
    };
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUserSubject.next(user);
    this.saveTokenToLocalStorage(response.accessToken);
  }

  public getToken(): string | null {
    return this.token;
  }

  public isAuthenticated(): boolean {
    const user = this.currentUserSubject.value;
    return user && Object.keys(user).length > 0;
  }


  private handleRefreshResponse(response: any): void {
    if (this.lastRefreshedToken === response.accessToken) {
      return;
    }
    if(response.refreshToken!==undefined)localStorage.setItem('refreshToken',response.refreshToken)
    this.lastRefreshedToken = response.accessToken;
    this.setLoggedInUser(response);
    let dateOfRefresh = new Date()
    dateOfRefresh.setTime(new Date().getTime()+14*60*1000)
    localStorage.setItem('dateOfRefresh', dateOfRefresh.toString())
  }

  public logout(): void {
    console.log('Logging out');
    localStorage.removeItem('user');
    localStorage.removeItem('accessToken');
    const checkRefreshToken = localStorage.getItem('refreshToken');
    if (checkRefreshToken) {//revoke refresh token
      this.http.post(this.revokeRefreshTokenUrl, {}).pipe()
      .subscribe();
    }
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('dateOfLogout');
    localStorage.removeItem('dateOfRefresh');
    localStorage.removeItem('lastActivity');
    this.currentUserSubject.next({});
    this.token = null;
    this.router.navigate(['/player/main-page']);
  }

  public getCurrentObservableUser(): Observable<any> {
    return this.currentUserSubject.asObservable();
  }

  public getCurrentUser(){
    return this.currentUserSubject
  }
}
