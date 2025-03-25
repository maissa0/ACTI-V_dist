import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Classe } from '../../models/classe.model';
import { ClasseService } from '../../services/classe.service';

@Component({
  selector: 'app-classe-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './classe-list.component.html',
  styleUrls: ['./classe-list.component.scss']
})
export class ClasseListComponent implements OnInit {
  classes: Classe[] = [];
  loading = true;
  error: string | null = null;

  constructor(private classeService: ClasseService) {}

  ngOnInit(): void {
    this.loadClasses();
  }

  loadClasses(): void {
    this.loading = true;
    this.classeService.getClasses().subscribe({
      next: (data) => {
        this.classes = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
        this.error = 'Erreur lors du chargement des classes.';
        this.loading = false;
      }
    });
  }

  deleteClasse(id: number | undefined): void {
    if (id === undefined) {
      console.error('Cannot delete class with undefined id');
      this.error = 'Erreur: ID de classe non défini.';
      return;
    }

    if (confirm('Êtes-vous sûr de vouloir supprimer cette classe ?')) {
      this.classeService.deleteClasse(id).subscribe({
        next: () => this.loadClasses(),
        error: (error) => {
          console.error('Error deleting class:', error);
          this.error = 'Erreur lors de la suppression de la classe.';
        }
      });
    }
  }
}
