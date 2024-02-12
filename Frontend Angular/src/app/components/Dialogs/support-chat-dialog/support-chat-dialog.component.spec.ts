import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupportChatDialogComponent } from './support-chat-dialog.component';

describe('SupportChatDialogComponent', () => {
  let component: SupportChatDialogComponent;
  let fixture: ComponentFixture<SupportChatDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SupportChatDialogComponent]
    });
    fixture = TestBed.createComponent(SupportChatDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
