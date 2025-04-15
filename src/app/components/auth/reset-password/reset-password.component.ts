import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm: FormGroup;
  isSubmitting = false;
  error: string | null = null;
  success: string | null = null;
  token: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {
    this.resetPasswordForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });
  }

  ngOnInit() {
    // Get token from URL
    this.token = this.route.snapshot.queryParamMap.get('token');
    if (!this.token) {
      this.error = 'Invalid or expired password reset link';
      // Redirect to forgot-password page if no token is present
      setTimeout(() => {
        this.router.navigate(['/forgot-password']);
      }, 3000);
    }
  }

  // convenience getter for easy access to form fields
  get f() { return this.resetPasswordForm.controls; }

  passwordMatchValidator(g: FormGroup) {
    return g.get('password')?.value === g.get('confirmPassword')?.value
      ? null : { 'passwordMismatch': true };
  }

  onSubmit() {
    if (this.resetPasswordForm.invalid || !this.token) {
      return;
    }

    this.isSubmitting = true;
    this.error = null;
    this.success = null;

    this.authService.resetPassword(
      this.token,
      this.f['password'].value
    ).subscribe({
      next: () => {
        this.success = 'Your password has been reset successfully';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: error => {
        this.error = error?.error?.message || 'An error occurred while resetting your password';
        this.isSubmitting = false;
      }
    });
  }
}
