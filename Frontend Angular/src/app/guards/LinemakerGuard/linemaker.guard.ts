import {AuthService} from "../../services/AuthService/auth.service";
import {SecurityLogicService} from "../../services/SecurityLogicService/security-logic.service";
import {first, Observable} from "rxjs";
import {map} from "rxjs/operators";
import {Injectable} from "@angular/core";


@Injectable({
  providedIn: 'root' // Позволяет автоматически регистрировать сервис
})
export class LinemakerGuard {
  constructor(private authService: AuthService,
              private securityLogicService: SecurityLogicService
  ) {
    if(this.authService.isAuthenticated())this.securityLogicService.ngOnInit()
  }
  canActivate(): Observable<boolean> {
    return this.authService.getCurrentObservableUser().pipe(
      map(user => !!user && user.role === 'ROLE_LINEMAKER'),
      first() // или switchMap, в зависимости от ваших потребностей
    )
  }
}
