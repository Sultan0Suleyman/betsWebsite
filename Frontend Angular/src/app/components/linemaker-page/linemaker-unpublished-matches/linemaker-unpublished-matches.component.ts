import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {AuthService} from "../../../services/AuthService/auth.service";

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

    this.http.get<UnpublishedMatch[]>('http://localhost:8080/linemaker/unpublished-matches')
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
    this.http.post('http://localhost:8080/linemaker/publish-match', match)
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
  setOdds(index: number): void {
    const match = this.unpublishedMatches[index];
    if (match.id) {
      // Отправляем событие родительскому компоненту
      this.setOddsRequested.emit(match.id);
    } else {
      alert('Match ID is missing. Cannot set odds.');
    }
  }

  deleteMatch(index: number): void {
    if (!confirm('Are you sure you want to delete this match?')) return;
    const match = this.unpublishedMatches[index];
    this.http.delete(`http://localhost:8080/linemaker/delete-match/${match.id}`)
      .subscribe({
        next: () => {
          this.unpublishedMatches.splice(index, 1);
          alert('Match deleted successfully');
        },
        error: (error) => {
          console.error('Error deleting match:', error);
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

  toggleStatus(index: number): void {
    console.log(this.authService.getCachedNameSurname);
    const match = this.unpublishedMatches[index];
    const statuses: Array<'NONE' | 'IN_PROGRESS' | 'PENDING' | 'DONE'> = ['NONE', 'IN_PROGRESS', 'PENDING', 'DONE'];
    const currentIndex = statuses.indexOf(match.status ?? 'NONE');
    const nextStatus = statuses[(currentIndex + 1) % statuses.length];

    // update UI optimistically
    if (nextStatus === 'NONE') {
      match.status = 'NONE';
      match.linemakersName = undefined;
      match.linemakerInitials = undefined;
    } else {
      // получаем имя и фамилию из кеша AuthService
      const cachedUser = this.authService.getCachedNameSurname();
      if (cachedUser) {
        const initials = this.convertNameToInitials(`${cachedUser.firstName} ${cachedUser.lastName}`);
        match.status = nextStatus;
        match.linemakersName = `${cachedUser.firstName} ${cachedUser.lastName}`;
        match.linemakerInitials = initials;
      } else {
        // Если вдруг не нашли в sessionStorage, делаем запрос на бэк
        this.authService.getCurrentUsersNameSurname(this.authService.getCurrentUser().getValue().username)
          .subscribe(user => {
            const initials = this.convertNameToInitials(`${user.firstName} ${user.lastName}`);
            match.status = nextStatus;
            match.linemakersName = `${user.firstName} ${user.lastName}`;
            match.linemakerInitials = initials;
          }, error => {
            console.error('Failed to load user info', error);
            alert('Failed to get current user info');
          });
      }
    }

    // send update to backend if we have match id
    if (match.id != null) {
      const body = {
        matchId: match.id,
        status: match.status,
        linemakersName: match.linemakersName
      };

      this.http.patch('http://localhost:8080/linemaker/match-status', body)
        .subscribe({
          next: () => {
            // ok
          },
          error: (err) => {
            console.error('Failed to update match status:', err);
            // optionally revert UI on error
            match.status = statuses[currentIndex];
            if (match.status === 'NONE') {
              match.linemakersName = undefined;
              match.linemakerInitials = undefined;
            }
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
