import { Injectable } from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {CouponBetsDialogComponent} from "../../components/Dialogs/coupon-bets-dialog/coupon-bets-dialog.component";
import {SupportChatDialogComponent} from "../../components/Dialogs/support-chat-dialog/support-chat-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class DialogButtonsService {

  constructor(private dialog: MatDialog) {}

  openCouponDialog(): void {
    this.dialog.open(CouponBetsDialogComponent, {
      width: '350px', // Adjust the width as needed
      height:'400px'
    });
  }

  openChatDialog(): void {
    this.dialog.open(SupportChatDialogComponent, {
      width: '300px', // Adjust the width as needed
      height:'400px'
    });
  }
}
