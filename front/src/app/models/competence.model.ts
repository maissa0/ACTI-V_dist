export interface Competence {
  id?: number;
  name: string;
  description?: string;
  level?: number; // 1=Beginner, 2=Elementary, 3=Intermediate, 4=Advanced, 5=Expert
  userId?: number;
}

export interface CompetenceRequest {
  name: string;
  description?: string;
  level?: number;
  userId?: number;
} 