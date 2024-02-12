import { Injectable } from '@angular/core';
import {first, Observable} from 'rxjs';
import { map } from 'rxjs/operators';
import {AuthService} from "../../services/AuthService/auth.service";
import {SecurityLogicService} from "../../services/SecurityLogicService/security-logic.service";

@Injectable({
  providedIn: 'root',
})
export class PlayerGuard {
  constructor(private securityLogicService: SecurityLogicService,
              private authService: AuthService) {
    if(this.authService.isAuthenticated())this.securityLogicService.ngOnInit()
  }
  canActivate(): Observable<boolean> {
    return this.authService.getCurrentObservableUser().pipe(
      map(user => !!user && user.role === 'ROLE_PLAYER'),
      first() // или switchMap, в зависимости от ваших потребностей
    );
  }
}
