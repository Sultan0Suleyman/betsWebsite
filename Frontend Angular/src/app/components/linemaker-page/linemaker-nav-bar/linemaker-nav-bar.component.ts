import {Component, EventEmitter, Output} from '@angular/core';
import {AuthService} from "../../../services/AuthService/auth.service";

@Component({
  selector: 'app-linemaker-nav-bar',
  templateUrl: './linemaker-nav-bar.component.html',
  styleUrls: ['./linemaker-nav-bar.component.css']
})
export class LinemakerNavBarComponent {
  @Output() unpublishedMatchesClicked: EventEmitter<void> = new EventEmitter<void>()
  @Output() lineMatchesClicked: EventEmitter<void> = new EventEmitter<void>()
  @Output() liveMatchesClicked: EventEmitter<void> = new EventEmitter<void>()
  @Output() createMatchClicked: EventEmitter<void> = new EventEmitter<void>()

  constructor(
    private authService:AuthService
  ) {
  }

  onUnpublishedMatchesClicked(): void{
    this.unpublishedMatchesClicked.emit()
  }

  onLineMatchesClicked(): void{
    this.lineMatchesClicked.emit()
  }

  onLiveMatchesClicked(): void{
    this.liveMatchesClicked.emit()
  }

  onCreateMatchClicked(): void{
    this.createMatchClicked.emit()
  }

  onLogout(): void{
    this.authService.logout();
  }
}
