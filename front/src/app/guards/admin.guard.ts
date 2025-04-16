import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    // Get the current user
    const currentUser = this.authService.currentUserValue;
    
    // Check if user is logged in and has admin role
    if (currentUser && currentUser.role === 'ROLE_ADMIN') {
      return true;
    }
    
    // If not admin, redirect to dashboard
    this.router.navigate(['/admin/dashboard']);
    return false;
  }
} 