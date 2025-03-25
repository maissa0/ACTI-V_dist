import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Classe } from '../models/classe.model';

@Injectable({
  providedIn: 'root'
})
export class ClasseService {
  private apiUrl = 'http://localhost:8080/api/classes';

  constructor(private http: HttpClient) { }

  private handleError(error: HttpErrorResponse) {
    console.error('An error occurred:', error.error);
    console.error('Backend returned code:', error.status);
    console.error('Response body:', error.error);
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }

  getClasses(): Observable<Classe[]> {
    return this.http.get<Classe[]>(this.apiUrl).pipe(
      tap(data => console.log('Fetched classes:', data)),
      catchError(this.handleError)
    );
  }

  getClasse(id: number): Observable<Classe> {
    return this.http.get<Classe>(`${this.apiUrl}/${id}`).pipe(
      tap(data => console.log('Fetched class:', data)),
      catchError(this.handleError)
    );
  }

  createClasse(classe: Classe): Observable<Classe> {
    console.log('Creating class:', classe); // Add this line for debugging
    return this.http.post<Classe>(this.apiUrl, classe).pipe(
      tap(data => console.log('Created class:', data)),
      catchError(this.handleError)
    );
  }

  updateClasse(id: number, classe: Classe): Observable<Classe> {
    console.log('Updating class:', classe); // Add this line for debugging
    return this.http.put<Classe>(`${this.apiUrl}/${id}`, classe).pipe(
      tap(data => console.log('Updated class:', data)),
      catchError(this.handleError)
    );
  }

  deleteClasse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => console.log('Deleted class with id:', id)),
      catchError(this.handleError)
    );
  }
}

