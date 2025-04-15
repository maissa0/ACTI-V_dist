import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupForm: FormGroup = this.formBuilder.group({
    name: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    rememberMe: [false]
  });

  loading = false;
  error = '';
  hidePassword = true;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    // redirect to home if already logged in
    if (this.authService.currentUserValue) {
      this.router.navigate(['/ressources']);
    }
  }

  // convenience getter for easy access to form fields
  get f() { return this.signupForm.controls; }

  onSubmit() {
    if (this.signupForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.register(
      this.f['name'].value,
      this.f['lastName'].value,
      this.f['email'].value,
      this.f['password'].value
    ).subscribe({
      next: () => {
        this.router.navigate(['/ressources']);
      },
      error: error => {
        this.error = error?.error?.message || 'Une erreur est survenue';
        this.loading = false;
      }
    });
  }

  // Social sign-in methods
  signInWithGoogle() {
    // Implement Google sign-in
    console.log('Google sign-in clicked');
  }

  signInWithLinkedIn() {
    // Implement LinkedIn sign-in
    console.log('LinkedIn sign-in clicked');
  }

  signInWithSSO() {
    // Implement SSO sign-in
    console.log('SSO sign-in clicked');
  }
}
