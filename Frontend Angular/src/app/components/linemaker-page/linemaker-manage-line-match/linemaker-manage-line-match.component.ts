import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { environment } from 'src/environments/environment';

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
@Component({
  selector: 'app-linemaker-manage-line-match',
  templateUrl: './linemaker-manage-line-match.component.html',
  styleUrls: ['./linemaker-manage-line-match.component.css']
})
export class LinemakerManageLineMatchComponent implements OnInit {
  private readonly baseUrl = environment.apiUrl;

  @Input() matchId!: number;
  @Output() backToList = new EventEmitter<void>();

  matchDetails: MatchDetails | null = null;
  oddsData: Record<string, number | null> = {};

  isLoading = false;
  isSaving = false;
  errorMessage = '';
  isPublished = true; // состояние публикации

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
    this.http.get<MatchDetails>(`${this.baseUrl}/linemaker/unpublished-match-odds-details/${this.matchId}`)
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

    this.http.post(`${this.baseUrl}/linemaker/set-odds`, oddsPayload)
      .subscribe({
        next: () => {
          this.isSaving = false; // ✅ ДОБАВЬТЕ ЭТУ СТРОКУ
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
      ? `${this.baseUrl}/linemaker/unpublish-match/${this.matchId}`
      : `${this.baseUrl}/linemaker/publish-match/${this.matchId}`;

    if(!this.isPublished) this.saveOdds()

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

  trackByKey(index: number, item: any): string {
    return item.key;
  }
}
