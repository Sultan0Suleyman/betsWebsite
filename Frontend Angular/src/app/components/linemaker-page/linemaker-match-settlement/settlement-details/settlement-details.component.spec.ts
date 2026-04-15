import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettlementDetailsComponent } from './settlement-details.component';

describe('SettlementDetailsComponent', () => {
  let component: SettlementDetailsComponent;
  let fixture: ComponentFixture<SettlementDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SettlementDetailsComponent]
    });
    fixture = TestBed.createComponent(SettlementDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
