import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Student } from '../models/student.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private apiUrl = 'http://localhost:8080/api/etudiants';

  constructor(private http: HttpClient) { }

  private handleError(error: HttpErrorResponse) {
    console.error('An error occurred:', error.error);
    console.error('Backend returned code:', error.status);
    console.error('Response body:', error.error);
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }

  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(this.apiUrl).pipe(
      tap(data => console.log('Fetched students:', data)),
      catchError(this.handleError)
    );
  }

  getStudent(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/${id}`).pipe(
      tap(data => console.log('Fetched student:', data)),
      catchError(this.handleError)
    );
  }

  createStudent(student: Student): Observable<Student> {
    console.log('Creating student:', student); // Add this line for debugging
    return this.http.post<Student>(this.apiUrl, student).pipe(
      tap(data => console.log('Created student:', data)),
      catchError(this.handleError)
    );
  }

  updateStudent(id: number, student: Student): Observable<Student> {
    console.log('Updating student:', student); // Add this line for debugging
    return this.http.put<Student>(`${this.apiUrl}/${id}`, student).pipe(
      tap(data => console.log('Updated student:', data)),
      catchError(this.handleError)
    );
  }

  deleteStudent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => console.log('Deleted student with id:', id)),
      catchError(this.handleError)
    );
  }
}

