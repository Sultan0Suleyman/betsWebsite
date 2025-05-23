import {Component, OnInit} from '@angular/core';
import {UserService} from "../adminServices/UsersService/user.service";
import {User} from "../models/user";
import {MatDialog} from "@angular/material/dialog";
import {ViewUserInfoComponent} from "./view-user-info/view-user-info.component";
import {HttpClient} from "@angular/common/http";
import { UserDetailsResponse } from '../models/user-info';


@Component({
  selector: 'app-admin-list-of-users',
  templateUrl: './admin-list-of-users.component.html',
  styleUrls: ['./admin-list-of-users.component.css']
})

export class AdminListOfUsersComponent implements OnInit {
  users: User[] = []; // Список пользователей

  constructor(private userService: UserService,
              private dialog: MatDialog,
              private http: HttpClient) { }

  ngOnInit(): void {
    this.loadUsers(); // Инициализация списка пользователей
  }

  // Метод для получения всех пользователей
  loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (data: User[]) => {
        this.users = data; // Присваиваем данные в users
      },
      error: (error) => {
        console.error('Error fetching users', error); // Обработка ошибок
      }
    });
  }

  onRoleButton(role: string) :void{
    this.userService.getUsersByRole(role).subscribe({
      next:(data: User[]) => {
        this.users = data // Присваиваем данные в users
      },
      error:(error) => {
        console.error('Error fetching users', error) // Обработка ошибок
      }}
    )
  }


  onShowAllButton() :void{
    this.loadUsers()
  }

  // Метод для удаления пользователя
  deleteUser(userId: number): void {
    this.userService.deleteUser(userId).subscribe({
      next: (response) => {
        console.log('Delete successful', response);
        alert('User was successfully deleted.');
        this.loadUsers(); // Обновляем список после удаления
      },
      error: (error) => {
        console.error('Error deleting user', error);
        alert('Failed to delete user. Please try again.');
      }
    });
  }

  // Метод для просмотра информации
  viewUserDetails(userId: number): void {
    this.http.get<UserDetailsResponse>(`http://localhost:8080/admin/user/info/${userId}`).subscribe({
      next:(data) =>{
        console.log(data)
        this.dialog.open(ViewUserInfoComponent, {
          data,
          width: '350px',
          height: '480px'
        })
      },
      error: (error) => {
        console.error('Error loading user data:', error)
        alert('Failed to load user information. Please try again')
      }})
  }
}
