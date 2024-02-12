import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../../../services/AuthService/auth.service";

@Component({
  selector: 'app-withdraw-page',
  templateUrl: './withdraw-page.component.html',
  styleUrls: ['./withdraw-page.component.css']
})
export class WithdrawPageComponent implements OnInit{
  private apiUrl = 'http://localhost:8080/payment'
  amount: number | undefined
  selectedPaymentMethod!: string
  userId = this.authService.getCurrentUser().value.username
  paymentMethods!: string[];
  accountNumber: number | undefined
  showHistory: boolean = false
  withdrawHistory: any
  message: string = ""
  errorMessage: string = ""

  constructor(private http: HttpClient,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.loadPaymentMethods()
  }

  loadPaymentMethods() {
    this.http.get<string[]>(this.apiUrl + '/methods').subscribe({
      next: (methods) => {
        this.paymentMethods = methods;
      },
      error: (error) => {
        console.error('Failed to load payment methods:', error);
      }
    })
  }

  withdraw() {
    if (!this.selectedPaymentMethod || this.amount === undefined || this.amount <= 0 ||
      this.accountNumber === undefined || this.accountNumber <= 0) {
      this.message = ''
      this.errorMessage = 'Please enter a valid value for every field.'
      return;
    } else {
      const requestBody = {
        paymentMethod: this.selectedPaymentMethod,
        amount: this.amount,
        userId: this.userId,
        accountNumber: this.accountNumber
      }
      this.http.post<any>(this.apiUrl+'/withdraw/add', requestBody).subscribe({
        next: (response) => {
          this.errorMessage = ''
          this.message = response.message
          // Handle the response as needed
        },
        error: (errorResponse) => {
          // Access the error object and display the appropriate message
          if (errorResponse && errorResponse.error && errorResponse.error.error) {
            this.message = ''
            this.errorMessage = errorResponse.error.error;
          } else {
            // Handle other types of errors or display a generic message
            alert('An error occurred during withdrawal request.');
          }
        }
      })
    }
  }

  toggleHistory() {
    this.showHistory = !this.showHistory;
    if (this.showHistory) {
      this.http.get(this.apiUrl+`/withdraw/story/${this.userId}`).subscribe({
        next: (withdraws) => {
          console.log(withdraws)
          this.withdrawHistory = withdraws;
        },
        error: (error) => {
          console.error('Failed to load payment methods:', error);
        }
      });
    }
  }


}
