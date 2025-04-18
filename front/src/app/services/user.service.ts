import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { BasicUserInfo } from '../models/basic-user-info.model';
import { AuthService } from './auth.service';
import { Competence } from '../models/competence.model';
import { GeminiAiService } from './gemini-ai.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:5000/api/userAuth/api/users';
  private competenceApiUrl = 'http://localhost:5000/api/competence/competences';
  private userAuthPublicApi = 'http://localhost:5000/api/userAuth/api/public';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private geminiAiService: GeminiAiService
  ) { }

  // Get all users with ROLE_USER
  getAllUsersWithRoleUser(): Observable<BasicUserInfo[]> {
    return this.http.get<BasicUserInfo[]>(`${this.apiUrl}/role/user`, {
      headers: this.getAuthHeaders()
    });
  }

  // Get user competences by user ID
  getUserCompetences(userId: number): Observable<Competence[]> {
    return this.http.get<Competence[]>(
      `${this.competenceApiUrl}/user/${userId}`, 
      { headers: this.getAuthHeaders() }
    );
  }

  // Get user competences by username - this will handle the ID lookup on the backend
  getUserCompetencesByUsername(username: string): Observable<Competence[]> {
    return this.http.get<Competence[]>(
      `${this.competenceApiUrl}/username/${username}`, 
      { headers: this.getAuthHeaders() }
    );
  }

  // Simple method to get user ID by username - no authentication needed
  getSimpleUserIdByUsername(username: string): Observable<number> {
    return this.http.get<number>(`${this.userAuthPublicApi}/simple/user/${username}`);
  }

  // Generate PDF report for a user
  generateUserReport(userId: number): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/report/${userId}`, 
      { 
        headers: this.getAuthHeaders(),
        responseType: 'blob'
      }
    );
  }

  // Get user ID from username
  getUserIdByUsername(username: string): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/id/${username}`,
      { headers: this.getAuthHeaders() }
    );
  }

  // Generate PDF report for a user with AI-generated summary
  generateUserReportWithAiSummary(userId: number): Observable<Blob> {
    // First, get the user competences
    return this.getUserCompetences(userId).pipe(
      switchMap(competences => {
        // Get the user details using the same userId
        return this.getUserDetailsById(userId).pipe(
          switchMap(userDetails => {
            // Create a list of competence names for the AI to use
            const competenceNames = competences.map(comp => 
              `${comp.name} (Level: ${comp.level}/${comp.description ? ', ' + comp.description : ''})`
            );
            
            // Skip AI summary if there are no competences
            if (competenceNames.length === 0) {
              return this.generateUserReport(userId);
            }
            
            // Get AI-generated summary based on competences
            return this.generateAiSummaryForUser(userDetails.username, competenceNames).pipe(
              switchMap(aiSummary => {
                // Now call the endpoint to generate the PDF with the AI summary
                return this.http.post(
                  `${this.apiUrl}/report/${userId}/with-summary`, 
                  { aiSummary },
                  { 
                    headers: this.getAuthHeaders(),
                    responseType: 'blob'
                  }
                );
              }),
              catchError(error => {
                // If AI summary fails, fall back to regular PDF
                console.error('Error generating AI summary:', error);
                return this.generateUserReport(userId);
              })
            );
          })
        );
      })
    );
  }

  // Helper method to get user details by ID
  private getUserDetailsById(userId: number): Observable<BasicUserInfo> {
    return this.http.get<BasicUserInfo>(
      `${this.apiUrl}/${userId}`,
      { headers: this.getAuthHeaders() }
    );
  }

  // Helper method to generate AI summary
  private generateAiSummaryForUser(username: string, competences: string[]): Observable<string> {
    const background = `User ${username} has the following competences: ${competences.join(', ')}`;
    
    return this.geminiAiService.getCompetenceRecommendations(background).pipe(
      catchError(error => {
        console.error('Error calling Gemini AI service:', error);
        return of('Unable to generate AI summary at this time.');
      })
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

  // Get username by user ID
  getUsernameById(userId: number): Observable<string> {
    return this.http.get<string>(
      `${this.userAuthPublicApi}/username/${userId}`,
      { responseType: 'text' as 'json' }
    ).pipe(
      catchError(error => {
        console.error('Error getting username by ID:', error);
        return of(`User ${userId}`); // Return a default value in case of error
      })
    );
  }
}
