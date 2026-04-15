import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinemakerManageLineMatchComponent } from './linemaker-manage-line-match.component';

describe('LinemakerManageLineMatchComponent', () => {
  let component: LinemakerManageLineMatchComponent;
  let fixture: ComponentFixture<LinemakerManageLineMatchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinemakerManageLineMatchComponent]
    });
    fixture = TestBed.createComponent(LinemakerManageLineMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
