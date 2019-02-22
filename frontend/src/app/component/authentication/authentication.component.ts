import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../service/authentication/auth.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
})
export class AuthenticationComponent implements OnInit {
  emailSignIn: string;
  emailSignUp: string;

  constructor(private authService: AuthService,
              private toastr: ToastrService) { }

  ngOnInit() {
  }

  signIn(){
    this.authService.signIn(this.emailSignIn).subscribe(value => {
      this.toastr.success("You are successfully signed in!");
    },error => {
      console.log(error);
      this.toastr.error(error.error.message);
    })
  }

  signUp(){
    if(this.emailSignUp){
      this.authService.signUp(this.emailSignUp).subscribe(value => {
        this.toastr.success("You are successfully signed up!");
      },error => {
        console.log(error);
        this.toastr.error(error.error.message);
      })
    }
  }
}
