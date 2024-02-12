import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaguesMatchesContentComponent } from './leagues-matches-content.component';

describe('LeaguesMatchesContentComponent', () => {
  let component: LeaguesMatchesContentComponent;
  let fixture: ComponentFixture<LeaguesMatchesContentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LeaguesMatchesContentComponent]
    });
    fixture = TestBed.createComponent(LeaguesMatchesContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
