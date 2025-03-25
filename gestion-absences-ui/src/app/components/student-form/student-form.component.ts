import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Student } from '../../models/student.model';
import { Classe } from '../../models/classe.model';
import { StudentService } from '../../services/student.service';
import { ClasseService } from '../../services/classe.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-student-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './student-form.component.html',
  styleUrls: ['./student-form.component.scss']
})
export class StudentFormComponent implements OnInit {
  studentForm: FormGroup;
  isEditMode = false;
  classes: Classe[] = [];
  errorMessage: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private studentService: StudentService,
    private classeService: ClasseService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.studentForm = this.formBuilder.group({
      prenom: ['', Validators.required],
      nom: ['', Validators.required],
      dateNaissance: ['', Validators.required],
      classeId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadClasses();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.loadStudent(+id);
    }
  }

  loadClasses(): void {
    this.classeService.getClasses().subscribe({
      next: (data) => {
        this.classes = data;
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
        this.errorMessage = 'Failed to load classes. Please try again.';
      }
    });
  }

  loadStudent(id: number): void {
    this.studentService.getStudent(id).subscribe({
      next: (data) => {
        this.studentForm.patchValue(data);
      },
      error: (error) => {
        console.error('Error fetching student:', error);
        this.errorMessage = 'Failed to load student data. Please try again.';
      }
    });
  }

  onSubmit(): void {
    if (this.studentForm.valid) {
      const student: Student = this.studentForm.value;
      console.log('Submitting student:', student); // Add this line for debugging
      if (this.isEditMode) {
        const id = this.route.snapshot.paramMap.get('id');
        this.studentService.updateStudent(+id!, student).subscribe({
          next: () => {
            this.router.navigate(['/students']);
          },
          error: (error) => {
            console.error('Error updating student:', error);
            this.errorMessage = 'Failed to update student. Please try again.';
          }
        });
      } else {
        this.studentService.createStudent(student).subscribe({
          next: () => {
            this.router.navigate(['/students']);
          },
          error: (error) => {
            console.error('Error creating student:', error);
            this.errorMessage = 'Failed to create student. Please try again.';
          }
        });
      }
    }
  }
}

