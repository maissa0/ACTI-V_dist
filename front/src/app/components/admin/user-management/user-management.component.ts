import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { BasicUserInfo } from '../../../models/basic-user-info.model';
import { Competence } from '../../../models/competence.model';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  
  // Table data
  displayedColumns: string[] = ['username', 'email', 'firstName', 'lastName', 'actions'];
  dataSource = new MatTableDataSource<BasicUserInfo>([]);
  
  // Loading states
  loading: boolean = false;
  loadingCompetences: boolean = false;
  
  // Selected user and competences
  selectedUser: BasicUserInfo | null = null;
  userCompetences: Competence[] = [];
  
  // Current user (admin)
  currentUser: any;
  
  // New properties for loadUserCompetences
  selectedUserCompetences: Competence[] = [];
  errorMessage: string = '';
  
  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Get the current user
    this.currentUser = this.authService.currentUserValue;
    
    // Load users
    this.loadUsers();
  }
  
  loadUsers(): void {
    this.loading = true;
    
    this.userService.getAllUsersWithRoleUser()
      .pipe(finalize(() => {
        this.loading = false;
        
        // Initialize table sorting and pagination after data is loaded
        if (this.dataSource.data.length > 0) {
          setTimeout(() => {
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
          });
        }
      }))
      .subscribe({
        next: (users) => {
          this.dataSource.data = users;
          console.log('Users loaded:', users);
        },
        error: (error) => {
          console.error('Error loading users:', error);
          if (error.status === 403) {
            alert('You do not have permission to access this resource.');
            this.router.navigate(['/home']);
          }
        }
      });
  }
  
  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  
  viewUserDetails(user: BasicUserInfo): void {
    this.selectedUser = user;
    this.loadUserCompetences(user);
  }
  
  loadUserCompetences(user: BasicUserInfo): void {
    if (!user || !user.username) {
      console.error('Invalid user or username');
      return;
    }

    this.loadingCompetences = true;
    this.errorMessage = '';
    
    // Use the simple approach to get user ID by username
    this.userService.getSimpleUserIdByUsername(user.username).subscribe({
      next: (userId) => {
        if (userId) {
          // Now use this ID to get the competences
          this.userService.getUserCompetences(userId).subscribe({
            next: (competences) => {
              this.selectedUserCompetences = competences;
              this.loadingCompetences = false;
            },
            error: (error) => {
              console.error('Error loading competences:', error);
              this.errorMessage = 'Failed to load user competences';
              this.loadingCompetences = false;
            }
          });
        } else {
          console.error('User ID not found for username:', user.username);
          this.errorMessage = 'User ID not found';
          this.loadingCompetences = false;
        }
      },
      error: (error) => {
        console.error('Error getting user ID:', error);
        this.errorMessage = 'Failed to retrieve user ID';
        this.loadingCompetences = false;
      }
    });
  }
  
  closeUserDetails(): void {
    this.selectedUser = null;
    this.userCompetences = [];
  }
  
  generatePdf(user: BasicUserInfo): void {
    if (!user || !user.username) {
      alert('Cannot generate PDF: Invalid user');
      return;
    }
    
    this.loading = true;
    
    // Get the user ID using the simple approach
    this.userService.getSimpleUserIdByUsername(user.username).subscribe({
      next: (userId) => {
        if (!userId) {
          alert('Cannot generate PDF: User ID not available');
          this.loading = false;
          return;
        }
        
        // Generate the PDF with AI summary
        this.userService.generateUserReportWithAiSummary(userId)
          .pipe(finalize(() => this.loading = false))
          .subscribe({
            next: (blob) => {
              // Create a URL for the blob
              const url = window.URL.createObjectURL(blob);
              
              // Create a link and click it to start the download
              const a = document.createElement('a');
              a.href = url;
              a.download = `user-report-${user.username}.pdf`;
              document.body.appendChild(a);
              a.click();
              
              // Clean up
              window.URL.revokeObjectURL(url);
              document.body.removeChild(a);
            },
            error: (error) => {
              console.error('Error generating PDF:', error);
              alert('Error generating PDF report. Please try again later.');
            }
          });
      },
      error: (error) => {
        console.error('Error getting user ID:', error);
        alert('Error retrieving user ID for PDF generation');
        this.loading = false;
      }
    });
  }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
