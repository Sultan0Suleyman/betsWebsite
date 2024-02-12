import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FullGameContentComponent } from './full-game-content.component';

describe('FullGameContentComponent', () => {
  let component: FullGameContentComponent;
  let fixture: ComponentFixture<FullGameContentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FullGameContentComponent]
    });
    fixture = TestBed.createComponent(FullGameContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
