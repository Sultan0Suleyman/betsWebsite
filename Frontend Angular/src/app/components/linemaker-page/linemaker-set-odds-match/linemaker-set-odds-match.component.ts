import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";

interface MatchDetails {
  id: number;
  sport: string;
  country: string | null;
  league: string;
  teamHome: string;
  teamAway: string;
  dateOfMatch: string;
  odds: Record<string, number | null>;
}

interface OddsData {
  win1: number | null;
  draw: number | null;
  win2: number | null;
  x1: number | null;
  w12: number | null;
  x2: number | null;
  w1InMatch: number | null;
  w2InMatch: number | null;
  firstTeamScoresFirst: number | null;
  secondTeamScoresFirst: number | null;
}
@Component({
  selector: 'app-linemaker-set-odds-match',
  templateUrl: './linemaker-set-odds-match.component.html',
  styleUrls: ['./linemaker-set-odds-match.component.css']
})

export class LinemakerSetOddsMatchComponent implements OnInit {
  @Input() matchId!: number;
  @Output() backToList = new EventEmitter<void>();

  matchDetails: MatchDetails | null = null;
  oddsData: Record<string, number | null> = {};

  isLoading = false;
  isSaving = false;
  errorMessage = '';
  isPublished = false; // состояние публикации

  showAddEventModal = false;
  newEventName = '';
  newEventValue: number | null = null;

  readonly DEFAULT_ODDS_KEYS = [
    "win1", "draw", "win2",
    "x1", "w12", "x2",
    "w1InMatch", "w2InMatch",
    "firstTeamScoresFirst", "secondTeamScoresFirst"
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadMatchDetails();
  }

  loadMatchDetails(): void {
    this.isLoading = true;
    this.http.get<MatchDetails>(`http://localhost:8080/linemaker/unpublished-match-odds-details/${this.matchId}`)
      .subscribe({
        next: (data) => {
          this.matchDetails = data;
          this.oddsData = { ...data.odds };

          this.DEFAULT_ODDS_KEYS.forEach(key => {
            if (!(key in this.oddsData)) this.oddsData[key] = null;
          });

          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading match details:', error);
          this.errorMessage = 'Failed to load match details';
          this.isLoading = false;
        }
      });
  }

  saveOdds(): void {
    if (!this.matchDetails) return;
    this.isSaving = true;

    const oddsPayload = {
      matchId: this.matchId,
      odds: this.oddsData
    };

    this.http.post('http://localhost:8080/linemaker/set-odds', oddsPayload)
      .subscribe({
        next: () => {
          alert('Odds saved successfully!');
        },
        error: (error) => {
          console.error('Error saving odds:', error);
          alert('Failed to save odds. Please try again.');
          this.isSaving = false;
        }
      });
  }

  goBack(): void {
    this.backToList.emit();
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  openAddEventModal(): void {
    this.showAddEventModal = true;
    this.newEventName = '';
    this.newEventValue = null;
  }

  closeAddEventModal(): void {
    this.showAddEventModal = false;
  }

  confirmAddEvent(): void {
    if (!this.newEventName.trim() || this.newEventValue === null) {
      alert("Please enter both event name and coefficient");
      return;
    }

    const key = this.newEventName.trim();
    if (this.oddsData[key] !== undefined) {
      alert("Event with this name already exists");
      return;
    }

    this.oddsData[key] = this.newEventValue;
    this.closeAddEventModal();
  }

  /** === Публикация / снятие публикации === */
  togglePublish(): void {
    this.isSaving = true;

    const url = this.isPublished
      ? `http://localhost:8080/linemaker/unpublish-match/${this.matchId}`
      : `http://localhost:8080/linemaker/publish-match/${this.matchId}`;

    this.http.post(url, {}).subscribe({
      next: () => {
        this.isSaving = false;
        this.isPublished = !this.isPublished;

        if (this.isPublished) {
          alert('✅ Match published successfully!');
        } else {
          alert('⛔ Match unpublished successfully!');
        }
      },
      error: (error) => {
        console.error('Error toggling publish state:', error);
        alert('Failed to update match status.');
        this.isSaving = false;
      }
    });
  }

  isDefaultKey(key: string): boolean {
    return this.DEFAULT_ODDS_KEYS.includes(key);
  }
}
