import {Component, DoCheck, OnChanges} from '@angular/core';
import {DialogButtonsService} from "../../services/DialogButtonsService/dialog-buttons.service";
import {CouponService} from "../../services/CouponService/coupon.service";
import {SecurityLogicService} from "../../services/SecurityLogicService/security-logic.service";
import {AuthService} from "../../services/AuthService/auth.service";

@Component({
  selector: 'app-home-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements DoCheck{
  selectedCountry: string = ''
  selectedSport: string = ''
  selectedLeague: string = ''
  selectedGameId: number = 0
  couponCount: number = 0
  isBetHistoryClicked = false
  userId = this.authService.getCurrentUser().value.username
  isAuthenticated! : boolean

  constructor(private dialogButtonsService: DialogButtonsService,
              private couponService: CouponService,
              private securityLogicService: SecurityLogicService,
              private authService: AuthService) {
    if(this.authService.isAuthenticated())this.securityLogicService.ngOnInit()
  }

  onCountrySportSelected(event: { country: string, sport: string, league: string}) {
    this.selectedCountry = event.country
    this.selectedSport = event.sport
    this.selectedLeague = event.league
    this.selectedGameId = 0
    this.isBetHistoryClicked = false
  }

  onIdOfMatchSelected(event: { gameId: number }){
    this.selectedGameId = event.gameId
  }

  openCouponDialog(): void {
    this.dialogButtonsService.openCouponDialog()
  }

  openChatDialog(): void {
    this.dialogButtonsService.openChatDialog()
  }

  ngDoCheck(): void {
    this.couponCount = this.couponService.getCouponLength()
    this.isAuthenticated = this.authService.isAuthenticated()
  }

  onBetHistoryClicked() {
    this.isBetHistoryClicked = true
  }
}
