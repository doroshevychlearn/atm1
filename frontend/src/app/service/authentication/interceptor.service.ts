import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Router} from "@angular/router";
import {AuthService} from "./auth.service";
import {Observable} from "rxjs";

@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService,
              private router: Router) {
  }


  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let headers: HttpHeaders;

    if (this.authService.isAuthorized()) {
      headers = new HttpHeaders()
        .set("Customer", this.authService.getCustomer());
    }


    let request = req.clone({
      headers: headers
    });

    return next.handle(request);
  }
}
