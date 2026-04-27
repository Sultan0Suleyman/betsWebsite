import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {AuthService} from "../../../services/AuthService/auth.service";
import { environment } from 'src/environments/environment';

interface UnpublishedMatch {
  id?: number;
  sport: string;
  country: string | null; // Теперь страна может быть null
  league: string;
  teamHome: string;
  teamAway: string;
  dateOfMatch: string;

  // status: NONE = gray (no initials)
  // IN_PROGRESS = blue (with initials)
  // PENDING = orange (with initials)
  // DONE = green (with initials)
  status?: 'NONE' | 'IN_PROGRESS' | 'PENDING' | 'DONE';
  linemakersName?: string;
  linemakerInitials?: string; // добавляем поле для инициалов
}

@Component({
  selector: 'app-linemaker-unpublished-matches',
  templateUrl: './linemaker-unpublished-matches.component.html',
  styleUrls: ['./linemaker-unpublished-matches.component.css']
})

export class LinemakerUnpublishedMatchesComponent implements OnInit {
  private readonly baseUrl = environment.apiUrl;

  @Output() setOddsRequested = new EventEmitter<number>();

  unpublishedMatches: UnpublishedMatch[] = [];
  isLoading = false;
  errorMessage = '';

  searchQuery: string = '';
  statusFilter: string = '';
  sportFilter: string = '';
  availableSports: string[] = [];

  filteredMatches: UnpublishedMatch[] = [];

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUnpublishedMatches();
  }

  loadUnpublishedMatches(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.http.get<UnpublishedMatch[]>(`${this.baseUrl}/linemaker/unpublished-matches`)
      .subscribe({
        next: (data) => {
          // ensure defaults for status/initials to avoid TS/runtime issues
          this.unpublishedMatches = data.map(m => ({
            ...m,
            status: m.status ?? 'NONE',
            country: m.country ?? null, // Обрабатываем null для страны
            linemakersName: m.linemakersName ?? undefined,
            // конвертируем полное имя в инициалы
            linemakerInitials: m.linemakersName ? this.convertNameToInitials(m.linemakersName) : undefined
          }));
          this.availableSports = [...new Set(this.unpublishedMatches.map(m => m.sport))]; // формируем список видов спорта
          this.applyFilters(); // применяем фильтры сразу после загрузки
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading unpublished matches:', error);
          this.errorMessage = 'Failed to load matches';
          this.isLoading = false;
        }
      });
  }

  publishMatch(index: number): void {
    if (!confirm('Are you sure you want to publish this match?')) return;
    const match = this.unpublishedMatches[index];
    this.http.post(`${this.baseUrl}/linemaker/publish-match`, match)
      .subscribe({
        next: () => {
          this.unpublishedMatches.splice(index, 1);
          alert('Match published successfully');
        },
        error: (error) => {
          console.error('Error publishing match:', error);
          alert('Failed to publish match');
        }
      });
  }

  // Новый метод для открытия настройки коэффициентов
  setOdds(match: UnpublishedMatch): void {
    if (match.id) {
      this.setOddsRequested.emit(match.id);
    } else {
      alert('Match ID is missing. Cannot set odds.');
    }
  }

  deleteMatch(match: UnpublishedMatch): void {
    if (!confirm('Are you sure you want to delete this match?')) return;

    if (!match.id) {
      alert('Match ID is missing');
      return;
    }

    this.http.delete(`${this.baseUrl}/linemaker/delete-match/${match.id}`)
      .subscribe({
        next: () => {
          // удаляем из оригинального массива по id
          this.unpublishedMatches = this.unpublishedMatches.filter(m => m.id !== match.id);

          // пересчитываем фильтрованный массив
          this.applyFilters();

          alert('Match deleted successfully');
        },
        error: (err) => {
          console.error('Error deleting match:', err);
          alert('Failed to delete match');
        }
      });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  // Вспомогательный метод для отображения страны
  getCountryDisplay(country: string | null): string {
    return country || 'N/A'; // Если страна null, показываем "N/A"
  }

  private getCurrentInitials(): string {
    // try localStorage first (you probably save initials on login)
    const stored = localStorage.getItem('linemakerInitials');
    if (stored && stored.trim()) return stored.trim();

    // fallback: derive from full name in localStorage (e.g. "John Doe" -> "JD")
    const full = localStorage.getItem('linemakerName') || '';
    return this.convertNameToInitials(full);
  }

  // Вынесем логику конвертации имени в инициалы в отдельный метод
  private convertNameToInitials(fullName: string): string {
    const parts = fullName.trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) return 'LM';
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  toggleStatus(match: UnpublishedMatch): void {
    const statuses: Array<'NONE' | 'IN_PROGRESS' | 'PENDING' | 'DONE'> =
      ['NONE', 'IN_PROGRESS', 'PENDING', 'DONE'];

    const currentIndex = statuses.indexOf(match.status ?? 'NONE');
    const nextStatus = statuses[(currentIndex + 1) % statuses.length];

    // UI update
    if (nextStatus === 'NONE') {
      match.status = 'NONE';
      match.linemakersName = undefined;
      match.linemakerInitials = undefined;
    } else {
      const cachedUser = this.authService.getCachedNameSurname();

      const name = cachedUser
        ? `${cachedUser.firstName} ${cachedUser.lastName}`
        : null;

      if (name) {
        match.status = nextStatus;
        match.linemakersName = name;
        match.linemakerInitials = this.convertNameToInitials(name);
      } else {
        this.authService
          .getCurrentUsersNameSurname(this.authService.getCurrentUser().getValue().username)
          .subscribe(
            user => {
              const fullName = `${user.firstName} ${user.lastName}`;
              match.status = nextStatus;
              match.linemakersName = fullName;
              match.linemakerInitials = this.convertNameToInitials(fullName);
            },
            err => {
              console.error('Failed to load user info', err);
              alert('Failed to get current user info');
            }
          );
      }
    }

    // backend update
    if (match.id != null) {
      const body = {
        matchId: match.id,
        status: match.status,
        linemakersName: match.linemakersName
      };

      this.http.patch(`${this.baseUrl}/linemaker/match-status`, body)
        .subscribe({
          error: err => {
            console.error('Failed to update match status:', err);
          }
        });
    }
  }


  getStatusClass(status?: string): string {
    switch (status) {
      case 'IN_PROGRESS': return 'in-progress';
      case 'PENDING': return 'pending';
      case 'DONE': return 'done';
      default: return 'gray'; // NONE -> gray
    }
  }
  applyFilters(): void {
    this.filteredMatches = this.unpublishedMatches.filter(match => {
      const matchesSearch =
        match.teamHome.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        match.teamAway.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        match.league.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        match.sport.toLowerCase().includes(this.searchQuery.toLowerCase());

      const matchesStatus = this.statusFilter ? match.status === this.statusFilter : true;
      const matchesSport = this.sportFilter ? match.sport === this.sportFilter : true;

      return matchesSearch && matchesStatus && matchesSport;
    });
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.statusFilter = '';
    this.sportFilter = '';
    this.applyFilters();
  }
}
