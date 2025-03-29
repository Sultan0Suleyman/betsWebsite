import { Component } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ValidationService} from "../../../services/ValidationService/validation.service";
import {limitInputLength} from "../../../utils/form.utils";

@Component({
  selector: 'app-admin-worker-registration',
  templateUrl: './admin-worker-registration.component.html',
  styleUrls: ['./admin-worker-registration.component.css']
})
export class AdminWorkerRegistrationComponent {
  myForm: FormGroup = new FormGroup({

  })
  submissionMessage:String = ''
  isContractModalOpen = false;
  contract: any = null;  // Данные контракта

  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.myForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      numberOfPassport: ['', Validators.required],
      passportIssueDate: ['', [Validators.required, ValidationService.issueDateValidator]],
      passportIssuingAuthority: ['', Validators.required],
      password1: ['', [Validators.required, Validators.pattern(/^(?=.*[A-Z])(?=.*\d).{8,}$/)]],
      password2: ['', Validators.required],
      role: ['', Validators.required]
    }, {
      validators: ValidationService.passwordMatchValidator
    });
  }

  limitPassportNumber(event: any) {
    limitInputLength(event, 18);
  }

  openContractModal() {
    this.isContractModalOpen = true;
  }

  closeContractModal() {
    this.isContractModalOpen = false;
  }

  onContractCreated(contractData: any) {
    this.contract = contractData;  // Привязываем контракт к форме
    this.isContractModalOpen = false;  // Закрываем модальное окно
  }

  submitForm(): void {
    if (this.myForm.valid) {
      const formData = { ...this.myForm.value }
      formData.password = formData.password1
      delete formData.password1
      delete formData.password2

      if (this.contract) {
        formData.contract = this.contract  // Добавляем контракт в данные формы
      }

      // Отправка данных на бэкенд, включая контракт
      if (formData.password !== undefined) this.sendDataToBackend(formData)
    }
  }

  sendDataToBackend(formData: any) {
    this.http.post('http://localhost:8080/admin/worker-registration',formData)
      .subscribe({next:(response: any) => {
          this.submissionMessage = response.message // извлекаем сообщение об успешной регистрации из ответа или используем стандартное сообщение
        }, error:(error) => {
          if (error.error && error.error.error) {
            this.submissionMessage = error.error.error; // извлекаем сообщение об ошибке из ответа
          } else {
            this.submissionMessage = 'An error occurred on the server.' // используем стандартное сообщение об ошибке
          }
        }})
  }



  removeContract() {
    this.contract = null;
  }


}
