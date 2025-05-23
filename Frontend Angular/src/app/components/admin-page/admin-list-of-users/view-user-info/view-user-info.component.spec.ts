import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewUserInfoComponent } from './view-user-info.component';

describe('ViewUserInfoComponent', () => {
  let component: ViewUserInfoComponent;
  let fixture: ComponentFixture<ViewUserInfoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewUserInfoComponent]
    });
    fixture = TestBed.createComponent(ViewUserInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
