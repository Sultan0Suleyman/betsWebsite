import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinemakerLineMatchesComponent } from './linemaker-line-matches.component';

describe('LinemakerLineMatchesComponent', () => {
  let component: LinemakerLineMatchesComponent;
  let fixture: ComponentFixture<LinemakerLineMatchesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinemakerLineMatchesComponent]
    });
    fixture = TestBed.createComponent(LinemakerLineMatchesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
