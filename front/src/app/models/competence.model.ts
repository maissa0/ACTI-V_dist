export interface Competence {
  id?: number;
  name: string;
  description?: string;
  level?: string; // Beginner, Intermediate, Advanced, Expert
  userId?: number;
}

export interface CompetenceRequest {
  name: string;
  description?: string;
  level?: string;
  userId?: number;
} 