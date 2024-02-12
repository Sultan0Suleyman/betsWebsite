import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../../services/AuthService/auth.service";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-top-up-page',
  templateUrl: './top-up-page.component.html',
  styleUrls: ['./top-up-page.component.css']
})
export class TopUpPageComponent implements OnInit {
  private apiUrl = 'http://localhost:8080/payment';
  selectedPaymentMethod!: string
  paymentMethods!: string[]
  amount: number | undefined
  userId = this.authService.getCurrentUser().value.username
  showHistory: boolean = false
  refillHistory: any
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
    });
  }

  topUp() {
    console.log("this.selectedPaymentMethod", this.selectedPaymentMethod)
    console.log("this.amount", this.amount)
    if (!this.selectedPaymentMethod || this.amount === undefined || this.amount <= 0) {
      this.message = ''
      this.errorMessage = 'Please select a payment method and enter a valid amount.'
      return;
    } else {
      const requestBody = {
        paymentMethod: this.selectedPaymentMethod,
        amount: this.amount,
        userId: this.userId,
        isPaymentSuccessful: "true"
      };
      this.http.post<any>(this.apiUrl+'/refill/add', requestBody).subscribe({
        next: (response) => {
          if (response.paymentSuccessful) {
            this.updatePlayerBalance(this.userId, response.replenishmentAmount);
          }else{
            this.message = ''
          }
          // Handle the response as needed
        },
        error: (error) => {
          this.errorMessage='Error happened'
          console.error('Failed to process refill request:', error);
        }
      })
    }
  }
  updatePlayerBalance(userId: string, amount: number) {
    const updateBalanceRequest = {
      userId: userId,
      amount: amount
    };

    // Assuming there's an endpoint for updating the player's balance
    this.http.patch(this.apiUrl+'/updateBalance', updateBalanceRequest).subscribe({
      next: (response) => {
        this.message = 'Payment added successfully.'
        this.errorMessage = ''
        // Handle the update response if needed
      },
      error: (error) => {
        console.error('Failed to update player balance:', error);
        // Handle the error if needed
      }
    });
  }
  toggleHistory(){
    this.showHistory = !this.showHistory;
    if (this.showHistory) {
      this.http.get(this.apiUrl+`/refill/story/${this.userId}`).subscribe({
        next: (refills) => {
          console.log(refills)
          this.refillHistory = refills;
        },
        error: (error) => {
          console.error('Failed to load payment methods:', error);
        }
      });
    }
    }
}
