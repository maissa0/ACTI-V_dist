import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, of, switchMap, forkJoin } from 'rxjs';
import { catchError, retry, map } from 'rxjs/operators';
import { UserService } from './user.service';

export interface Feedback {
  id?: string;
  evenementId: number;
  note: number;
  commentaire: string;
  userId?: number; // Optional as it can be extracted from token by backend
  date?: Date; // Add date property for when feedback was submitted
  username?: string; // Username for display purposes
}

export interface FeedbackStatistics {
  average: number;
  distribution: {[key: number]: number};
  count: number;
  min?: number;
  max?: number;
}

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private apiUrl = 'http://localhost:5000/eventeval/evaluations';
  private userAuthApiUrl = 'http://localhost:5000/api/userAuth/api/users';

  constructor(private http: HttpClient, private userService: UserService) { }

  // Get headers with authorization token
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  /**
   * Get user ID from JWT token
   * Uses the backend endpoint that extracts username from token and returns the user ID
   */
  getUserIdByUsername(): Observable<number> {
    return this.http.get<number>(`${this.userAuthApiUrl}/username`, {
      headers: this.getHeaders()
    }).pipe(
      retry(1),
      catchError(error => {
        console.error('Error getting user ID from token:', error);
        return throwError(() => new Error(`Failed to get user ID: ${error.message || 'Unknown error'}`));
      })
    );
  }

  // Error handling method
  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    
    if (error.status === 0) {
      errorMessage = 'Network error occurred. Please check your connection.';
      console.error('A network error occurred:', error);
    } else {
      errorMessage = `Server returned code ${error.status}, error message: ${error.message}`;
      console.error('Backend returned code:', error.status, 'body was:', error.error);
    }
    
    return throwError(() => new Error(errorMessage));
  }

  // Get all feedbacks for an event
  getEventFeedbacks(eventId: number): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/evenement/${eventId}`, { 
      headers: this.getHeaders() 
    }).pipe(
      retry(1),
      switchMap(feedbacks => {
        // If no feedbacks, return empty array
        if (!feedbacks || feedbacks.length === 0) {
          return of([]);
        }
        
        // Get usernames for all feedbacks with userId
        const feedbacksWithUserNames$ = feedbacks.map(feedback => {
          if (feedback.userId) {
            return this.userService.getUsernameById(feedback.userId).pipe(
              map(username => ({
                ...feedback,
                username: username
              }))
            );
          } else {
            return of({
              ...feedback,
              username: 'Anonymous'
            });
          }
        });
        
        // Combine all results
        return forkJoin(feedbacksWithUserNames$);
      }),
      catchError(error => {
        console.error('Error loading feedbacks:', error);
        return this.handleError(error);
      })
    );
  }

  // Get current user's feedback for an event
  getUserFeedbackForEvent(eventId: number): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/evenement/${eventId}/user`, { 
      headers: this.getHeaders() 
    }).pipe(
      retry(1),
      switchMap(feedbacks => {
        // If no feedbacks, return empty array
        if (!feedbacks || feedbacks.length === 0) {
          return of([]);
        }
        
        // Get usernames for all feedbacks with userId
        const feedbacksWithUserNames$ = feedbacks.map(feedback => {
          if (feedback.userId) {
            return this.userService.getUsernameById(feedback.userId).pipe(
              map(username => ({
                ...feedback,
                username: username
              }))
            );
          } else {
            return of({
              ...feedback,
              username: 'Anonymous'
            });
          }
        });
        
        // Combine all results
        return forkJoin(feedbacksWithUserNames$);
      }),
      catchError(error => {
        console.error('Error loading user feedback:', error);
        return this.handleError(error);
      })
    );
  }

  // Create new feedback
  createFeedback(eventId: number, feedback: Feedback): Observable<Feedback> {
    // Create evaluation object with required fields
    const evaluationData: Feedback = {
      evenementId: eventId,
      note: feedback.note,
      commentaire: feedback.commentaire,
      userId: feedback.userId // Will be extracted from token if null
    };
    
    console.log('Creating feedback with payload:', evaluationData);
    
    // Send to the backend endpoint which will extract userId from token if needed
    return this.http.post<Feedback>(
      `${this.apiUrl}/evenement/${eventId}/create`, 
      evaluationData, 
      { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Error creating feedback:', error);
        return throwError(() => new Error(`Failed to create feedback: ${error.message || 'Unknown error'}`));
      })
    );
  }

  // Update existing feedback
  updateFeedback(id: string, feedback: Feedback): Observable<Feedback> {
    // Ensure we're sending exactly what the backend expects
    const payload: Feedback = {
      evenementId: feedback.evenementId,
      note: feedback.note,
      commentaire: feedback.commentaire
      // userId will be extracted from the token by the backend
    };
    
    console.log('Updating feedback with payload:', payload);
    
    return this.http.put<Feedback>(`${this.apiUrl}/update/${id}`, payload, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(error => {
        console.error('Error updating feedback:', error);
        return throwError(() => new Error(`Failed to update feedback: ${error.message || 'Unknown error'}`));
      })
    );
  }

  // Delete feedback
  deleteFeedback(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Get average rating for an event
  getAverageRating(eventId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/evenement/${eventId}/rating/average`, { 
      headers: this.getHeaders() 
    }).pipe(
      retry(1),
      catchError(error => {
        console.error('Error fetching average rating:', error);
        return of(0); // Return 0 as default value on error
      })
    );
  }

  // Get rating distribution for an event
  getRatingDistribution(eventId: number): Observable<{[key: number]: number}> {
    return this.http.get<{[key: number]: number}>(`${this.apiUrl}/evenement/${eventId}/rating/distribution`, { 
      headers: this.getHeaders() 
    }).pipe(
      retry(1),
      catchError(error => {
        console.error('Error fetching rating distribution:', error);
        return of({1: 0, 2: 0, 3: 0, 4: 0, 5: 0}); // Return empty distribution on error
      })
    );
  }

  // Get statistics for an event rating
  getRatingStatistics(eventId: number): Observable<FeedbackStatistics> {
    return this.http.get<FeedbackStatistics>(`${this.apiUrl}/evenement/${eventId}/rating/statistics`, { 
      headers: this.getHeaders() 
    }).pipe(
      retry(1),
      catchError(error => {
        console.error('Error fetching rating statistics:', error);
        return of({
          average: 0,
          distribution: {1: 0, 2: 0, 3: 0, 4: 0, 5: 0},
          count: 0
        }); // Return empty statistics on error
      })
    );
  }
} 