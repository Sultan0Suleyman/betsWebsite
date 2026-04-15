import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {SettlementMatch} from "../../../../services/Linemaker/match-settlement/models/settlement-match.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatchSettlementService} from "../../../../services/Linemaker/match-settlement/match-settlement.service";
import {FinishGameRequest} from "../../../../services/Linemaker/match-settlement/models/finish-game-request.model";
import {
  BetCalculationResult
} from "../../../../services/Linemaker/match-settlement/models/bet-calculation-result.model";
import {RefundResult} from "../../../../services/Linemaker/match-settlement/models/refund-result.model";

@Component({
  selector: 'app-settlement-details',
  templateUrl: './settlement-details.component.html',
  styleUrls: ['./settlement-details.component.css']
})

export class SettlementDetailsComponent implements OnChanges {
  @Input() matchId!: number;
  @Output() backToList = new EventEmitter<void>();

  match: SettlementMatch | null = null;

  settlementForm!: FormGroup;

  isLoading = false;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  isActionCompleted = false;

  settlementResult: BetCalculationResult | null = null;
  refundResult: RefundResult | null = null;

  constructor(
    private fb: FormBuilder,
    private settlementService: MatchSettlementService
  ) {
    this.initForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['matchId'] && this.matchId) {
      this.loadMatch();
    }
  }

  initForm(): void {
    this.settlementForm = this.fb.group({
      homeScore: [null, [Validators.required, Validators.min(0)]],
      awayScore: [null, [Validators.required, Validators.min(0)]],
      extraTimeHomeScore: [null, [Validators.min(0)]],
      extraTimeAwayScore: [null, [Validators.min(0)]],
      penaltyHomeScore: [null, [Validators.min(0)]],
      penaltyAwayScore: [null, [Validators.min(0)]]
    });
  }

  loadMatch(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.settlementService.getMatch(this.matchId).subscribe({
      next: (data) => {
        this.match = data;

        this.settlementForm.patchValue({
          homeScore: data.scoreHome,
          awayScore: data.scoreAway,
          extraTimeHomeScore: data.extraTimeHomeScore,
          extraTimeAwayScore: data.extraTimeAwayScore,
          penaltyHomeScore: data.penaltyHomeScore,
          penaltyAwayScore: data.penaltyAwayScore
        });

        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = this.extractErrorMessage(error);
        this.isLoading = false;
      }
    });
  }

  saveScore(): void {
    if (this.settlementForm.invalid || !this.match) {
      this.settlementForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: FinishGameRequest = this.settlementForm.value;

    this.settlementService.saveScore(this.match.id, request).subscribe({
      next: (updatedMatch) => {
        this.match = updatedMatch;
        this.successMessage = 'Score saved successfully';
        this.isSubmitting = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = this.extractErrorMessage(error);
        this.isSubmitting = false;
      }
    });
  }

  finishAndSettle(): void {
    if (this.settlementForm.invalid || !this.match) {
      this.settlementForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.settlementResult = null;
    this.refundResult = null;

    const request: FinishGameRequest = this.settlementForm.value;

    this.settlementService.finishAndSettle(this.match.id, request).subscribe({
      next: (result) => {
        this.successMessage = 'Match settled successfully';
        this.settlementResult = result;
        this.isSubmitting = false;

        // 🔥 ВАЖНО
        this.isActionCompleted = true;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = this.extractErrorMessage(error);
        this.isSubmitting = false;
      }
    });
  }

  cancelMatch(): void {
    if (!this.match) return;

    const confirmed = window.confirm('Are you sure you want to cancel this match and refund all bets?');
    if (!confirmed) return;

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.settlementResult = null;
    this.refundResult = null;

    this.settlementService.cancelMatch(this.match.id).subscribe({
      next: (result) => {
        this.successMessage = 'Match cancelled successfully';
        this.refundResult = result;
        this.isSubmitting = false;

        // 🔥 ВАЖНО
        this.isActionCompleted = true;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = this.extractErrorMessage(error);
        this.isSubmitting = false;
      }
    });
  }

  goBack(): void {
    this.backToList.emit();
  }

  getStatusText(): string {
    if (!this.match) {
      return '';
    }

    if (this.match.resultsProcessed) {
      return 'Settled';
    }

    if (this.match.gameEnded) {
      return 'Finished';
    }

    return 'Pending / Live-like';
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.settlementForm.get(fieldName);
    return !!field && field.invalid && (field.touched || field.dirty);
  }

  extractErrorMessage(error: any): string {
    if (typeof error?.error === 'string') {
      return error.error;
    }

    if (error?.error?.message) {
      return error.error.message;
    }

    return 'Operation failed';
  }
}
