import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinemakerUnpublishedMatchesComponent } from './linemaker-unpublished-matches.component';

describe('LinemakerUnpublishedMatchesComponent', () => {
  let component: LinemakerUnpublishedMatchesComponent;
  let fixture: ComponentFixture<LinemakerUnpublishedMatchesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinemakerUnpublishedMatchesComponent]
    });
    fixture = TestBed.createComponent(LinemakerUnpublishedMatchesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
