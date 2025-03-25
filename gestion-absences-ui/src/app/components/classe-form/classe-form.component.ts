import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Classe } from '../../models/classe.model';
import { ClasseService } from '../../services/classe.service';

@Component({
  selector: 'app-classe-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterLink
  ],
  templateUrl: './classe-form.component.html',
  styleUrls: ['./classe-form.component.scss']
})
export class ClasseFormComponent implements OnInit {
  classeForm: FormGroup;
  isEditMode = false;
  errorMessage: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private classeService: ClasseService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.classeForm = this.formBuilder.group({
      nom: ['', Validators.required],
      niveau: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.loadClasse(+id);
    }
  }

  loadClasse(id: number): void {
    this.classeService.getClasse(id).subscribe({
      next: (data) => {
        this.classeForm.patchValue(data);
      },
      error: (error) => {
        console.error('Error fetching class:', error);
        this.errorMessage = 'Failed to load class data. Please try again.';
      }
    });
  }

  onSubmit(): void {
    if (this.classeForm.valid) {
      const classe: Classe = this.classeForm.value;

      if (this.isEditMode) {
        const id = this.route.snapshot.paramMap.get('id');
        if (!id) return;

        this.classeService.updateClasse(+id, classe).subscribe({
          next: () => {
            this.router.navigate(['/classes']);
          },
          error: (error) => {
            console.error('Error updating class:', error);
            this.errorMessage = 'Failed to update class. Please try again.';
          }
        });
      } else {
        this.classeService.createClasse(classe).subscribe({
          next: () => {
            this.router.navigate(['/classes']);
          },
          error: (error) => {
            console.error('Error creating class:', error);
            this.errorMessage = 'Failed to create class. Please try again.';
          }
        });
      }
    }
  }
}
