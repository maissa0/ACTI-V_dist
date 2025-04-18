import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ProfileService } from '../../services/profile.service';
import { BasicUserInfo } from '../../models/basic-user-info.model';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { Router } from '@angular/router';
import { CompetenceService } from '../../services/competence.service';
import { Competence, CompetenceRequest } from '../../models/competence.model';
import { finalize } from 'rxjs/operators';

// Password validator function
function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const newPassword = control.get('newPassword');
  const confirmPassword = control.get('confirmPassword');
  
  if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
    return { passwordMismatch: true };
  }
  
  return null;
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  passwordForm: FormGroup;
  profileImage: string | null = null;
  email: string = '';
  loading: boolean = false;
  username: string = '';
  userInfo: BasicUserInfo | null = null;
  
  // Competences
  competences: Competence[] = [];
  newCompetence: string = '';
  selectedLevel: number = 3; // Default to Intermediate (level 3)
  competenceDescription: string = '';
  levels = [
    { value: 1, label: 'Beginner' },
    { value: 2, label: 'Elementary' },
    { value: 3, label: 'Intermediate' },
    { value: 4, label: 'Advanced' },
    { value: 5, label: 'Expert' }
  ];
  loadingCompetences: boolean = false;
  
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private profileService: ProfileService,
    private competenceService: CompetenceService,
    private router: Router
  ) {
    this.profileForm = this.fb.group({
      username: [{ value: '', disabled: true }],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required]
    });
    
    // Initialize password form
    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, { validators: passwordMatchValidator });
  }

  ngOnInit() {
    // Get current user info from auth service
    const currentUser = this.authService.currentUserValue;
    console.log('Current user from localStorage:', currentUser);
    
    if (currentUser) {
      // Set initial values from localStorage
      this.username = currentUser.username || '';
      this.email = currentUser.email || '';
      console.log('Initial email set from localStorage:', this.email);
      
      // Load basic user info from the backend
      this.authService.getBasicUserInfo().subscribe({
        next: (userInfo: BasicUserInfo) => {
          console.log('User info from API:', userInfo);
          this.userInfo = userInfo;
          
          // Update the email from backend data
          if (userInfo.email) {
            this.email = userInfo.email;
            console.log('Email updated from API:', this.email);
          }
          
          // Update the form with real user data
          this.profileForm.patchValue({
            username: userInfo.username,
            email: userInfo.email,
            firstName: userInfo.firstName || '',
            lastName: userInfo.lastName || ''
          });
          
          // Try to load profile picture
          this.loadProfilePicture();
        },
        error: (error) => {
          console.error('Error fetching user info:', error);
          
          // Fallback to default values if API call fails
    this.profileForm.patchValue({
            username: this.username,
            email: this.email,
            firstName: 'John',
            lastName: 'Doe'
          });
        }
      });

      // Load user competences
      this.loadCompetences();
    } else {
      console.warn('No current user found in localStorage');
    }
  }
  
  loadProfilePicture() {
    this.loading = true;
    
    // Use the service to get the profile picture with proper authentication
    this.profileService.getProfilePicture().subscribe({
      next: (blob: Blob) => {
        // Create a URL for the blob
        const objectUrl = URL.createObjectURL(blob);
        this.profileImage = objectUrl;
        this.loading = false;
        console.log('Profile picture loaded successfully');
      },
      error: (error) => {
        console.error('Error loading profile picture:', error);
        this.profileImage = null;
        this.loading = false;
      }
    });
  }

  loadCompetences() {
    this.loadingCompetences = true;
    this.competenceService.getUserCompetences()
      .pipe(finalize(() => this.loadingCompetences = false))
      .subscribe({
        next: (competences) => {
          this.competences = competences;
          console.log('Competences loaded:', this.competences);
        },
        error: (error) => {
          console.error('Error loading competences:', error);
          // Fallback to empty array if API call fails
          this.competences = [];
        }
    });
  }

  onSubmit() {
    if (this.profileForm.valid) {
      this.loading = true;
      
      const formData = {
        userName: this.profileForm.get('username')?.value,
        email: this.profileForm.get('email')?.value,
        firstName: this.profileForm.get('firstName')?.value,
        lastName: this.profileForm.get('lastName')?.value
      };
      
      console.log('Updating basic user info:', formData);
      
      this.authService.updateBasicUserInfo(formData).subscribe({
        next: (response) => {
          console.log('Update successful:', response);
          this.loading = false;
          
          // Update the local user information with the response
          if (response) {
            this.userInfo = response;
            this.email = response.email; // Update the email in the header
          }
          
          alert('Profile updated successfully!');
        },
        error: (error) => {
          console.error('Error updating profile:', error);
          this.loading = false;
          alert('Error updating profile: ' + (error.error?.message || 'Unknown error'));
        }
      });
    }
  }
  
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Check if file is an image
      if (!file.type.startsWith('image/')) {
        alert('Please select an image file');
        return;
      }
      
      // Check file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('File size should not exceed 5MB');
        return;
      }
      
      // Upload to server
      this.loading = true;
      this.profileService.uploadProfilePicture(file).subscribe({
        next: (event: HttpEvent<any>) => {
          // Check the event type
          if (event.type === HttpEventType.UploadProgress) {
            // Calculate and display upload progress if needed
            const percentDone = event.total ? Math.round(100 * event.loaded / event.total) : 0;
            console.log(`Upload progress: ${percentDone}%`);
          } else if (event.type === HttpEventType.Response) {
            console.log('Upload complete:', event.body);
            this.loading = false;
            
            // Load the new profile picture with cache busting
            this.loadProfilePicture();
            
            alert('Profile picture uploaded successfully!');
          }
        },
        error: (error) => {
          console.error('Error uploading profile picture:', error);
          this.loading = false;
          alert('Error uploading profile picture: ' + (error.error?.message || 'Unknown error'));
        }
      });
    }
  }
  
  removeProfilePicture() {
    if (this.profileImage) {
      this.loading = true;
      this.profileService.deleteProfilePicture().subscribe({
        next: (response) => {
          console.log('Delete successful:', response);
          this.loading = false;
          this.profileImage = null;
          alert('Profile picture removed successfully!');
        },
        error: (error) => {
          console.error('Error removing profile picture:', error);
          this.loading = false;
          alert('Error removing profile picture: ' + (error.error?.message || 'Unknown error'));
        }
      });
    }
  }

  // Competence methods
  addCompetence() {
    if (this.newCompetence && this.newCompetence.trim() !== '') {
      const competenceName = this.newCompetence.trim();
      
      // Check for duplicates
      const exists = this.competences.some(c => c.name.toLowerCase() === competenceName.toLowerCase());
      
      if (!exists) {
        this.loadingCompetences = true;
        
        const competenceRequest: CompetenceRequest = {
          name: competenceName,
          level: this.selectedLevel,
          description: this.competenceDescription || undefined
        };
        
        this.competenceService.addCompetence(competenceRequest)
          .pipe(finalize(() => this.loadingCompetences = false))
          .subscribe({
            next: (newCompetence) => {
              this.competences.push(newCompetence);
              console.log('Added competence:', newCompetence);
              
              // Reset form fields
              this.newCompetence = '';
              this.competenceDescription = '';
              this.selectedLevel = 3;
            },
            error: (error) => {
              console.error('Error adding competence:', error);
              alert('Error adding competence: ' + (error.error?.message || 'Unknown error'));
            }
          });
      } else {
        alert('This competence already exists in your list.');
      }
    }
  }

  removeCompetence(competence: Competence) {
    if (competence && competence.id) {
      this.loadingCompetences = true;
      
      this.competenceService.deleteCompetence(competence.id)
        .pipe(finalize(() => this.loadingCompetences = false))
        .subscribe({
          next: () => {
            // Remove from local array
            this.competences = this.competences.filter(c => c.id !== competence.id);
            console.log('Removed competence with ID:', competence.id);
          },
          error: (error) => {
            console.error('Error removing competence:', error);
            alert('Error removing competence: ' + (error.error?.message || 'Unknown error'));
          }
        });
    }
  }

  // Change password functionality
  onChangePassword() {
    if (this.passwordForm.valid) {
      this.loading = true;
      
      const currentPassword = this.passwordForm.get('currentPassword')?.value;
      const newPassword = this.passwordForm.get('newPassword')?.value;
      
      this.authService.changePassword(currentPassword, newPassword).subscribe({
        next: (response) => {
          console.log('Password change successful:', response);
          this.loading = false;
          
          // Reset form after successful password change
          this.passwordForm.reset();
          
          alert('Password changed successfully!');
        },
        error: (error) => {
          console.error('Error changing password:', error);
          this.loading = false;
          
          let errorMessage = 'Unknown error occurred';
          if (error.error?.message) {
            errorMessage = error.error.message;
          } else if (error.status === 401) {
            errorMessage = 'Current password is incorrect';
          }
          
          alert('Error changing password: ' + errorMessage);
        }
      });
    }
  }
  
  // Two-factor authentication toggle
  toggleTwoFactor() {
    // This would be implemented if two-factor auth is supported
    alert('Two-factor authentication toggle not implemented yet.');
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
