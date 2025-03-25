import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Absence } from '../../models/absence.model';
import { AbsenceService } from '../../services/absence.service';

@Component({
  selector: 'app-absence-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './absence-list.component.html',
  styleUrls: ['./absence-list.component.scss']
})
export class AbsenceListComponent implements OnInit {
  absences: Absence[] = [];
  loading: boolean = true;
  error: string | null = null;

  constructor(private absenceService: AbsenceService) {}

  ngOnInit(): void {
    this.loadAbsences();
  }

  loadAbsences(): void {
    this.loading = true;
    this.error = null;
    this.absenceService.getAbsences().subscribe({
      next: (data) => {
        this.absences = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching absences:', error);
        this.error = 'Erreur lors du chargement des absences. Veuillez réessayer plus tard.';
        this.loading = false;
      }
    });
  }

  deleteAbsence(id: number | undefined): void {
    if (id === undefined) {
      console.error('Cannot delete absence with undefined id');
      return;
    }

    if (confirm('Êtes-vous sûr de vouloir supprimer cette absence ?')) {
      this.absenceService.deleteAbsence(id).subscribe({
        next: () => {
          this.loadAbsences();
        },
        error: (error) => {
          console.error('Error deleting absence:', error);
          this.error = 'Erreur lors de la suppression de l\'absence. Veuillez réessayer plus tard.';
        }
      });
    }
  }
}
