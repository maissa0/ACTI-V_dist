import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';


export interface Mission {
  id?: number;
  nomMission: string;
  description: string;
  dateDebut: Date | string;
  statut: string;
  competencesRequises?: string[] | string;
  evenementId: number;
  responsableId: number;
}

@Injectable({
  providedIn: 'root'
})
export class MissionService {

  private originalApiUrl = 'http://localhost:5000/missionspath/api/missions';
  // Resources API URL - Updated to match gateway configuration
  

  constructor(private http: HttpClient) { }

  getMissionsByEventId(eventId: number): Observable<Mission[]> {
    return this.http.get<Mission[]>(`${this.originalApiUrl}/event/${eventId}`)
      .pipe(
        catchError(this.handleError<Mission[]>('getMissionsByEventId', []))
      );
  }

  getMissionById(id: number): Observable<Mission> {
    return this.http.get<Mission>(`${this.originalApiUrl}/get/${id}`)
      .pipe(
        catchError(this.handleError<Mission>('getMissionById'))
      );
  }

  createMission(mission: Mission): Observable<Mission> {
    // Create a copy of the mission to modify
    const missionToSend = { ...mission };

    // Handle competencesRequises - ensure it's an array
    if (typeof missionToSend.competencesRequises === 'string') {
      if (missionToSend.competencesRequises.trim() !== '') {
        // Split by commas if it's a non-empty string
        missionToSend.competencesRequises = missionToSend.competencesRequises
          .split(',')
          .map(item => item.trim())
          .filter(item => item !== '');
      } else {
        // Empty string becomes empty array
        missionToSend.competencesRequises = [];
      }
    } else if (missionToSend.competencesRequises === null || missionToSend.competencesRequises === undefined) {
      // Null or undefined becomes empty array
      missionToSend.competencesRequises = [];
    }
    // If it's already an array, we leave it as is
    
    console.log('Sending mission data:', JSON.stringify(missionToSend));
    
    return this.http.post<Mission>(`${this.originalApiUrl}/create`, missionToSend)
      .pipe(
        catchError(this.handleError<Mission>('createMission'))
      );
  }

  updateMission(id: number, mission: Mission): Observable<Mission> {
    // Create a copy of the mission to modify
    const missionToSend = { ...mission };

    // Handle competencesRequises - ensure it's an array
    if (typeof missionToSend.competencesRequises === 'string') {
      if (missionToSend.competencesRequises.trim() !== '') {
        // Split by commas if it's a non-empty string
        missionToSend.competencesRequises = missionToSend.competencesRequises
          .split(',')
          .map(item => item.trim())
          .filter(item => item !== '');
      } else {
        // Empty string becomes empty array
        missionToSend.competencesRequises = [];
      }
    } else if (missionToSend.competencesRequises === null || missionToSend.competencesRequises === undefined) {
      // Null or undefined becomes empty array
      missionToSend.competencesRequises = [];
    }
    // If it's already an array, we leave it as is
    
    console.log('Sending mission update data:', JSON.stringify(missionToSend));
    
    return this.http.put<Mission>(`${this.originalApiUrl}/update/${id}`, missionToSend)
      .pipe(
        catchError(this.handleError<Mission>('updateMission'))
      );
  }

  deleteMission(id: number): Observable<any> {
    return this.http.delete(`${this.originalApiUrl}/delete/${id}`)
      .pipe(
        catchError(this.handleError<any>('deleteMission'))
      );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      // Let the app keep running by returning an empty result
      return of(result as T);
    };
  }
} 