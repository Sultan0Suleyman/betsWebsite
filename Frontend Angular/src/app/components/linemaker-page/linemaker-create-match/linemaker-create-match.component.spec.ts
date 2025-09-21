import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinemakerCreateMatchComponent } from './linemaker-create-match.component';

describe('LinemakerCreateMatchComponent', () => {
  let component: LinemakerCreateMatchComponent;
  let fixture: ComponentFixture<LinemakerCreateMatchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinemakerCreateMatchComponent]
    });
    fixture = TestBed.createComponent(LinemakerCreateMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
