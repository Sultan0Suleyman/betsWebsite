import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { linemakerGuard } from './linemaker.guard';

describe('linemakerGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => linemakerGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
