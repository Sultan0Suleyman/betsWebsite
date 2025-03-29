import {Component, EventEmitter, OnChanges, OnInit, Output} from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css']
})
export class SideBarComponent implements OnInit{
  @Output() countrySportSelected: EventEmitter<{ country: string, sport: string, league: string }> = new EventEmitter();

  listOfSports: string[] = [];
  selectedSport: string = '';
  listOfCountries: { [sport: string]: string[] } = {};
  selectedCountry: string = '';
  listOfLeagues: { [countrySport: string]: string[] } = {};
  selectedLeague: string = '';
  shouldShowArrow: boolean = true;
  noMatchesMessage = ''

  constructor(private http: HttpClient) { }

  ngOnInit() {
    // Fetch data from the backend
    this.http.get<string[]>('http://localhost:8080/list/sports').subscribe({
      next:(data: string[]) => {
        this.listOfSports = data
      },
      error:(error) => {
        console.error('Error fetching sports:', error);
      }
  });
  }

  onSportClick(sport: string) {
    // Toggle the selected sport
    this.noMatchesMessage = ''
    this.selectedSport = (this.selectedSport === sport) ? '' : sport;
    this.selectedCountry = ''; // Reset selected country
    this.selectedLeague = ''; // Reset selected league
    // Fetch countries for the selected sport
    if (!this.listOfCountries[sport]) {
      this.http.get<string[]>(`http://localhost:8080/list/countries/${sport}`).subscribe({
        next:(data: string[]) => {
          this.listOfCountries[sport] = data
          if(data.length===0)this.noMatchesMessage = "There are no upcoming matches in this sport"
        },
        error:(error) => {
          console.error('Error fetching countries:', error);
        }
    });
    }
  }

  onCountryClick(country: string, sport: string) {
    this.selectedCountry = (this.selectedCountry === country) ? '' : country;
    this.selectedLeague = ''; // Reset selected league
    // Fetch leagues for the selected country and sport
    if (!this.listOfLeagues[`${country}-${sport}`]) {
      this.http.get<string[]>(`http://localhost:8080/list/leagues/${sport}/${country}`).subscribe({
        next:(data: string[]) => {
          if(data===null){
            this.onLeagueClick(country,'null',sport)
            this.shouldShowArrow = false
          }else{
            this.shouldShowArrow = true
            this.listOfLeagues[`${country}-${sport}`] = data
          }
        },
        error:(error) => {
          console.error('Error fetching leagues:', error)
        }
    });
    }
  }

  onLeagueClick(league: string, country: string, sport: string) {
    this.selectedLeague = (this.selectedLeague === league) ? '' : league;
    this.countrySportSelected.emit({ country, sport, league });
  }

  getCountriesForSport(sport: string): string[] {
    return this.listOfCountries[sport] || [];
  }

  getLeaguesForCountrySport(country: string, sport: string): string[] {
    return this.listOfLeagues[`${country}-${sport}`] || [];
  }
}
