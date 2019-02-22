import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {AuthenticationComponent} from "./component/authentication/authentication.component";

const routes: Routes = [
  {path: '', component: AuthenticationComponent},
  { path: 'profile', loadChildren: 'src/app/component/profile/profile.module#ProfileModule' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
