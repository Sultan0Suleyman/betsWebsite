import {Component} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/AuthService/auth.service';
import {RedirectService} from "../../../services/RedirectService/redirect.service";

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent{
  currentDate = new Date();
  submitChecker: boolean = false;
  errorMessage: string = '';
  myForm: FormGroup = new FormGroup({

  })

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private redirectService:RedirectService
  ) {}

  submitForm() {
    if (this.myForm.valid) {
      const formData = this.myForm.value;
      this.submitChecker = true;
      this.authService.login(formData).subscribe({
        next: (response:any) => {
          console.log(response); // Log the entire response to inspect its structure
          // Обработка успешного ответа
          if (response && response.accessToken) {
            this.errorMessage = 'Login successful'
            // Сохранение токена в localStorage (вам следует рассмотреть более безопасные варианты)
            // Авторизация успешна, обновляем текущего пользователя
            // Редирект на нужную страницу в зависимости от роли
            this.authService.getCurrentObservableUser().subscribe((user) => {
              this.redirectService.handleRoleBasedRedirect(user.role)
              const currentTime = new Date()
              const futureTimeLogout = new Date();
              const storedDate = localStorage.getItem('dateOfLogout');
              if (!this.myForm.get('rememberMe')?.value&&this.authService.getUserRole()!=="ROLE_MAIN_ADMIN") {
                if(!storedDate){
                  futureTimeLogout.setHours(currentTime.getHours() + 1);
                  console.log("Date of logout: " + futureTimeLogout)
                  localStorage.setItem('dateOfLogout', futureTimeLogout.toString())
                }
              }else if(this.authService.getUserRole()==="ROLE_MAIN_ADMIN") {
                if (!storedDate) {
                  futureTimeLogout.setMinutes(currentTime.getMinutes()+30)
                  console.log("Date of logout: " + futureTimeLogout)
                  localStorage.setItem('dateOfLogout', futureTimeLogout.toString())
                }
              }
              localStorage.setItem('lastActivity',new Date().toString())
              const dateOfRefresh = new Date()
              dateOfRefresh.setTime(currentTime.getTime()+14*60*1000)
              localStorage.setItem('dateOfRefresh', dateOfRefresh.toString())
              console.log(dateOfRefresh)
            });
          } else {
            console.error('Wrong format of response:', response);
          }
        },
        error: (error) => {
          // Обработка ошибки
          console.error('Login failed:', error)
          // Отобразите сообщение об ошибке пользователю, например, через переменную в шаблоне
          this.errorMessage = 'Wrong login or password'
        }}
      );
    }
  }

  // onLoginPlayerLogic(){
  //   const currentTime = new Date()
  //   if (!this.myForm.get('rememberMe')?.value&&this.authService.getUserRole()=="ROLE_PLAYER") {
  //     const futureTimeLogout = new Date();
  //     const storedDate = localStorage.getItem('dateOfLogout');
  //     if(!storedDate){
  //       futureTimeLogout.setHours(currentTime.getHours() + 1);
  //       console.log("Date of logout: "+futureTimeLogout)
  //       localStorage.setItem('dateOfLogout', futureTimeLogout.toString())
  //       localStorage.setItem('lastActivity',new Date().toString())
  //     }
  //   }
  // }

  ngOnInit(): void {
    this.myForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      rememberMe: [false] // добавлено поле rememberMe
    });
  }
  printForm():void{
    console.log(this.errorMessage)
  }
}
