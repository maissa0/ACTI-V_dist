import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(private router: Router, private authService: AuthService) {}
  
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const currentUser = this.authService.currentUserValue;
    console.log('AuthGuard check:', { currentUser, isAuthenticated: this.authService.isAuthenticated() });
    
    // Check various possibilities for token existence
    const hasToken = 
      (currentUser && currentUser.token) || 
      (currentUser && typeof currentUser === 'string') ||
      (currentUser && currentUser.jwtToken);
    
    if (hasToken) {
      // User is logged in, so return true
      console.log('User is authenticated, allowing access to:', state.url);
      return true;
    }
    
    // Not logged in, redirect to login page with return url
    console.log('User is not authenticated, redirecting to login');
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
} 