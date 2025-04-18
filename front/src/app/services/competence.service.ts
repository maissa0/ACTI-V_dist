import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Competence, CompetenceRequest } from '../models/competence.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CompetenceService {
  private apiUrl = 'http://localhost:5000/api/competence/competences'; // Update with your competence microservice URL

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  // Get current user's competences
  getUserCompetences(): Observable<Competence[]> {
    return this.http.get<Competence[]>(`${this.apiUrl}/me`, {
      headers: this.getAuthHeaders()
    });
  }

  // Add a new competence
  addCompetence(competence: CompetenceRequest): Observable<Competence> {
    return this.http.post<Competence>(`${this.apiUrl}`, competence, {
      headers: this.getAuthHeaders()
    });
  }

  // Delete a competence
  deleteCompetence(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders()
    });
  }

  // Helper method to get auth headers
  private getAuthHeaders(): HttpHeaders {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.token) {
      console.error('No authentication token available');
      return new HttpHeaders();
    }
    
    // Determine token format (Bearer or just the token)
    const token = currentUser.token;
    const tokenValue = typeof token === 'string' ? token : token.token || token.accessToken || '';
    const authHeader = tokenValue.startsWith('Bearer ') ? tokenValue : `Bearer ${tokenValue}`;
    
    return new HttpHeaders({
      'Authorization': authHeader,
      'Content-Type': 'application/json'
    });
  }
} 