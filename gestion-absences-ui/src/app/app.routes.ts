// app.routes.ts
import { Routes } from '@angular/router';
import { StudentListComponent } from './components/student-list/student-list.component';
import { StudentFormComponent } from './components/student-form/student-form.component';
import { ClasseListComponent } from './components/classe-list/classe-list.component';
import { ClasseFormComponent } from './components/classe-form/classe-form.component';
import { AbsenceListComponent } from './components/absence-list/absence-list.component';
import { AbsenceFormComponent } from './components/absence-form/absence-form.component';

export const routes: Routes = [
  { path: '', redirectTo: '/students', pathMatch: 'full' },
  { path: 'students', component: StudentListComponent },
  { path: 'students/add', component: StudentFormComponent },
  { path: 'students/edit/:id', component: StudentFormComponent },
  { path: 'classes', component: ClasseListComponent },
  { path: 'classes/add', component: ClasseFormComponent },
  { path: 'classes/edit/:id', component: ClasseFormComponent },
  { path: 'absences', component: AbsenceListComponent },
  { path: 'absences/add', component: AbsenceFormComponent },
  { path: 'absences/edit/:id', component: AbsenceFormComponent },
  // Add a wildcard route to handle undefined routes
  { path: '**', redirectTo: '/students' }
];
