import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs/operators';
import { Competence } from 'src/app/models/competence.model';
import { UserService } from 'src/app/services/user.service';

export interface UserDetailsDialogData {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

@Component({
  selector: 'app-user-details-dialog',
  templateUrl: './user-details-dialog.component.html',
  styleUrls: ['./user-details-dialog.component.scss']
})
export class UserDetailsDialogComponent implements OnInit {
  competences: Competence[] = [];
  loadingCompetences = false;
  generatingPdf = false;
  userId: number | null = null;

  constructor(
    public dialogRef: MatDialogRef<UserDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserDetailsDialogData,
    private userService: UserService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadUserCompetences();
  }

  loadUserCompetences(): void {
    if (!this.data.username) {
      return;
    }

    this.loadingCompetences = true;
    // Get the user ID first
    this.userService.getSimpleUserIdByUsername(this.data.username).subscribe({
      next: (userId) => {
        if (userId) {
          this.userId = userId;
          // Use the user ID to get competences
          this.userService.getUserCompetences(userId).subscribe({
            next: (competences) => {
              this.competences = competences;
              this.loadingCompetences = false;
            },
            error: (error) => {
              console.error('Error loading competences:', error);
              this.snackBar.open('Failed to load user competences', 'Close', {
                duration: 3000,
                panelClass: ['error-snackbar']
              });
              this.loadingCompetences = false;
            }
          });
        } else {
          console.error('User ID not found for username:', this.data.username);
          this.snackBar.open('User ID not found', 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
          this.loadingCompetences = false;
        }
      },
      error: (error) => {
        console.error('Error getting user ID:', error);
        this.snackBar.open('Failed to retrieve user ID', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        this.loadingCompetences = false;
      }
    });
  }

  generatePdf(): void {
    if (!this.userId) {
      this.snackBar.open('Cannot generate PDF: User ID not available', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.generatingPdf = true;
    this.snackBar.open('Generating PDF report with AI summary...', 'Close', {
      duration: 3000
    });

    // Generate the PDF with AI summary
    this.userService.generateUserReportWithAiSummary(this.userId)
      .pipe(finalize(() => this.generatingPdf = false))
      .subscribe({
        next: (blob) => {
          // Create a URL for the blob
          const url = window.URL.createObjectURL(blob);
          
          // Create a link and click it to start the download
          const a = document.createElement('a');
          a.href = url;
          a.download = `${this.data.username}-report-with-ai-summary.pdf`;
          document.body.appendChild(a);
          a.click();
          
          // Clean up
          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);
          
          this.snackBar.open('PDF report generated successfully!', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
        },
        error: (error) => {
          console.error('Error generating PDF:', error);
          this.snackBar.open('Error generating PDF report. Please try again later.', 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
        }
      });
  }

  getLevelLabel(level: number | undefined): string {
    if (level === undefined) return 'Not specified';
    
    switch(level) {
      case 1: return 'Beginner';
      case 2: return 'Elementary';
      case 3: return 'Intermediate';
      case 4: return 'Advanced';
      case 5: return 'Expert';
      default: return 'Unknown';
    }
  }
} 