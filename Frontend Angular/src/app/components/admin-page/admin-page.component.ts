import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.css']
})
export class AdminPageComponent {
  isListOfUsersClicked = false
  isRegisterWorkerClicked = false

  onIsListOfUsersClicked(){
    this.isListOfUsersClicked = true
    this.isRegisterWorkerClicked = false
  }

  onIsRegisterWorkerClicked(){
    this.isRegisterWorkerClicked = true
    this.isListOfUsersClicked = false
  }
}
