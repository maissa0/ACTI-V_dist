import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Ressource {
  id: number;
  nom: string;
  quantite: number;
  type: { id: number; nom: string };
  eventId?: number;
} 

@Injectable({
  providedIn: 'root'
})
export class RessourceService {
  // Ensure we're using port 5000 (gateway) instead of accessing the microservice directly
  private apiUrl = 'http://localhost:5000/ressources/ressources'; // API URL through gateway

  constructor(private http: HttpClient) {}

  // Get all ressources
  getAllRessources(): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(this.apiUrl);
  }

  // Get ressource by ID
  getRessourceById(id: number): Observable<Ressource> {
    return this.http.get<Ressource>(`${this.apiUrl}/${id}`);
  }

  // Create a new ressource
  createRessource(ressource: Ressource): Observable<Ressource> {
    console.log('Creating resource through gateway:', ressource);
    return this.http.post<Ressource>(this.apiUrl, ressource);
  }

  // Update an existing ressource
  updateRessource(ressource: Ressource): Observable<Ressource> {
    console.log('Updating resource through gateway:', ressource);
    return this.http.put<Ressource>(this.apiUrl, ressource);
  }

  // Delete a ressource by ID
  deleteRessource(id: number): Observable<Ressource> {
    console.log('Deleting resource through gateway, ID:', id);
    return this.http.delete<Ressource>(`${this.apiUrl}/${id}`);
  }

  // Get ressources by type
  getRessourcesByType(typeId: number): Observable<Ressource[]> {
    console.log('Getting resources by type through gateway, typeId:', typeId);
    return this.http.get<Ressource[]>(`${this.apiUrl}/type/${typeId}`);
  }
  
  // Get resources for an event
  getRessourcesByEventId(eventId: number): Observable<Ressource[]> {
    console.log('Getting resources for event through gateway, eventId:', eventId);
    return this.http.get<Ressource[]>(`${this.apiUrl}/event/${eventId}`);
  }
}
