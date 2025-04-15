import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  forgotPasswordForm: FormGroup;
  isSubmitting = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.forgotPasswordForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    if (this.forgotPasswordForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const email = this.forgotPasswordForm.get('email')?.value;

    // For development/testing purposes
    // Remove this block when backend is ready
    const mockToken = 'mock-reset-token-' + Math.random().toString(36).substr(2);
    this.successMessage = 'Password reset instructions have been sent to your email.';
    setTimeout(() => {
      this.router.navigate(['/reset-password'], { 
        queryParams: { token: mockToken }
      });
    }, 1500);
    return;

    // Uncomment this when backend is ready
    /*
    this.authService.forgotPassword(email).subscribe({
      next: (response) => {
        this.successMessage = 'Password reset instructions have been sent to your email.';
        const resetToken = response.token;
        setTimeout(() => {
          this.router.navigate(['/reset-password'], { 
            queryParams: { token: resetToken }
          });
        }, 1500);
      },
      error: error => {
        this.errorMessage = error?.error?.message || 'An error occurred while processing your request.';
        this.isSubmitting = false;
      }
    });
    */
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
