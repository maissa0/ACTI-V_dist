import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Student } from '../../models/student.model';
import { StudentService } from '../../services/student.service';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss']
})
export class StudentListComponent implements OnInit {
  students: Student[] = [];
  loading: boolean = true;
  error: string | null = null;

  constructor(private studentService: StudentService) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.loading = true;
    this.error = null;
    this.studentService.getStudents().subscribe({
      next: (data) => {
        this.students = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching students:', error);
        this.error = 'Failed to load students. Please try again later.';
        this.loading = false;
      }
    });
  }

  deleteStudent(id: number | undefined): void {
    if (id === undefined) {
      console.error('Cannot delete student with undefined id');
      return;
    }

    if (confirm('Are you sure you want to delete this student?')) {
      this.studentService.deleteStudent(id).subscribe({
        next: () => {
          this.loadStudents();
        },
        error: (error) => {
          console.error('Error deleting student:', error);
          this.error = 'Failed to delete student. Please try again later.';
        }
      });
    }
  }
}

