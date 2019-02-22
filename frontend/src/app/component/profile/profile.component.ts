import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../service/authentication/auth.service";
import {UserData} from "../../model/user-data";
import {PaymentService} from "../../service/payment/payment.service";
import {ToastrService} from "ngx-toastr";
import {MatTabChangeEvent} from "@angular/material";
import {Statement} from "../../model/statement";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {


  userData: UserData;
  replenish: number;
  withdraw: number;
  monthlyStatement: Statement;
  yearlyStatement: Statement;

  constructor(public authService: AuthService,
              public paymentService: PaymentService,
              public toastr: ToastrService) { }

  ngOnInit() {
    if(!this.authService.isAuthorized()){
      location.replace("/");
    }else{
     this.authService.getUserData().subscribe(value => {
       this.userData = value;
     }, error => {
       this.toastr.error(error.error.message);
     });
    }
  }

  replenishMoney(){
    if(this.replenish && this.replenish > 0){
      this.paymentService.replenishMoney(this.replenish).subscribe(value => {
        if(value){
          this.toastr.success("Success payment!");
          setTimeout(()=>{ location.reload() }, 2000);
        }
      }, error => {
        this.toastr.error(error.error.message);
      });
    }
  }

  withdrawMoney(){
    if(this.withdraw && this.withdraw > 0){
      this.paymentService.withdrawMoney(this.withdraw).subscribe(value => {
        if(value){
          this.toastr.success("Success withdraw!");
          setTimeout(()=>{ location.reload() }, 2000);
        }
      }, error => {
        this.toastr.error(error.error.message);
      });
    }
  }

  onLinkClick(event: MatTabChangeEvent) {
    if(event.index === 3){
      this.paymentService.getMonthlyStatement().subscribe(value => {
        this.monthlyStatement = value;
      }, error => {
        this.toastr.error(error.error.message);
      })
    }else if(event.index === 4){
      this.paymentService.getYearlyStatement().subscribe(value => {
        this.yearlyStatement = value;
      }, error => {
        this.toastr.error(error.error.message);
      })

    }
  }

}
