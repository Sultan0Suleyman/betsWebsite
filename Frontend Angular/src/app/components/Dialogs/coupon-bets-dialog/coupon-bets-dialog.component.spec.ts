import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CouponBetsDialogComponent } from './coupon-bets-dialog.component';

describe('CouponBetsDialogComponent', () => {
  let component: CouponBetsDialogComponent;
  let fixture: ComponentFixture<CouponBetsDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CouponBetsDialogComponent]
    });
    fixture = TestBed.createComponent(CouponBetsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
