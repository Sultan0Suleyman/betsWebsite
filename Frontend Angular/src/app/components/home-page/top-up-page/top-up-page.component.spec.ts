import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopUpPageComponent } from './top-up-page.component';

describe('TopUpPageComponent', () => {
  let component: TopUpPageComponent;
  let fixture: ComponentFixture<TopUpPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TopUpPageComponent]
    });
    fixture = TestBed.createComponent(TopUpPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
