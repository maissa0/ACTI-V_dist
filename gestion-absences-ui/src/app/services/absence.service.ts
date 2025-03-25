import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Absence } from '../models/absence.model';

@Injectable({
  providedIn: 'root'
})
export class AbsenceService {
  private apiUrl = 'http://localhost:8080/api/absences';

  constructor(private http: HttpClient) { }

  private handleError(error: HttpErrorResponse) {
    console.error('An error occurred:', error.error);
    console.error('Backend returned code:', error.status);
    console.error('Response body:', error.error);
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }

  getAbsences(): Observable<Absence[]> {
    return this.http.get<Absence[]>(this.apiUrl).pipe(
      tap(data => console.log('Fetched absences:', data)),
      catchError(this.handleError)
    );
  }

  getAbsence(id: number): Observable<Absence> {
    return this.http.get<Absence>(`${this.apiUrl}/${id}`).pipe(
      tap(data => console.log('Fetched absence:', data)),
      catchError(this.handleError)
    );
  }

  createAbsence(absence: Absence): Observable<Absence> {
    console.log('Creating absence:', absence); // Add this line for debugging
    return this.http.post<Absence>(this.apiUrl, absence).pipe(
      tap(data => console.log('Created absence:', data)),
      catchError(this.handleError)
    );
  }

  updateAbsence(id: number, absence: Absence): Observable<Absence> {
    return this.http.put<Absence>(`${this.apiUrl}/${id}`, absence).pipe(
      tap(data => console.log('Updated absence:', data)),
      catchError(this.handleError)
    );
  }

  deleteAbsence(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => console.log('Deleted absence with id:', id)),
      catchError(this.handleError)
    );
  }
}

