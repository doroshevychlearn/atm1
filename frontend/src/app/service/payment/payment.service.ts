import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Observable} from "rxjs";
import {Statement} from "../../model/statement";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  public static readonly TRANSACTION_REPLENISH_URL = environment.apiUrl + "/api/transaction/replenish";
  public static readonly TRANSACTION_WITHDRAW_URL = environment.apiUrl + "/api/transaction/withdraw";
  public static readonly TRANSACTION_REMIT_URL = environment.apiUrl + "/api/transaction/remit";
  public static readonly MONTHLY_STATEMENT_URL = environment.apiUrl + "/api/transaction/statement/monthly";
  public static readonly YEARLY_STATEMENT_URL = environment.apiUrl + "/api/transaction/statement/yearly";

  constructor(public httpClient: HttpClient) { }

  public replenishMoney(replenish: number) : Observable<any>{
    const requestParam = {
      amount: replenish
    };
    return this.httpClient.post(PaymentService.TRANSACTION_REPLENISH_URL, requestParam).pipe(
      map(res => {
        return res;
      }));
  }

  public withdrawMoney(withdraw: number) : Observable<any>{
    const requestParam = {
      amount: withdraw
    };
    return this.httpClient.post(PaymentService.TRANSACTION_WITHDRAW_URL, requestParam).pipe(
      map(res => {
        return res;
      }));
  }

  public remitMoney(number: number, amount: number) : Observable<any>{
    const requestParam = {
      number: number,
      amount: amount
    };
    return this.httpClient.post(PaymentService.TRANSACTION_REMIT_URL, requestParam).pipe(
      map(res => {
        return res;
      }));
  }

  public getMonthlyStatement(): Observable<Statement>{
    return this.httpClient.get<Statement>(PaymentService.MONTHLY_STATEMENT_URL).pipe(
      map(res => {
        return res;
      }));

  }

  public getYearlyStatement(): Observable<Statement>{
    return this.httpClient.get<Statement>(PaymentService.YEARLY_STATEMENT_URL).pipe(
      map(res => {
        return res;
      }));

  }

}
