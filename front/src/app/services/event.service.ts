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

export interface Resource {
  id?: number;
  nom: string;
  quantite: number;
  type: ResourceType;
  eventId?: number;
}

export interface ResourceType {
  id?: number;
  nom: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  // Original API URL updated to correct port
  private originalApiUrl = 'http://localhost:5000/api/evenemment/events';
  // Resources API URL - Updated to match gateway configuration
  private resourcesApiUrl = 'http://localhost:5000/ressources/ressources';
  private resourceTypesApiUrl = 'http://localhost:5000/ressources/TypeRessource';
  
  // We'll use this URL in our requests
  private apiUrl = this.originalApiUrl;

  constructor(
    private http: HttpClient
  ) { }

  // Get headers with authorization token
  private getHeaders(): HttpHeaders {
    // Get the token from localStorage directly
    const token = localStorage.getItem('auth_token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      // Adding these headers might help with CORS issues in development
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'Origin, Content-Type, Accept, Authorization'
    });
  }

  // Error handling method
  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    
    if (error.status === 0) {
      // Client-side or network error
      if (error.error instanceof ProgressEvent && error.error.type === 'error') {
        errorMessage = 'Network error occurred. Please check your connection and ensure the server is running.';
      } else if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else {
        errorMessage = 'A CORS issue may be preventing connection to the API. Please check server configuration.';
      }
      console.error('A network error occurred:', error);
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
      headers: this.getHeaders(),
      // Disabling withCredentials to avoid CORS issues
      withCredentials: false
    }).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  // Get event by ID
  getEventById(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`, { 
      headers: this.getHeaders(),
      withCredentials: false
    }).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  // Create new event
  createEvent(event: Event): Observable<Event> {
    return this.http.post<Event>(this.apiUrl, event, { 
      headers: this.getHeaders(),
      withCredentials: false
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Update existing event
  updateEvent(id: number, event: Event): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${id}`, event, { 
      headers: this.getHeaders(),
      withCredentials: false
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Delete event
  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { 
      headers: this.getHeaders(),
      withCredentials: false
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Get resources for an event
  getResourcesByEventId(eventId: number): Observable<Resource[]> {
    return this.http.get<Resource[]>(`${this.resourcesApiUrl}/event/${eventId}`, {
      headers: this.getHeaders(),
      withCredentials: false
    }).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  // Get all resource types
  getAllResourceTypes(): Observable<ResourceType[]> {
    return this.http.get<ResourceType[]>(this.resourceTypesApiUrl, {
      headers: this.getHeaders(),
      withCredentials: false
    }).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  // Create new resource
  createResource(resource: Resource): Observable<Resource> {
    return this.http.post<Resource>(this.resourcesApiUrl, resource, {
      headers: this.getHeaders(),
      withCredentials: false
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Delete resource
  deleteResource(id: number): Observable<void> {
    return this.http.delete<void>(`${this.resourcesApiUrl}/${id}`, {
      headers: this.getHeaders(),
      withCredentials: false
    }).pipe(
      catchError(this.handleError)
    );
  }
} 