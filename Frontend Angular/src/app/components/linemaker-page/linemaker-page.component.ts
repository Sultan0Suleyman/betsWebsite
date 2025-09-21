import { Component } from '@angular/core';

@Component({
  selector: 'app-linemaker-page',
  templateUrl: './linemaker-page.component.html',
  styleUrls: ['./linemaker-page.component.css']
})
export class LinemakerPageComponent {
  isUnpublishedMatchesClicked = false;
  isLineMatchesClicked = false
  isLiveMatchesClicked = false
  isCreateMatchClicked = false

  onIsUnpublishedMatchesClicked(){
    this.isUnpublishedMatchesClicked = true;
    this.isLineMatchesClicked = false
    this.isLiveMatchesClicked = false
    this.isCreateMatchClicked = false
  }

  onIsLineMatchesClicked(){
    this.isUnpublishedMatchesClicked = false;
    this.isLineMatchesClicked = true
    this.isLiveMatchesClicked = false
    this.isCreateMatchClicked = false
  }

  onIsLiveMatchesClicked(){
    this.isUnpublishedMatchesClicked = false;
    this.isLineMatchesClicked = false
    this.isLiveMatchesClicked = true
    this.isCreateMatchClicked = false
  }

  onIsCreateMatchClicked(){
    this.isUnpublishedMatchesClicked = false;
    this.isLineMatchesClicked = false
    this.isLiveMatchesClicked = false
    this.isCreateMatchClicked = true
  }
}
