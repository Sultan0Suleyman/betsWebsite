import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GreenEmptyNavBarComponent } from './green-empty-nav-bar.component';

describe('GreenEmptyNavBarComponent', () => {
  let component: GreenEmptyNavBarComponent;
  let fixture: ComponentFixture<GreenEmptyNavBarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GreenEmptyNavBarComponent]
    });
    fixture = TestBed.createComponent(GreenEmptyNavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
