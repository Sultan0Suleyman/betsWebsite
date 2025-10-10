import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinemakerSetOddsMatchComponent } from './linemaker-set-odds-match.component';

describe('LinemakerSetOddsMatchComponent', () => {
  let component: LinemakerSetOddsMatchComponent;
  let fixture: ComponentFixture<LinemakerSetOddsMatchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinemakerSetOddsMatchComponent]
    });
    fixture = TestBed.createComponent(LinemakerSetOddsMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
