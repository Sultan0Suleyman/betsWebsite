import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinemakerPageComponent } from './linemaker-page.component';

describe('LinemakerPageComponent', () => {
  let component: LinemakerPageComponent;
  let fixture: ComponentFixture<LinemakerPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinemakerPageComponent]
    });
    fixture = TestBed.createComponent(LinemakerPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
