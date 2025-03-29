import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminListOfUsersComponent } from './admin-list-of-users.component';

describe('AdminListOfUsersComponent', () => {
  let component: AdminListOfUsersComponent;
  let fixture: ComponentFixture<AdminListOfUsersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminListOfUsersComponent]
    });
    fixture = TestBed.createComponent(AdminListOfUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
