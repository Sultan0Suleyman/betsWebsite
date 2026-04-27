import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {HttpClient} from "@angular/common/http";
import { environment } from 'src/environments/environment';

// Interfaces based on your backend entities
interface Country {
  name_en: string;
}

interface Sport {
  name_en: string;
}

interface League {
  name_en: string;
  country: Country;
  sport: Sport;
}

interface Team {
  name_en: string;
  league: League;
  sport: Sport;
  country: Country;
}

@Component({
  selector: 'app-linemaker-create-match',
  templateUrl: './linemaker-create-match.component.html',
  styleUrls: ['./linemaker-create-match.component.css']
})
export class LinemakerCreateMatchComponent implements OnInit{
  private readonly baseUrl = environment.apiUrl;

  createMatchForm: FormGroup;

  // Data arrays - in real app would come from backend
  countries: Country[] = [];
  sports: Sport[] = [];
  leagues: League[] = [];
  teams: Team[] = [];

  // Filtered arrays based on selections
  filteredLeagues: League[] = [];
  filteredHomeTeams: Team[] = [];
  filteredAwayTeams: Team[] = [];

  isLoading = false;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient
  ) {
    this.createMatchForm = this.fb.group({
      sport: ['', Validators.required],
      country: [''],
      league: ['', Validators.required],
      teamHome: ['', Validators.required],
      teamAway: ['', Validators.required],
      dateOfMatch: ['', Validators.required],
      timeOfMatch: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadInitialData();
    this.setupFormSubscriptions();
  }

  private loadInitialData(): void {
    this.isLoading = true;

    this.http.get<string[]>(`${this.baseUrl}/linemaker/list/countries`).subscribe({
      next: (data) => {
        this.countries = data.map(country => ({ name_en: country }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading countries:', error);
        this.countries = [];
        this.isLoading = false;
      }
    });

    this.http.get<string[]>(`${this.baseUrl}/list/sports`).subscribe({
      next: (data) => {
        this.sports = data.map(sport => ({ name_en: sport }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading sports:', error);
        this.sports = [];
        this.isLoading = false;
      }
    });

    // Команды теперь загружаются динамически при выборе лиги
    this.teams = [];
    this.filteredHomeTeams = [];
    this.filteredAwayTeams = [];
  }

  private setupFormSubscriptions(): void {
    this.createMatchForm.get('sport')?.valueChanges.subscribe(() => {
      this.updateLeagues();
    });

    this.createMatchForm.get('country')?.valueChanges.subscribe(() => {
      this.updateLeagues();
    });

    this.createMatchForm.get('league')?.valueChanges.subscribe(() => {
      const selectedLeague = this.createMatchForm.get('league')?.value;
      console.log('League changed:', selectedLeague);

      if (selectedLeague) {
        this.loadTeams(selectedLeague);
      } else {
        this.filteredHomeTeams = [];
        this.filteredAwayTeams = [];
        this.clearTeamSelections();
      }
    });

    this.createMatchForm.get('teamHome')?.valueChanges.subscribe(() => {
      this.updateAwayTeams();
    });

    this.createMatchForm.get('teamAway')?.valueChanges.subscribe(() => {
      this.updateHomeTeams();
    });
  }

  private updateLeagues(): void {
    const sport = this.createMatchForm.get('sport')?.value;
    const country = this.createMatchForm.get('country')?.value;

    if (sport) {
      this.loadLeagues(sport, country);
    } else {
      this.filteredLeagues = [];
      this.createMatchForm.patchValue({ league: '' });
    }
    this.clearTeamSelections();
  }

  private loadTeams(league: string): void {
    this.http.get<string[]>(`${this.baseUrl}/linemaker/list/teams/${league}`).subscribe({
      next: (data) => {
        // Получаем текущие выбранные значения спорта и страны
        const selectedSport = this.createMatchForm.get('sport')?.value;
        const selectedCountry = this.createMatchForm.get('country')?.value;
        const selectedLeague = this.leagues.find(l => l.name_en === league);

        // Создаем объекты команд из полученных имен
        this.teams = data.map(teamName => ({
          name_en: teamName,
          league: selectedLeague || {
            name_en: league,
            country: { name_en: selectedCountry },
            sport: { name_en: selectedSport }
          },
          sport: { name_en: selectedSport },
          country: { name_en: selectedCountry }
        }));

        // Обновляем фильтрованные списки
        this.filteredHomeTeams = [...this.teams];
        this.filteredAwayTeams = [...this.teams];

        // Очищаем выбранные команды если они не входят в новый список
        const currentHomeTeam = this.createMatchForm.get('teamHome')?.value;
        const currentAwayTeam = this.createMatchForm.get('teamAway')?.value;

        if (currentHomeTeam && !this.teams.some(t => t.name_en === currentHomeTeam)) {
          this.createMatchForm.patchValue({ teamHome: '' });
        }
        if (currentAwayTeam && !this.teams.some(t => t.name_en === currentAwayTeam)) {
          this.createMatchForm.patchValue({ teamAway: '' });
        }
      },
      error: (error) => {
        console.error('Error loading teams:', error);
        this.teams = [];
        this.filteredHomeTeams = [];
        this.filteredAwayTeams = [];
      }
    });
  }

  private loadLeagues(sport: string, country?: string): void {
    this.isLoading = true;

    // если страна не выбрана → передаем пустую строку
    const url = `${this.baseUrl}/linemaker/list/leagues/${sport}/${country || null}`;

    this.http.get<string[]>(url).subscribe({
      next: (data) => {
        this.leagues = data.map(name => ({
          name_en: name,
          country: { name_en: country || '' },
          sport: { name_en: sport }
        }));
        this.filteredLeagues = this.leagues;
        this.isLoading = false;

        const currentLeague = this.createMatchForm.get('league')?.value;
        if (currentLeague && !this.leagues.some(l => l.name_en === currentLeague)) {
          this.createMatchForm.patchValue({ league: '' });
        }
      },
      error: (error) => {
        console.error('Ошибка загрузки лиг:', error);
        this.leagues = [];
        this.filteredLeagues = [];
        this.isLoading = false;
      }
    });
  }

  private updateHomeTeams(): void {
    const selectedAwayTeam = this.createMatchForm.get('teamAway')?.value;
    const selectedLeague = this.createMatchForm.get('league')?.value;

    if (selectedLeague) {
      this.filteredHomeTeams = this.teams.filter(team =>
        team.league.name_en === selectedLeague && team.name_en !== selectedAwayTeam
      );
    }
  }

  private updateAwayTeams(): void {
    const selectedHomeTeam = this.createMatchForm.get('teamHome')?.value;
    const selectedLeague = this.createMatchForm.get('league')?.value;

    if (selectedLeague) {
      this.filteredAwayTeams = this.teams.filter(team =>
        team.league.name_en === selectedLeague && team.name_en !== selectedHomeTeam
      );
    }
  }

  private clearTeamSelections(): void {
    this.createMatchForm.patchValue({
      teamHome: '',
      teamAway: ''
    });
    this.filteredHomeTeams = [];
    this.filteredAwayTeams = [];
  }

  onSubmit(): void {
    if (this.createMatchForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;

      const formValue = this.createMatchForm.value;
      const matchDateTime = `${formValue.dateOfMatch}T${formValue.timeOfMatch}:00`;

      const matchData = {
        sport: formValue.sport,
        country: formValue.country || null,
        league: formValue.league,
        teamHome: formValue.teamHome,
        teamAway: formValue.teamAway,
        dateOfMatch: matchDateTime
      };

      this.http.post(`${this.baseUrl}/linemaker/create-match`, matchData, { responseType: 'text' }).subscribe({
        next: (response) => {
          // response — это строка (сообщение с бэка)
          console.log('Match created successfully:', response);
          this.isSubmitting = false;
          alert(response);
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Error creating match:', error);

          // сервер вернул текст
          if (typeof error.error === 'string') {
            alert(error.error);
          } else if (error.error?.message) {
            alert(error.error.message);
          } else {
            alert('Failed to create match. Please try again.');
          }
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.createMatchForm.controls).forEach(key => {
      const control = this.createMatchForm.get(key);
      control?.markAsTouched();
    });
  }

  onCancel(): void {
    // Сбрасываем форму к исходным значениям (пустые строки)
    this.createMatchForm.reset({
      sport: '',
      country: '',
      league: '',
      teamHome: '',
      teamAway: '',
      dateOfMatch: '',
      timeOfMatch: ''
    });

    // Очищаем все фильтрованные массивы
    this.filteredLeagues = [];
    this.filteredHomeTeams = [];
    this.filteredAwayTeams = [];

    // Сбрасываем состояния
    this.isSubmitting = false;

    // Убираем состояние touched со всех полей
    this.createMatchForm.markAsUntouched();

    // Переходим обратно (по желанию)
    // this.router.navigate(['/linemaker/unpublished-matches']);
  }

  // Utility methods for template
  isFieldInvalid(fieldName: string): boolean {
    const field = this.createMatchForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.createMatchForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${this.getFieldDisplayName(fieldName)} is required`;
    }
    return '';
  }

  private getFieldDisplayName(fieldName: string): string {
    const displayNames: { [key: string]: string } = {
      sport: 'Sport',
      country: 'Country',
      league: 'League',
      teamHome: 'Home Team',
      teamAway: 'Away Team',
      dateOfMatch: 'Match Date',
      timeOfMatch: 'Match Time'
    };
    return displayNames[fieldName] || fieldName;
  }

  getTodayDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }
}
