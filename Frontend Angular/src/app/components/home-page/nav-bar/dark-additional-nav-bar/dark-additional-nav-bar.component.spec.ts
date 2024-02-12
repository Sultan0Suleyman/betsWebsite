import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DarkAdditionalNavBarComponent } from './dark-additional-nav-bar.component';

describe('DarkAdditionalNavBarComponent', () => {
  let component: DarkAdditionalNavBarComponent;
  let fixture: ComponentFixture<DarkAdditionalNavBarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DarkAdditionalNavBarComponent]
    });
    fixture = TestBed.createComponent(DarkAdditionalNavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
