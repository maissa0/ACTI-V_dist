import {Component, OnInit} from '@angular/core';
import {Student} from '../../models/student.model';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AbsenceService} from '../../services/absence.service';
import {StudentService} from '../../services/student.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Absence} from '../../models/absence.model';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-absence-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './absence-form.component.html',
  styleUrl: './absence-form.component.scss'
})
export class AbsenceFormComponent implements OnInit {
  absenceForm: FormGroup;
  isEditMode = false;
  students: Student[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private absenceService: AbsenceService,
    private studentService: StudentService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.absenceForm = this.formBuilder.group({
      date: ['', Validators.required],
      raison: ['', Validators.required],
      justifiee: [false],
      etudiantId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadStudents();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.loadAbsence(+id);
    }
  }

  loadStudents(): void {
    this.studentService.getStudents().subscribe(
      (data) => {
        this.students = data;
      },
      (error) => {
        console.error('Error fetching students:', error);
      }
    );
  }

  loadAbsence(id: number): void {
    this.absenceService.getAbsence(id).subscribe(
      (data) => {
        this.absenceForm.patchValue(data);
      },
      (error) => {
        console.error('Error fetching absence:', error);
      }
    );
  }

  onSubmit(): void {
    if (this.absenceForm.valid) {
      const absence: Absence = this.absenceForm.value;
      if (this.isEditMode) {
        const id = this.route.snapshot.paramMap.get('id');
        this.absenceService.updateAbsence(+id!, absence).subscribe(
          () => {
            this.router.navigate(['/absences']);
          },
          (error) => {
            console.error('Error updating absence:', error);
          }
        );
      } else {
        this.absenceService.createAbsence(absence).subscribe(
          () => {
            this.router.navigate(['/absences']);
          },
          (error) => {
            console.error('Error creating absence:', error);
          }
        );
      }
    }
  }
}
