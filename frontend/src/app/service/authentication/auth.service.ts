import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Observable} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {UserData} from "../../model/user-data";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public static readonly SIGN_UP_URL = environment.apiUrl + "/api/user/signUp";
  public static readonly SIGN_IN_URL = environment.apiUrl + "/api/user/signIn";
  public static readonly USER_URL = environment.apiUrl + "/api/user/";
  public static readonly customer = "Customer";

  private customer: string;

  constructor(public httpClient: HttpClient,
              private toastr: ToastrService) {
    const user = localStorage.getItem(AuthService.customer);
    if (user) {
      this.customer = user;
    } else {
      this.customer = null;
    }
  }

  public signUp(email: string): Observable<void | {}> {
    const requestParam = {
      email: email
    };
    return this.httpClient.post(AuthService.SIGN_UP_URL, requestParam).pipe(
      map(res => {
        if(res){
          this.saveUserIntoLocalStorage(res);
        }else{
          this.toastr.error("ERROR")
        }
      }));
  }

  public signIn(email: string) : Observable<void | {}>{
    const requestParam = {
      email: email
    };
    return this.httpClient.post(AuthService.SIGN_IN_URL, requestParam).pipe(
      map(res => {
        if(res){
          this.saveUserIntoLocalStorage(res);
        }
      }));
  }

  public logout(): void {
    localStorage.removeItem(AuthService.customer);
    location.replace("/");
  }

  private saveUserIntoLocalStorage(value: any): void{
    localStorage.setItem(AuthService.customer, value.number);
    location.replace("/profile")
  }

  public getCustomer(): string{
    return this.customer;
  }

  public isAuthorized(): boolean{
    if(this.customer){
      return true;
    }else{
      return false;
    }
  }

  public getUserData(): Observable<UserData>{
    return this.httpClient.get<UserData>(AuthService.USER_URL).pipe(
      map(res => {
        if(res){
          return res;
        }else{
          this.toastr.error("ERROR")
        }
      }));

  }
}
