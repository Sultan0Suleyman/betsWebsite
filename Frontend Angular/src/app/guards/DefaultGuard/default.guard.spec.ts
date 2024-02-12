import { TestBed } from '@angular/core/testing';
import { DefaultGuard } from './default.guard';
import { RouterTestingModule } from '@angular/router/testing';

describe('DefaultGuard', () => {
  let guard: DefaultGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule], // Добавляем RouterTestingModule для работы с маршрутами
      providers: [DefaultGuard],
    });
    guard = TestBed.inject(DefaultGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
