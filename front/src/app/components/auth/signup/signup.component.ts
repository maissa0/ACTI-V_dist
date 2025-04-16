import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  signupForm: FormGroup = this.formBuilder.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
    firstName: ['', [Validators.required, Validators.maxLength(120)]],
    lastName: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]],
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
      this.router.navigate(['/home']);
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
      this.f['username'].value,
      this.f['email'].value,
      this.f['password'].value,
      this.f['firstName'].value,
      this.f['lastName'].value
    ).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        this.loading = false;
        
        // Show success message (optional)
        alert('Registration successful! Please log in.');
        
        // Redirect to login page
        this.router.navigate(['/login']);
      },
      error: error => {
        console.error('Registration error:', error);
        this.error = error?.error?.message || 'An error occurred during registration';
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
