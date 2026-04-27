import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError, switchMap, map } from "rxjs/operators";
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {
  private readonly defaultUrl = environment.apiUrl;
  csrfToken: string = ""

  constructor(private http: HttpClient) { }

  getCsrf(): Observable<any> {
    return this.http.get(this.defaultUrl + "/csrf/token", { withCredentials: true })
      .pipe(
        map((data: any) => {
          this.csrfToken = data?.token;
          return data;
        }),
        catchError(error => {
          console.error('Error while getting CSRF token:', error);
          return throwError(() => new Error('CSRF token error'));
        })
      );
  }

  get<T>(url: string): Observable<T> {
    return this.http.get<T>(this.defaultUrl + url, { withCredentials: true });
  }

  post<T>(url: string, data: any): Observable<T> {
    return this.getCsrf().pipe(
      switchMap(() => {
        const headers = new HttpHeaders({
          "X-CSRF-TOKEN": this.csrfToken,
          "Content-Type": "application/json"
        });
        return this.http.post<T>(this.defaultUrl + url, data, { withCredentials: true, headers });
      }),
      catchError(error => {
        console.error('Error while processing request:', error);
        return throwError(() => new Error('Request error'));
      })
    );
  }

  put<T>(url: string, data: any): Observable<T> {
    return this.getCsrf().pipe(
      switchMap(() => {
        const headers = new HttpHeaders({
          "X-CSRF-TOKEN": this.csrfToken,
          "Content-Type": "application/json"
        });
        return this.http.put<T>(this.defaultUrl + url, data, { withCredentials: true, headers });
      }),
      catchError(error => {
        console.error('Error while processing request:', error);
        return throwError(() => new Error('Request error'));
      })
    );
  }
}
