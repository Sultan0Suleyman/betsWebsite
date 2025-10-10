import { Component } from '@angular/core';

@Component({
  selector: 'app-linemaker-page',
  templateUrl: './linemaker-page.component.html',
  styleUrls: ['./linemaker-page.component.css']
})
export class LinemakerPageComponent {
  isUnpublishedMatchesClicked = false;
  isLineMatchesClicked = false;
  isLiveMatchesClicked = false;
  isCreateMatchClicked = false;
  isSetOddsClicked = false;

  // Данные для передачи в компонент настройки коэффициентов
  selectedMatchId: number | null = null;

  onIsUnpublishedMatchesClicked(){
    this.resetAllStates();
    this.isUnpublishedMatchesClicked = true;
  }

  onIsLineMatchesClicked(){
    this.resetAllStates();
    this.isLineMatchesClicked = true;
  }

  onIsLiveMatchesClicked(){
    this.resetAllStates();
    this.isLiveMatchesClicked = true;
  }

  onIsCreateMatchClicked(){
    this.resetAllStates();
    this.isCreateMatchClicked = true;
  }

  // Новый метод для открытия настройки коэффициентов
  onSetOddsClicked(matchId: number){
    this.resetAllStates();
    this.isSetOddsClicked = true;
    this.selectedMatchId = matchId;
  }

  // Вспомогательный метод для сброса всех состояний
  private resetAllStates(){
    this.isUnpublishedMatchesClicked = false;
    this.isLineMatchesClicked = false;
    this.isLiveMatchesClicked = false;
    this.isCreateMatchClicked = false;
    this.isSetOddsClicked = false;
    this.selectedMatchId = null;
  }

  // Метод для возврата к списку неопубликованных матчей
  goBackToUnpublishedMatches(){
    this.onIsUnpublishedMatchesClicked();
  }
}
