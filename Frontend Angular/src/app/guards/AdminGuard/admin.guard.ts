import { Injectable } from '@angular/core';
import {first, Observable} from 'rxjs';
import { map } from 'rxjs/operators';
import {AuthService} from "../../services/AuthService/auth.service";
import {SecurityLogicService} from "../../services/SecurityLogicService/security-logic.service";

@Injectable({
  providedIn: 'root',
})
export class AdminGuard {
  constructor(private authService: AuthService,
              private securityLogicService: SecurityLogicService
              ) {
    if(this.authService.isAuthenticated())this.securityLogicService.ngOnInit()
  }
  canActivate(): Observable<boolean> {
    return this.authService.getCurrentObservableUser().pipe(
      map(user => !!user && user.role === 'ROLE_MAIN_ADMIN'),
      first() // или switchMap, в зависимости от ваших потребностей
    );
  }
}
