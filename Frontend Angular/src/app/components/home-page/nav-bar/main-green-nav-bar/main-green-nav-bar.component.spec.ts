import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainGreenNavBarComponent } from './main-green-nav-bar.component';

describe('GreenDefaultUnregistratedNavBarComponent', () => {
  let component: MainGreenNavBarComponent;
  let fixture: ComponentFixture<MainGreenNavBarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MainGreenNavBarComponent]
    });
    fixture = TestBed.createComponent(MainGreenNavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
