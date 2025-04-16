import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private router: Router) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Get the auth token from AuthService
    const currentUser = this.authService.currentUserValue;
    console.log('Auth Interceptor:', { 
      url: request.url, 
      method: request.method,
      currentUser: currentUser ? 'exists' : 'null'
    });
    
    let token = null;
    
    // Handle different token formats
    if (currentUser) {
      if (typeof currentUser === 'string') {
        token = currentUser;
      } else if (currentUser.token) {
        token = currentUser.token;
      } else if (currentUser.jwtToken) {
        token = currentUser.jwtToken;
      }
    }
    
    // Only clone and modify if this is not a preflight OPTIONS request
    if (request.method !== 'OPTIONS') {
      // Check if this is a multipart form request (file upload)
      const isMultipartFormData = request.body instanceof FormData;
      
      const headers: any = {};
      
      // Only set Content-Type for non-multipart requests
      if (!isMultipartFormData) {
        headers['Content-Type'] = 'application/json';
      }
      
      // Add authorization if token exists
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
        console.log('Added authorization header with token:', token.substring(0, 15) + '...');
      }
      
      // Add X-XSRF-TOKEN for CSRF protection on state-changing methods
      if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(request.method)) {
        // For Spring-based backends, use X-XSRF-TOKEN
        headers['X-XSRF-TOKEN'] = 'nocheck';
        
        // Disable CSRF for this specific endpoint if needed
        if (request.url.includes('/api/auth/profile/update-basic-info') ||
            request.url.includes('/api/profile/picture')) {
          console.log('Adding CSRF exemption for profile update endpoint');
          headers['X-CSRF-EXEMPT'] = 'true';
        }
      }
      
      // Clone the request with the updated headers
      request = request.clone({
        setHeaders: headers,
        withCredentials: true  // Include cookies in cross-site requests
      });
    }

    // Forward the cloned request to the next handler
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('HTTP error:', error);
        
        // Handle 401 Unauthorized errors (token expired or invalid)
        if (error.status === 401) {
          console.log('401 Unauthorized error - logging out');
          // Automatically logout if 401 response returned from API
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        
        // Handle 403 Forbidden errors (CSRF or permission issues)
        if (error.status === 403) {
          console.log('403 Forbidden error - may be CSRF related');
          // Could potentially refresh the CSRF token here if needed
        }
        
        return throwError(() => error);
      })
    );
  }
} 