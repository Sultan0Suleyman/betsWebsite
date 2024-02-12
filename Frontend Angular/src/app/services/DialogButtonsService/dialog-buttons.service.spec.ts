import { TestBed } from '@angular/core/testing';

import { DialogButtonsService } from './dialog-buttons.service';

describe('DialogButtonsService', () => {
  let service: DialogButtonsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DialogButtonsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
