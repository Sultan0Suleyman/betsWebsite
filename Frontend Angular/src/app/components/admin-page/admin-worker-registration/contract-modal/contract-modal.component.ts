import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-contract-modal',
  templateUrl: './contract-modal.component.html',
  styleUrls: ['./contract-modal.component.css']
})
export class ContractModalComponent {
  contractForm: FormGroup;

  @Input() isOpen = false;
  @Output() contractCreated = new EventEmitter<any>();
  @Output() modalClosed = new EventEmitter<void>();

  constructor(private fb: FormBuilder) {
    this.contractForm = this.fb.group({
      contractCreationDate: ['', [Validators.required, this.creationDateValidator]],
      contractDateOfExpiry: ['', [Validators.required]],
      contractSalary: ['', [Validators.required, Validators.min(0)]],
    }, {
      validators: this.expiryDateValidator // Добавляем кастомный валидатор
    });
  }

  closeModal() {
    this.isOpen = false;
    this.modalClosed.emit();
  }

  saveContract() {
    if (this.contractForm.valid) {
      this.contractCreated.emit(this.contractForm.value);
      this.closeModal();
    }
  }

  // Валидация: Дата создания не может быть в будущем
  creationDateValidator(control: AbstractControl) {
    const selectedDate = new Date(control.value);
    const today = new Date();

    if (selectedDate > today) {
      return { futureDate: true };
    }
    return null;
  }

  // Валидация: Дата истечения должна быть позже даты создания
  expiryDateValidator(group: AbstractControl) {
    const creationDate = new Date(group.get('contractCreationDate')?.value);
    const expiryDate = new Date(group.get('contractDateOfExpiry')?.value);

    if (expiryDate <= creationDate) {
      return { invalidExpiryDate: true };
    }
    return null;
  }
}
