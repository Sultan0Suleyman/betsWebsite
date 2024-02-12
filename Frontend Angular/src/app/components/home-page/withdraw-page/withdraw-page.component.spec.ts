import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WithdrawPageComponent } from './withdraw-page.component';

describe('WithdrawPageComponent', () => {
  let component: WithdrawPageComponent;
  let fixture: ComponentFixture<WithdrawPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WithdrawPageComponent]
    });
    fixture = TestBed.createComponent(WithdrawPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
