import { TestBed } from '@angular/core/testing';

import { SecurityLogicService } from './security-logic.service';

describe('SecurityLogicService', () => {
  let service: SecurityLogicService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SecurityLogicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
