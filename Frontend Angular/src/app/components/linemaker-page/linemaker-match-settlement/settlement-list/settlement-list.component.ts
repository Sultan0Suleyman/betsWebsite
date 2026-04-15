import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {SettlementMatch} from "../../../../services/Linemaker/match-settlement/models/settlement-match.model";
import {MatchSettlementService} from "../../../../services/Linemaker/match-settlement/match-settlement.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-settlement-list',
  templateUrl: './settlement-list.component.html',
  styleUrls: ['./settlement-list.component.css']
})
export class SettlementListComponent implements OnInit {
  @Output() openSettlementMatchRequested = new EventEmitter<number>();

  matches: SettlementMatch[] = [];
  filteredMatches: SettlementMatch[] = [];

  isLoading = false;
  errorMessage = '';

  searchTerm = '';
  selectedSport = 'All';
  availableSports: string[] = ['All'];

  constructor(private settlementService: MatchSettlementService) {}

  ngOnInit(): void {
    this.loadMatches();
  }

  loadMatches(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.settlementService.getMatches().subscribe({
      next: (data) => {
        this.matches = data;
        this.filteredMatches = [...data];

        const sports = [...new Set(data.map(m => m.sport).filter(Boolean))];
        this.availableSports = ['All', ...sports];

        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = this.extractErrorMessage(error);
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    const term = this.searchTerm.trim().toLowerCase();

    this.filteredMatches = this.matches.filter(match => {
      const matchesSearch =
        !term ||
        match.homeTeam.toLowerCase().includes(term) ||
        match.awayTeam.toLowerCase().includes(term) ||
        match.league.toLowerCase().includes(term) ||
        match.sport.toLowerCase().includes(term);

      const matchesSport =
        this.selectedSport === 'All' || match.sport === this.selectedSport;

      return matchesSearch && matchesSport;
    });
  }

  openMatch(matchId: number): void {
    this.openSettlementMatchRequested.emit(matchId);
  }

  getScoreText(match: SettlementMatch): string {
    if (match.scoreHome == null || match.scoreAway == null) {
      return '— : —';
    }

    let text = `${match.scoreHome}:${match.scoreAway}`;

    if (match.extraTimeHomeScore != null && match.extraTimeAwayScore != null) {
      text += ` (ET ${match.extraTimeHomeScore}:${match.extraTimeAwayScore})`;
    }

    if (match.penaltyHomeScore != null && match.penaltyAwayScore != null) {
      text += ` (PEN ${match.penaltyHomeScore}:${match.penaltyAwayScore})`;
    }

    return text;
  }

  getStatusText(match: SettlementMatch): string {
    if (match.resultsProcessed) {
      return 'Settled';
    }

    if (match.gameEnded) {
      return 'Finished';
    }

    const now = new Date();
    const matchDate = new Date(match.dateOfMatch);

    if (matchDate < now) {
      return 'Live';
    }

    return 'Pending';
  }

  extractErrorMessage(error: any): string {
    if (typeof error?.error === 'string') {
      return error.error;
    }

    if (error?.error?.message) {
      return error.error.message;
    }

    return 'Failed to load matches';
  }
}
