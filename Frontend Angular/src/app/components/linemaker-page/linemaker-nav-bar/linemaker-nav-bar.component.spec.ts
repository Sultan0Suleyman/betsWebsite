import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinemakerNavBarComponent } from './linemaker-nav-bar.component';

describe('LinemakerNavBarComponent', () => {
  let component: LinemakerNavBarComponent;
  let fixture: ComponentFixture<LinemakerNavBarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinemakerNavBarComponent]
    });
    fixture = TestBed.createComponent(LinemakerNavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
