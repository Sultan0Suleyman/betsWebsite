import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import { AbstractControl } from '@angular/forms';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";

@Component({
  selector: 'app-registration-page',
  templateUrl: './registration-page.component.html',
  styleUrls: ['./registration-page.component.css']
})
export class RegistrationPageComponent {
  currentDate = new Date()
  submissionMessage:String = ''
  myForm: FormGroup = new FormGroup({

  })
  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient, // Inject the HttpClient
    private router: Router
  ) {}
  submitForm(): void {
    if (this.myForm.valid) {
      const formData = { ...this.myForm.value };
      formData.password = formData.password1;  // Устанавливаем значение password

      // Если нужно удалить password1 и password2, сделаем это после установки значения password
      delete formData.password1;
      delete formData.password2;

      if (formData.password !== undefined) {
        // Если пароль не равен undefined, отправляем форму
        this.http.post('http://localhost:8080/players', formData)
          .subscribe({next:(response: any) => {
            console.log('Form data sent successfully!', response);
            this.submissionMessage = response.message || 'You have successfully registered'; // извлекаем сообщение об успешной регистрации из ответа или используем стандартное сообщение
            this.router.navigate(['/home/login'])
          }, error:(error) => {
            console.error('Error occurred while sending form data:', error);
            if (error.error && error.error.error) {
              this.submissionMessage = error.error.error; // извлекаем сообщение об ошибке из ответа
            } else {
              this.submissionMessage = 'An error occurred on the server.'; // используем стандартное сообщение об ошибке
            }
          }});
      }
    }
  }
  ngOnInit(): void {
    this.myForm = this.formBuilder.group({
      email:['',[Validators.required,Validators.email]],
      password1: ['', [
        Validators.required,
        Validators.pattern(/^(?=.*[A-Z])(?=.*\d).{8,}$/)]],
      password2: ['', Validators.required],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      numberOfPassport: ['', Validators.required],
      passportIssueDate: ['', Validators.required],
      passportIssuingAuthority: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator // Добавление кастомного валидатора
    })
  }
  passwordMatchValidator(control: AbstractControl) {
    const password1 = control.get('password1');
    const password2 = control.get('password2');

    if (password1?.value !== password2?.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

}

