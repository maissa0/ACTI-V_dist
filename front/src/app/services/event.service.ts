import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

export interface Event {
  id?: number;
  name: string;
  description: string;
  type: string;
  dateDeb: Date;
  dateFin: Date;
  userId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  // Use the environment variable path or fallback to localhost
  private apiUrl = 'http://localhost:5000/api/evenemment/events';

  constructor(
    private http: HttpClient
  ) { }

  // Get headers with authorization token
  private getHeaders(): HttpHeaders {
    // Get the token from localStorage directly
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Error handling method
  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    
    if (error.status === 0) {
      // Client-side or network error
      errorMessage = 'Network error occurred. Please check your connection and ensure the server is running.';
      console.error('A network error occurred:', error.error);
    } else {
      // Server-side error
      errorMessage = `Server returned code ${error.status}, error message: ${error.message}`;
      console.error('Backend returned code:', error.status, 'body was:', error.error);
    }
    
    return throwError(() => new Error(errorMessage));
  }

  // Get all events (admin or public view)
  getAllEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl, { 
      headers: this.getHeaders() 
    }).pipe(
      retry(2), // Retry failed requests up to 2 times
      catchError(this.handleError)
    );
  }

  // Get current user's events
  getMyEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/my-events`, { 
      headers: this.getHeaders() 
    }).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  // Get event by ID
  getEventById(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`, { 
      headers: this.getHeaders() 
    }).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  // Create new event
  createEvent(event: Event): Observable<Event> {
    return this.http.post<Event>(this.apiUrl, event, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Update existing event
  updateEvent(id: number, event: Event): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${id}`, event, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Delete event
  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(this.handleError)
    );
  }
} 