import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {User} from "../../models/user";
import { environment } from 'src/environments/environment';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrlGetAllUsers = `${environment.apiUrl}/admin/list/users`; // URL вашего API
  private apiUrlDeleteUser = `${environment.apiUrl}/admin/delete`

  constructor(private http: HttpClient) { }

  // Метод для получения списка пользователей с бэкенда
  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrlGetAllUsers);
  }

  getUsersByRole(role: string): Observable<User[]>{
    return this.http.get<User[]>(`${this.apiUrlGetAllUsers}/${role}`)
  }

  deleteUser(userId: number): Observable<any> {
    // Вывод окна подтверждения перед удалением
    const confirmed = window.confirm('Are you sure you want to delete user with id ' + userId);

    if (confirmed) {
      console.log(`Attempting to delete user with id: ${userId}`);
      return this.http.delete(`${this.apiUrlDeleteUser}/${userId}`);
    } else {
      console.log('User deletion canceled.');
      return new Observable(observer => observer.complete()); // Возвращаем пустой Observable
    }
  }
}
