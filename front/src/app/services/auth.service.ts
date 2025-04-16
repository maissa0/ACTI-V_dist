import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;
  private apiUrl = 'http://localhost:8081/api/userAuth/api'; // Update with your backend URL

  constructor(private http: HttpClient, private router: Router) {
    // Get from localStorage and parse
    let storedUser = localStorage.getItem('currentUser');
    let parsedUser = null;
    
    if (storedUser) {
      try {
        parsedUser = JSON.parse(storedUser);
        console.log('Loaded user from localStorage:', parsedUser);
      } catch (e) {
        console.error('Error parsing stored user:', e);
        localStorage.removeItem('currentUser');
      }
    }
    
    // Ensure the user object has the expected structure
    this.currentUserSubject = new BehaviorSubject<any>(parsedUser);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue() {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string) {
    return this.http.post<any>(`${this.apiUrl}/auth/public/signin`, { username, password })
      .pipe(map(response => {
        // The response is the full token data
        console.log('Login response:', response);
        
        // Handle both direct token response or nested structure
        const userData = {
          username: response.username || (response.currentUser ? response.currentUser.username : username),
          roles: response.roles || (response.currentUser ? response.currentUser.roles : ['USER']),
          token: response.jwtToken || response.token || response
        };
        
        console.log('Processed user data:', userData);
        
        // store user details and jwt token in local storage
        localStorage.setItem('currentUser', JSON.stringify(userData));
        this.currentUserSubject.next(userData);
        return userData;
      }));
  }

  register(username: string, email: string, password: string, firstName: string, lastName: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/public/signup`, { 
      username, 
      email, 
      password,
      firstName,
      lastName
    });
    // No longer automatically setting the user as logged in
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['/home']);
  }

  forgotPassword(email: string): Observable<any> {
    // Using HttpParams to pass the email as a query parameter
    const params = new HttpParams().set('email', email);
    return this.http.post<any>(`${this.apiUrl}/auth/public/forgot-password`, null, { params });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    // Using HttpParams to pass token and newPassword as query parameters
    const params = new HttpParams()
      .set('token', token)
      .set('newPassword', newPassword);
    return this.http.post<any>(`${this.apiUrl}/auth/public/reset-password`, null, { params });
  }

  isAuthenticated(): boolean {
    const currentUser = this.currentUserValue;
    console.log('Checking authentication with user:', currentUser);
    
    if (!currentUser) {
      console.log('Not authenticated: No current user');
      return false;
    }
    
    // Check various token locations based on the screenshot
    const hasToken = 
      (currentUser.token) || 
      (typeof currentUser === 'string') || 
      (currentUser.jwtToken);
    
    if (!hasToken) {
      console.log('Not authenticated: No token found in current user');
      return false;
    }
    
    console.log('User is authenticated');
    return true;
  }

  updateProfile(userData: any) {
    return this.http.put(`${this.apiUrl}/users/profile`, userData)
      .pipe(map(user => {
        const updatedUser = { ...this.currentUserValue, ...user };
        localStorage.setItem('currentUser', JSON.stringify(updatedUser));
        this.currentUserSubject.next(updatedUser);
        return updatedUser;
      }));
  }

  getBasicUserInfo(): Observable<any> {
    return this.http.get(`${this.apiUrl}/auth/user/basic-info`);
  }

  updateBasicUserInfo(userData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/auth/profile/update-basic-info`, userData)
      .pipe(map(response => {
        // Update stored user information with the response data
        const currentUser = this.currentUserValue;
        if (currentUser) {
          const updatedUser = { 
            ...currentUser,
            username: userData.userName || currentUser.username, 
            email: userData.email || currentUser.email
          };
          localStorage.setItem('currentUser', JSON.stringify(updatedUser));
          this.currentUserSubject.next(updatedUser);
        }
        return response;
      }));
  }

  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    const params = new HttpParams()
      .set('currentPassword', currentPassword)
      .set('newPassword', newPassword);
      
    return this.http.post(`${this.apiUrl}/auth/change-password`, null, { params });
  }
} 