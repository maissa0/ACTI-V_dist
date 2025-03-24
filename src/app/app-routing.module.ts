import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RessourcesComponent } from './components/ressources/ressources.component';

const routes: Routes = [
  { path: 'ressources', component: RessourcesComponent },


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
