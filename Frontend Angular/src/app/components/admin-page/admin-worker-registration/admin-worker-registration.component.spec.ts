import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminWorkerRegistrationComponent } from './admin-worker-registration.component';

describe('AdminWorkerRegistrationComponent', () => {
  let component: AdminWorkerRegistrationComponent;
  let fixture: ComponentFixture<AdminWorkerRegistrationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminWorkerRegistrationComponent]
    });
    fixture = TestBed.createComponent(AdminWorkerRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
