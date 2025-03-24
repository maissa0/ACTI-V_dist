import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';



export interface Ressource {
  id: number;
  nom: string;
  quantite: number;
  type: { id: number; nom: string };
} 

@Injectable({
  providedIn: 'root'
})
export class RessourceService {

  private apiUrl = 'http://localhost:8086/ressources'; // Your Spring Boot API URL

  constructor(private http: HttpClient) {}

  // ✅ Get all ressources
  getAllRessources(): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(this.apiUrl);
  }

  // ✅ Get ressource by ID
  getRessourceById(id: number): Observable<Ressource> {
    return this.http.get<Ressource>(`${this.apiUrl}/${id}`);
  }

  // ✅ Create a new ressource
  createRessource(ressource: Ressource): Observable<Ressource> {
    return this.http.post<Ressource>(this.apiUrl, ressource);
  }

  // ✅ Update an existing ressource
  updateRessource(ressource: Ressource): Observable<Ressource> {
    return this.http.put<Ressource>(this.apiUrl, ressource);
  }

  // ✅ Delete a ressource by ID
  deleteRessource(id: number): Observable<Ressource> {
    return this.http.delete<Ressource>(`${this.apiUrl}/${id}`);
  
  }
  getRessourcesByType(typeId: number): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(`${this.apiUrl}/filter?typeId=${typeId}`);
  }
  
}
