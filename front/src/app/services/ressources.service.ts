import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Resource {
  _id: string;
  nom: string;
  type: string;
  quantite: number;
  createdAt: Date;
  updatedAt: Date;
}

@Injectable({
  providedIn: 'root'
})
export class RessourcesService {
  private apiUrl = 'http://localhost:3000/api/ressources';

  constructor(private http: HttpClient) { }

  getAllRessources(): Observable<Resource[]> {
    return this.http.get<Resource[]>(this.apiUrl);
  }

  getRessource(id: string): Observable<Resource> {
    return this.http.get<Resource>(`${this.apiUrl}/${id}`);
  }

  createRessource(resource: Omit<Resource, '_id' | 'createdAt' | 'updatedAt'>): Observable<Resource> {
    return this.http.post<Resource>(this.apiUrl, resource);
  }

  updateRessource(id: string, resource: Partial<Resource>): Observable<Resource> {
    return this.http.put<Resource>(`${this.apiUrl}/${id}`, resource);
  }

  deleteRessource(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
} 