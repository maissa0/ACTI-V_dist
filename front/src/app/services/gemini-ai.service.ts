import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class GeminiAiService {
  private apiUrl = 'http://localhost:8082/api/competence/gemini'; // Base URL for Gemini AI endpoints

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  /**
   * Get competence recommendations based on user background
   * @param userBackground The user's professional background description
   */
  getCompetenceRecommendations(userBackground: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/recommendations`, 
      { userBackground }, 
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Analyze a competence description and get improvement suggestions
   * @param competenceDescription The description to analyze
   */
  analyzeCompetenceDescription(competenceDescription: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/analyze`, 
      { competenceDescription }, 
      { headers: this.getAuthHeaders() }
    );
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