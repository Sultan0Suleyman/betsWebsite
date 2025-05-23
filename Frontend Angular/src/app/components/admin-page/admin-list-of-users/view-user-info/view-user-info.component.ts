import {Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {UserDetailsResponse} from "../../models/user-info";

@Component({
  selector: 'app-view-user-info',
  templateUrl: './view-user-info.component.html',
  styleUrls: ['./view-user-info.component.css']
})
export class ViewUserInfoComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: UserDetailsResponse
  ) {}

}
