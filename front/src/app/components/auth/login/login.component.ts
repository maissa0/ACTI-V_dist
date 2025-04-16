import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup = this.formBuilder.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });
  loading = false;
  error = '';
  hidePassword = true;
  returnUrl: string = '/home';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {
    // redirect based on role if already logged in
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      // Check if user has admin role in the roles array
      const isAdmin = currentUser.roles && 
                      (currentUser.roles.includes('ROLE_ADMIN') || 
                       currentUser.roles.includes('ADMIN'));
      
      if (isAdmin) {
        this.router.navigate(['/admin/dashboard']);
      } else {
        this.router.navigate(['/home']);
      }
    }
  }

  ngOnInit() {
    // Get return url from route parameters or default to '/home'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/home';
  }

  // convenience getter for easy access to form fields
  get f() { return this.loginForm.controls; }

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login(this.f['username'].value, this.f['password'].value)
      .subscribe({
        next: (response) => {
          console.log('Login successful:', response);
          
          // Check user role and redirect accordingly
          const currentUser = this.authService.currentUserValue;
          if (currentUser) {
            // Check if user has admin role in the roles array
            const isAdmin = currentUser.roles && 
                          (currentUser.roles.includes('ROLE_ADMIN') || 
                           currentUser.roles.includes('ADMIN'));
            
            if (isAdmin) {
              // If user is admin, redirect to dashboard
              this.router.navigate(['/admin/dashboard']);
            } else {
              // For regular users, use the return URL or default to home
              this.router.navigate([this.returnUrl]);
            }
          }
        },
        error: error => {
          console.error('Login error:', error);
          this.error = error?.error?.message || 'Invalid username or password. Please try again.';
          this.loading = false;
        },
        complete: () => {
          this.loading = false;
        }
      });
  }
} 