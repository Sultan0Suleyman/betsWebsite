import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { AppComponent } from './app.component';
import { SideBarComponent } from './components/home-page/side-bar/mainSidebar/side-bar.component';
import { ContentComponent } from './components/home-page/default-content/content.component';
import { PhotoSliderComponent } from './components/home-page/default-content/photo-slider/photo-slider.component';
import { TopeventsComponent } from './components/home-page/default-content/topevents/topevents.component';

import {RouterModule, Routes} from "@angular/router";
import { MainPageComponent } from './components/home-page/main-page.component';
import { RegistrationPageComponent } from './components/home-page/registration-page/registration-page.component';
import { DarkAdditionalNavBarComponent } from './components/home-page/nav-bar/dark-additional-nav-bar/dark-additional-nav-bar.component';
import { MainGreenNavBarComponent } from './components/home-page/nav-bar/main-green-nav-bar/main-green-nav-bar.component';
import { GreenEmptyNavBarComponent } from './components/home-page/nav-bar/green-empty-nav-bar/green-empty-nav-bar.component';
import { ReactiveFormsModule} from "@angular/forms";
import { LoginPageComponent } from './components/home-page/login-page/login-page.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AdminGuard } from "./guards/AdminGuard/admin.guard";
import { DefaultGuard } from "./guards/DefaultGuard/default.guard";
import { TokenInterceptor } from "./interceptors/TokenInterceptor/token.interceptor";
import { AuthService } from "./services/AuthService/auth.service";
import { JwtDecodeService } from "./services/JwtDecodeService/jwt-decode.service";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { LeaguesMatchesContentComponent } from './components/home-page/leagues-matches-content/leagues-matches-content.component';
import { FullGameContentComponent } from './components/home-page/full-game-content/full-game-content.component';
import { CouponBetsDialogComponent } from './components/Dialogs/coupon-bets-dialog/coupon-bets-dialog.component';
import { SupportChatDialogComponent } from './components/Dialogs/support-chat-dialog/support-chat-dialog.component';
import { WithdrawPageComponent } from './components/home-page/withdraw-page/withdraw-page.component';
import {PlayerGuard} from "./guards/PlayerGuard/player.guard";
import { TopUpPageComponent } from './components/home-page/top-up-page/top-up-page.component';
import { BetHistoryContentComponent } from './components/home-page/bet-history-content/bet-history-content.component';
import {MatTableModule} from "@angular/material/table";
import { AdminPageComponent } from './components/admin-page/admin-page.component';
import { AdminNavBarComponent } from './components/admin-page/admin-nav-bar/admin-nav-bar.component';
import { AdminListOfUsersComponent } from './components/admin-page/admin-list-of-users/admin-list-of-users.component';
import { AdminWorkerRegistrationComponent } from './components/admin-page/admin-worker-registration/admin-worker-registration.component';
import { ContractModalComponent } from './components/admin-page/admin-worker-registration/contract-modal/contract-modal.component';
import { ViewUserInfoComponent } from './components/admin-page/admin-list-of-users/view-user-info/view-user-info.component';
import { LinemakerPageComponent } from './components/linemaker-page/linemaker-page.component';
import {LinemakerGuard} from "./guards/LinemakerGuard/linemaker.guard";
import { LinemakerNavBarComponent } from './components/linemaker-page/linemaker-nav-bar/linemaker-nav-bar.component';
import { LinemakerLineMatchesComponent } from './components/linemaker-page/linemaker-line-matches/linemaker-line-matches.component';
import { LinemakerLiveMatchesComponent } from './components/linemaker-page/linemaker-live-matches/linemaker-live-matches.component';
import { LinemakerCreateMatchComponent } from './components/linemaker-page/linemaker-create-match/linemaker-create-match.component';
import { LinemakerUnpublishedMatchesComponent } from './components/linemaker-page/linemaker-unpublished-matches/linemaker-unpublished-matches.component';

const routes: Routes=[
  { path: '', redirectTo: '/player/main-page', pathMatch: 'full' },
  { path: 'home/login', component: LoginPageComponent },
  {
    path: 'player',
    children: [
      { path: 'main-page', component: MainPageComponent },
      { path:'registration',component:RegistrationPageComponent,canActivate: [DefaultGuard] },
      { path:'login',component:LoginPageComponent,canActivate: [DefaultGuard] },
      { path:'withdraw',component: WithdrawPageComponent ,canActivate: [PlayerGuard] },
      { path:'top-up',component: TopUpPageComponent ,canActivate: [PlayerGuard] },
    ]
  },
  { path: 'admin', component: AdminPageComponent, canActivate: [AdminGuard] },
  { path: 'linemaker', component: LinemakerPageComponent, canActivate: [LinemakerGuard] }
  ]

@NgModule({
  declarations: [
    AppComponent,
    SideBarComponent,
    ContentComponent,
    PhotoSliderComponent,
    TopeventsComponent,
    MainPageComponent,
    RegistrationPageComponent,
    DarkAdditionalNavBarComponent,
    MainGreenNavBarComponent,
    GreenEmptyNavBarComponent,
    LoginPageComponent,
    LeaguesMatchesContentComponent,
    FullGameContentComponent,
    CouponBetsDialogComponent,
    SupportChatDialogComponent,
    WithdrawPageComponent,
    TopUpPageComponent,
    BetHistoryContentComponent,
    AdminPageComponent,
    AdminNavBarComponent,
    AdminListOfUsersComponent,
    AdminWorkerRegistrationComponent,
    ContractModalComponent,
    ViewUserInfoComponent,
    LinemakerPageComponent,
    LinemakerNavBarComponent,
    LinemakerLineMatchesComponent,
    LinemakerLiveMatchesComponent,
    LinemakerCreateMatchComponent,
    LinemakerUnpublishedMatchesComponent,
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    BrowserAnimationsModule, // Включите RouterModule и передайте в него массив маршрутов
    FormsModule,
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule
  ],
  exports: [
    RouterModule
  ],
  providers: [
    AuthService,
    JwtDecodeService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
  ],
  bootstrap: [
    AppComponent
  ]

})
export class AppModule {

}
