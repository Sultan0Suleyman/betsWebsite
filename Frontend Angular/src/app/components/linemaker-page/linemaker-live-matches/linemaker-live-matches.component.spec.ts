import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinemakerLiveMatchesComponent } from './linemaker-live-matches.component';

describe('LinemakerLiveMatchesComponent', () => {
  let component: LinemakerLiveMatchesComponent;
  let fixture: ComponentFixture<LinemakerLiveMatchesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinemakerLiveMatchesComponent]
    });
    fixture = TestBed.createComponent(LinemakerLiveMatchesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
