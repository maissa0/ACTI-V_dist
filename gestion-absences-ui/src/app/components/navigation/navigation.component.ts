import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent {
  navItems = [
    { title: 'Etudiants', route: '/students', icon: 'users' },
    { title: 'Classes', route: '/classes', icon: 'academic' },
    { title: 'Absences', route: '/absences', icon: 'calendar' },
    { title: 'Ajouter Etudiant', route: '/students/add', icon: 'user-plus' },
    { title: 'Marquer Absence', route: '/absences/add', icon: 'plus' },
  ];
}

