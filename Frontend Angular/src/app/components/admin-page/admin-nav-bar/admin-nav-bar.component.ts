import {Component, EventEmitter, Output} from '@angular/core';
import {AuthService} from "../../../services/AuthService/auth.service";

@Component({
  selector: 'app-admin-nav-bar',
  templateUrl: './admin-nav-bar.component.html',
  styleUrls: ['./admin-nav-bar.component.css']
})
export class AdminNavBarComponent {
  @Output() registerWorkerClicked: EventEmitter<void> = new EventEmitter<void>()
  @Output() listOfUsersClicked: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private authService:AuthService
  ) {
  }

  onListOfUsersClicked(): void{
    this.listOfUsersClicked.emit()
  }

  onRegisterWorkerClicked(): void{
    this.registerWorkerClicked.emit()
  }

  onLogout(): void{
    this.authService.logout();
  }

}
