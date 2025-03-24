import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TypeRessourceServiceService {
  private apiUrl = 'http://localhost:8086/TypeRessource'; // Your Spring Boot API URL


  constructor(private http: HttpClient) {}

  // Get all types of ressources
  getAllTypes(): Observable<{ id: number; nom: string }[]> {
    return this.http.get<{ id: number; nom: string }[]>(this.apiUrl);
  }

  // Get specific type by ID (optional, if needed)
  getTypeById(id: number): Observable<{ id: number; nom: string }> {
    return this.http.get<{ id: number; nom: string }>(`${this.apiUrl}/${id}`);
  }}
