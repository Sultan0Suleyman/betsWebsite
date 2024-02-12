import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BetHistoryContentComponent } from './bet-history-content.component';

describe('BetHistoryContentComponent', () => {
  let component: BetHistoryContentComponent;
  let fixture: ComponentFixture<BetHistoryContentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BetHistoryContentComponent]
    });
    fixture = TestBed.createComponent(BetHistoryContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
