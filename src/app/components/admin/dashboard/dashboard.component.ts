import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  currentSection: string = 'dashboard';
  profileForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.profileForm = this.fb.group({
      name: ['John', Validators.required],
      surname: ['Doe', Validators.required],
      headline: ['UI/UX Developer', Validators.required],
      currentPosition: ['UI/UX Developer at Boston', Validators.required],
      location: ['Boston University', Validators.required],
      country: ['USA', Validators.required],
      stateRegion: ['Boston', Validators.required]
    });
  }

  showSection(section: string): void {
    this.currentSection = section;
  }

  onSubmitProfile(): void {
    if (this.profileForm.valid) {
      console.log(this.profileForm.value);
      // Add your save logic here
    }
  }
}
