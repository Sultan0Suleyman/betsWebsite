import {Component, OnInit} from '@angular/core';
import {UserService} from "../adminServices/UsersService/user.service";
import {User} from "../models/user";

@Component({
  selector: 'app-admin-list-of-users',
  templateUrl: './admin-list-of-users.component.html',
  styleUrls: ['./admin-list-of-users.component.css']
})
export class AdminListOfUsersComponent implements OnInit {
  users: User[] = []; // Список пользователей

  constructor(private userService: UserService) { }

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
    console.log(`Viewing details for user with ID: ${userId}`)
  }
}
