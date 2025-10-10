import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../AuthService/auth.service";

@Injectable({
  providedIn: 'root'
})
export class RedirectService {
  constructor(private router: Router,
              private authService: AuthService) {}

  handleRoleBasedRedirect(role: string): void {
    console.log('Decoded role:', role); // Проверим, что роль правильно декодирована
    switch (role) {
      case 'ROLE_PLAYER':
        this.router.navigate(['/player/main-page']);
        break;
      case 'ROLE_LINEMAKER':
        const username = this.authService.getCurrentUser().getValue().username;
        console.log('Logging in as linemaker, username:', username);

        this.authService.getCurrentUsersNameSurname(username)
          .subscribe({
            next: (data) => {
              console.log('Received name/surname from backend:', data);
            },
            error: (err) => {
              console.error('Failed to get name/surname:', err);
            }
          });

        this.router.navigate(['/linemaker']);
        break;
      case 'ROLE_SUPPORT':
        this.router.navigate(['/support']);
        break;
      case 'ROLE_MAIN_ADMIN':
        this.router.navigate(['/admin']);
        break;
    }
  }
}
