import { Injectable } from '@angular/core';
import {AbstractControl, ValidatorFn} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  // Валидатор для даты (не может быть в будущем)
  static issueDateValidator(control: AbstractControl) {
    const selectedDate = new Date(control.value);
    const today = new Date();

    if (selectedDate > today) {
      return { futureDate: true };
    }
    return null;
  }

  static passwordMatchValidator(control: AbstractControl) {
    const password1 = control.get('password1');
    const password2 = control.get('password2');

    if (password1?.value !== password2?.value) {
      return { passwordMismatch: true };
    }
    return null;
  }
}
