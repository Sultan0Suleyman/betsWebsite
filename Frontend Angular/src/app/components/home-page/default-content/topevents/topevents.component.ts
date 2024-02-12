import { Component } from '@angular/core';

@Component({
  selector: 'app-topevents',
  templateUrl: './topevents.component.html',
  styleUrls: ['./topevents.component.css']
})
export class TopeventsComponent {
  arrayOfTopEvents = [
    {nameOfEvent: "Barcelona1 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
    {nameOfEvent: "Barcelona2 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
    {nameOfEvent: "Barcelona3 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
    {nameOfEvent: "Barcelona4 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
    {nameOfEvent: "Barcelona5 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
    {nameOfEvent: "Barcelona6 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
    {nameOfEvent: "Barcelona7 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
    {nameOfEvent: "Barcelona8 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
    {nameOfEvent: "Barcelona9 - Real", date:new Date(2024,11,27),win1:1.3,draw:3,win2:4},
  ]
}
