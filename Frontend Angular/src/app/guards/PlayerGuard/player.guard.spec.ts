import { TestBed } from '@angular/core/testing';
import { PlayerGuard } from './player.guard';
import { RouterTestingModule } from '@angular/router/testing';

describe('PlayerGuard', () => {
  let guard: PlayerGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule], // Добавляем RouterTestingModule для работы с маршрутами
      providers: [PlayerGuard],
    });
    guard = TestBed.inject(PlayerGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
