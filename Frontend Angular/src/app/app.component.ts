import { Component } from '@angular/core';
import {HttpClientService} from "./services/HttpClientService/http-client.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'ProjectBK';

  constructor(
    // private http:HttpClientService
  ) {
  }

  // ngOnInit():void{
  //   this.http.getCsrf()
  // }
}
