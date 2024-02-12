import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class RedirectService {
  constructor(private router: Router) {}

  handleRoleBasedRedirect(role: string): void {
    console.log('Decoded role:', role); // Проверим, что роль правильно декодирована
    switch (role) {
      case 'ROLE_PLAYER':
        this.router.navigate(['/player/main-page']);
        break;
      case 'ROLE_LINEMAKER':
        this.router.navigate(['/linemaker']);
        break;
      case 'ROLE_SUPPORT':
        this.router.navigate(['/support']);
        break;
      case 'ROLE_ADMIN':
        this.router.navigate(['/admin']);
        break;
    }
  }
}
