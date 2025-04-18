import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'http://localhost:5000/api/userAuth/api'; // Update with your backend URL

  constructor(private http: HttpClient) { }

  /**
   * Upload a profile picture
   * @param file The image file to upload
   * @returns Observable of the HTTP response
   */
  uploadProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    
    // Important: Don't set Content-Type header manually, let the browser set it with the boundary
    return this.http.post(`${this.apiUrl}/profile/picture/upload`, formData, {
      // Skip setting the JSON content type so multipart/form-data with boundary is sent
      headers: new HttpHeaders({ 'Accept': 'application/json' }),
      reportProgress: true,
      observe: 'events',
      withCredentials: true
    });
  }

  /**
   * Returns the URL for the current user's profile picture
   */
  getProfilePictureUrl(): string {
    // Include timestamp to prevent caching
    return `${this.apiUrl}/profile/picture?t=${new Date().getTime()}`;
  }
  
  /**
   * Get the profile picture with proper authentication
   */
  getProfilePicture(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/profile/picture?t=${new Date().getTime()}`, {
      responseType: 'blob',
      withCredentials: true
    });
  }

  /**
   * Delete the profile picture
   * @returns Observable of the HTTP response
   */
  deleteProfilePicture(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/profile/picture`, {
      withCredentials: true
    });
  }
} 