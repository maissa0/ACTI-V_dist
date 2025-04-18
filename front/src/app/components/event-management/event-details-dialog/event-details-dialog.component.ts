import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Event, EventService, Resource, ResourceType } from 'src/app/services/event.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MissionService, Mission } from '../../../services/mission.service';
import { FeedbackService, Feedback, FeedbackStatistics } from '../../../services/feedback.service';

@Component({
  selector: 'app-event-details-dialog',
  templateUrl: './event-details-dialog.component.html',
  styleUrls: ['./event-details-dialog.component.scss']
})
export class EventDetailsDialogComponent implements OnInit {
  // Define table columns
  resourceColumns: string[] = ['nom', 'type', 'quantite', 'actions'];
  missionColumns: string[] = ['nomMission', 'description', 'dateDebut', 'statut', 'actions'];

  // Math reference for templates
  Math = Math;

  // Data properties
  resources: Resource[] = [];
  resourceTypes: ResourceType[] = [];
  missions: Mission[] = [];
  feedback: Feedback[] = [];
  
  // Rating statistics
  ratingStats: FeedbackStatistics = {
    average: 0,
    distribution: {1: 0, 2: 0, 3: 0, 4: 0, 5: 0},
    count: 0
  };

  // UI state properties
  isLoading = false;
  showAddResourceForm = false;
  resourceForm: FormGroup;

  // Form controls
  missionForm: FormGroup;
  showAddMissionForm = false;
  isMissionLoading = false;
  editingMissionId: number | null = null;
  
  // Feedback form and state
  feedbackForm: FormGroup;
  showAddFeedbackForm = false;
  isFeedbackLoading = false;
  userHasProvidedFeedback = false;
  userFeedbackId: string | null = null;

  constructor(
    public dialogRef: MatDialogRef<EventDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public event: Event,
    private eventService: EventService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private missionService: MissionService,
    private feedbackService: FeedbackService
  ) {
    // Initialize the resource form
    this.resourceForm = this.fb.group({
      nom: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      typeId: ['', Validators.required]
    });

    // Initialize mission form
    this.missionForm = this.fb.group({
      nomMission: ['', Validators.required],
      description: ['', Validators.required],
      dateDebut: ['', Validators.required],
      statut: ['EN_ATTENTE', Validators.required],
      competencesRequises: [[]],
      responsableId: [1, Validators.required]
    });
    
    // Initialize feedback form
    this.feedbackForm = this.fb.group({
      note: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      commentaire: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadResources();
    this.loadResourceTypes();
    this.loadMissions();
    this.loadFeedback();
    this.loadRatingStatistics();
    this.checkUserFeedback();
  }

  // Load feedback for the event
  loadFeedback(): void {
    if (!this.event?.id) return;
    
    this.isFeedbackLoading = true;
    this.feedbackService.getEventFeedbacks(this.event.id).subscribe({
      next: (data: Feedback[]) => {
        console.log('Feedback loaded successfully:', data);
        this.feedback = data;
        this.isFeedbackLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading feedback:', error);
        this.snackBar.open('Failed to load feedback: ' + error.message, 'Close', { duration: 3000 });
        this.isFeedbackLoading = false;
        
        // Clear feedback instead of using mock data
        this.feedback = [];
      }
    });
  }
  
  // Load rating statistics for the event
  loadRatingStatistics(): void {
    if (!this.event?.id) return;
    
    this.feedbackService.getRatingStatistics(this.event.id).subscribe({
      next: (stats: FeedbackStatistics) => {
        console.log('Rating statistics loaded:', stats);
        this.ratingStats = stats;
      },
      error: (error: any) => {
        console.error('Error loading rating statistics:', error);
      }
    });
  }
  
  // Check if the current user has already provided feedback
  checkUserFeedback(): void {
    if (!this.event?.id) return;
    
    this.feedbackService.getUserFeedbackForEvent(this.event.id).subscribe({
      next: (userFeedback: Feedback[]) => {
        if (userFeedback && userFeedback.length > 0) {
          this.userHasProvidedFeedback = true;
          this.userFeedbackId = userFeedback[0].id || null;
          
          // Pre-populate the form with existing feedback
          this.feedbackForm.patchValue({
            note: userFeedback[0].note,
            commentaire: userFeedback[0].commentaire
          });
        } else {
          this.userHasProvidedFeedback = false;
          this.userFeedbackId = null;
        }
      },
      error: (error: any) => {
        console.error('Error checking user feedback:', error);
        this.userHasProvidedFeedback = false;
      }
    });
  }
  
  // Toggle feedback form visibility
  toggleAddFeedbackForm(): void {
    this.showAddFeedbackForm = !this.showAddFeedbackForm;
    if (!this.showAddFeedbackForm) {
      this.feedbackForm.reset({ note: 5 });
    }
  }
  
  // Submit feedback
  submitFeedback(): void {
    if (this.feedbackForm.invalid || !this.event?.id) return;
    
    this.isFeedbackLoading = true;
    
    // Create basic feedback data (userId can be omitted as backend will extract it from token)
    const feedbackData: Feedback = {
      evenementId: this.event.id,
      note: this.feedbackForm.value.note,
      commentaire: this.feedbackForm.value.commentaire
      // userId is omitted - backend will extract from token
    };
    
    console.log('Preparing to submit feedback:', feedbackData);
    
    // Determine if we're updating or creating
    if (this.userHasProvidedFeedback && this.userFeedbackId) {
      // Update existing feedback
      this.feedbackService.updateFeedback(this.userFeedbackId, feedbackData).subscribe({
        next: (updatedFeedback: Feedback) => {
          this.snackBar.open('Feedback updated successfully', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.loadFeedback();
          this.loadRatingStatistics();
          this.toggleAddFeedbackForm();
          this.isFeedbackLoading = false;
        },
        error: (error: any) => {
          console.error('Error updating feedback:', error);
          this.snackBar.open('Error updating feedback: ' + error.message, 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
          this.isFeedbackLoading = false;
        }
      });
    } else {
      // Create new feedback
      this.feedbackService.createFeedback(this.event.id, feedbackData).subscribe({
        next: (newFeedback: Feedback) => {
          this.snackBar.open('Feedback submitted successfully', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.userHasProvidedFeedback = true;
          this.userFeedbackId = newFeedback.id || null;
          this.loadFeedback();
          this.loadRatingStatistics();
          this.toggleAddFeedbackForm();
          this.isFeedbackLoading = false;
        },
        error: (error: any) => {
          console.error('Error submitting feedback:', error);
          this.snackBar.open('Error submitting feedback: ' + error.message, 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
          this.isFeedbackLoading = false;
        }
      });
    }
  }
  
  // Delete feedback
  deleteFeedback(): void {
    if (!this.userFeedbackId || !this.userHasProvidedFeedback) return;
    
    if (confirm('Are you sure you want to delete your feedback?')) {
      this.isFeedbackLoading = true;
      
      this.feedbackService.deleteFeedback(this.userFeedbackId).subscribe({
        next: () => {
          this.snackBar.open('Feedback deleted successfully', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.userHasProvidedFeedback = false;
          this.userFeedbackId = null;
          this.loadFeedback();
          this.loadRatingStatistics();
          this.isFeedbackLoading = false;
          
          // Reset the form
          this.feedbackForm.reset({ note: 5 });
        },
        error: (error: any) => {
          console.error('Error deleting feedback:', error);
          this.snackBar.open('Error deleting feedback: ' + error.message, 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
          this.isFeedbackLoading = false;
        }
      });
    }
  }

  // Get the percentage for a specific star rating
  getRatingPercentage(rating: number): number {
    if (this.ratingStats.count === 0) return 0;
    const count = this.ratingStats.distribution[rating] || 0;
    return (count / this.ratingStats.count) * 100;
  }

  loadResources(): void {
    if (!this.event.id) return;

    this.isLoading = true;
    this.eventService.getResourcesByEventId(this.event.id).subscribe({
      next: (data) => {
        this.resources = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading resources:', error);
        this.isLoading = false;
        this.snackBar.open('Failed to load resources: ' + error.message, 'Close', {
          duration: 3000
        });
        
        // Load fallback data for development
        this.resources = [
          { 
            id: 1, 
            nom: 'Conference Room A', 
            quantite: 1, 
            type: { id: 1, nom: 'Venue' },
            eventId: this.event.id
          },
          { 
            id: 2, 
            nom: 'Projector', 
            quantite: 2, 
            type: { id: 2, nom: 'Equipment' },
            eventId: this.event.id
          },
          { 
            id: 3, 
            nom: 'Laptops', 
            quantite: 5, 
            type: { id: 2, nom: 'Equipment' },
            eventId: this.event.id
          }
        ];
      }
    });
  }

  loadResourceTypes(): void {
    this.eventService.getAllResourceTypes().subscribe({
      next: (data) => {
        this.resourceTypes = data;
      },
      error: (error) => {
        console.error('Error loading resource types:', error);
        this.snackBar.open('Failed to load resource types: ' + error.message, 'Close', {
          duration: 3000
        });
        
        // Load fallback data for development
        this.resourceTypes = [
          { id: 1, nom: 'Venue' },
          { id: 2, nom: 'Equipment' },
          { id: 3, nom: 'Materials' },
          { id: 4, nom: 'Personnel' }
        ];
      }
    });
  }

  toggleAddResourceForm(): void {
    this.showAddResourceForm = !this.showAddResourceForm;
    if (!this.showAddResourceForm) {
      this.resourceForm.reset();
      this.resourceForm.patchValue({ quantite: 1 });
    }
  }

  addResource(): void {
    if (this.resourceForm.invalid) {
      return;
    }

    const formValue = this.resourceForm.value;
    const selectedType = this.resourceTypes.find(type => type.id === +formValue.typeId);
    
    if (!selectedType) {
      this.snackBar.open('Please select a valid resource type', 'Close', {
        duration: 3000
      });
      return;
    }

    const resource: Resource = {
      nom: formValue.nom,
      quantite: formValue.quantite,
      type: selectedType,
      eventId: this.event.id
    };

    this.eventService.createResource(resource).subscribe({
      next: (newResource) => {
        this.resources.push(newResource);
        this.snackBar.open('Resource added successfully', 'Close', {
          duration: 3000
        });
        this.toggleAddResourceForm();
      },
      error: (error) => {
        console.error('Error adding resource:', error);
        this.snackBar.open('Failed to add resource: ' + error.message, 'Close', {
          duration: 3000
        });
      }
    });
  }

  deleteResource(resource: Resource): void {
    if (!resource.id) return;
    
    if (confirm(`Are you sure you want to delete the resource "${resource.nom}"?`)) {
      this.eventService.deleteResource(resource.id).subscribe({
        next: () => {
          this.resources = this.resources.filter(r => r.id !== resource.id);
          this.snackBar.open('Resource deleted successfully', 'Close', {
            duration: 3000
          });
        },
        error: (error) => {
          console.error('Error deleting resource:', error);
          this.snackBar.open('Failed to delete resource: ' + error.message, 'Close', {
            duration: 3000
          });
        }
      });
    }
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  generateReport(): void {
    // This would typically call a service to generate a report
    console.log('Generating report for event:', this.event.name);
    // For now, just show an alert
    alert(`Report for ${this.event.name} has been generated and sent to your email.`);
  }

  getEventTypeLabel(type: string): string {
    // Convert type value to a more readable label
    if (!type) return 'Not specified';
    
    // Convert from camelCase or snake_case to Title Case with spaces
    return type
      .replace(/([A-Z])/g, ' $1') // Insert a space before all capital letters
      .replace(/_/g, ' ') // Replace underscores with spaces
      .replace(/^\w/, (c) => c.toUpperCase()) // Capitalize first letter
      .trim();
  }

  getEventStatus(event: Event): string {
    const now = new Date();
    const startDate = new Date(event.dateDeb);
    const endDate = new Date(event.dateFin);

    if (now < startDate) {
      return 'Upcoming';
    } else if (now >= startDate && now <= endDate) {
      return 'Ongoing';
    } else {
      return 'Past';
    }
  }

  // Mission Management Methods
  loadMissions(): void {
    this.isMissionLoading = true;
    
    if (this.event && this.event.id) {
      console.log('Loading missions for event ID:', this.event.id);
      this.missionService.getMissionsByEventId(this.event.id).subscribe({
        next: (data) => {
          console.log('Missions loaded successfully:', data);
          if (data && data.length > 0) {
            this.missions = data;
            this.isMissionLoading = false;
          } else {
            console.warn('No missions returned from API');
            // If you want to use mock data when API returns empty array, uncomment this section
            /*
            this.missions = [
              { 
                id: 1, 
                nomMission: 'Venue Setup', 
                description: 'Prepare the venue for the event, including seating arrangements and equipment setup', 
                dateDebut: new Date('2023-11-01'), 
                statut: 'EN_COURS',
                competencesRequises: 'Organization, Technical setup',
                responsableId: 101,
                evenementId: this.event.id || 0
              },
              { 
                id: 2, 
                nomMission: 'Speaker Coordination', 
                description: 'Contact and coordinate with all speakers, ensure they have all necessary information', 
                dateDebut: new Date('2023-10-25'), 
                statut: 'TERMINEE',
                competencesRequises: 'Communication, Planning',
                responsableId: 102,
                evenementId: this.event.id || 0
              }
            ];
            console.log('Using mock mission data instead of empty array:', this.missions);
            */
            
            // Otherwise, just show the empty data
            this.missions = data || [];
            this.isMissionLoading = false;
          }
        },
        error: (error) => {
          console.error('Error loading missions:', error);
          this.snackBar.open('Failed to load missions: ' + (error.message || 'Unknown error'), 'Close', { duration: 3000 });
          this.isMissionLoading = false;
          
          // For development/demo purposes, add mock data if API fails
          this.missions = [
            { 
              id: 1, 
              nomMission: 'Venue Setup', 
              description: 'Prepare the venue for the event, including seating arrangements and equipment setup', 
              dateDebut: new Date('2023-11-01'), 
              statut: 'EN_COURS',
              competencesRequises: 'Organization, Technical setup',
              responsableId: 101,
              evenementId: this.event.id || 0
            },
            { 
              id: 2, 
              nomMission: 'Speaker Coordination', 
              description: 'Contact and coordinate with all speakers, ensure they have all necessary information', 
              dateDebut: new Date('2023-10-25'), 
              statut: 'TERMINEE',
              competencesRequises: 'Communication, Planning',
              responsableId: 102,
              evenementId: this.event.id || 0
            }
          ];
          console.log('Using mock mission data due to error:', this.missions);
        }
      });
    } else {
      console.warn('No event ID available, cannot load missions');
      this.isMissionLoading = false;
    }
  }

  toggleAddMissionForm(): void {
    this.showAddMissionForm = !this.showAddMissionForm;
    if (!this.showAddMissionForm) {
      this.missionForm.reset({
        statut: 'EN_ATTENTE',
        responsableId: 1,
        competencesRequises: []
      });
      this.editingMissionId = null;
    }
  }

  addMission(): void {
    if (this.missionForm.valid && this.event && this.event.id) {
      this.isMissionLoading = true;
      
      const missionData: Mission = {
        ...this.missionForm.value,
        evenementId: this.event.id
      };
      
      if (this.editingMissionId !== null) {
        // Update existing mission
        this.missionService.updateMission(this.editingMissionId, missionData).subscribe({
          next: (updatedMission) => {
            this.snackBar.open('Mission updated successfully', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
            this.loadMissions();
            this.toggleAddMissionForm();
            this.isMissionLoading = false;
          },
          error: (error) => {
            console.error('Error updating mission:', error);
            this.snackBar.open('Error updating mission', 'Close', {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
            this.isMissionLoading = false;
          }
        });
      } else {
        // Create new mission
        this.missionService.createMission(missionData).subscribe({
          next: (newMission) => {
            this.snackBar.open('Mission added successfully', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
            this.loadMissions();
            this.toggleAddMissionForm();
            this.isMissionLoading = false;
          },
          error: (error) => {
            console.error('Error adding mission:', error);
            this.snackBar.open('Error adding mission', 'Close', {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
            this.isMissionLoading = false;
          }
        });
      }
    }
  }

  editMission(mission: Mission): void {
    if (mission.id !== undefined) {
      this.editingMissionId = mission.id;
      this.missionForm.patchValue({
        nomMission: mission.nomMission,
        description: mission.description,
        dateDebut: mission.dateDebut,
        statut: mission.statut,
        competencesRequises: mission.competencesRequises,
        responsableId: mission.responsableId
      });
      this.showAddMissionForm = true;
    } else {
      this.snackBar.open('Cannot edit mission without ID', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
    }
  }

  deleteMission(missionId: number | undefined): void {
    if (missionId === undefined) {
      this.snackBar.open('Cannot delete mission without ID', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }
    
    if (confirm('Are you sure you want to delete this mission?')) {
      this.isMissionLoading = true;
      
      // Now we can safely use missionId as a number
      const id: number = missionId;
      this.missionService.deleteMission(id).subscribe({
        next: () => {
          this.snackBar.open('Mission deleted successfully', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.loadMissions();
          this.isMissionLoading = false;
        },
        error: (error) => {
          console.error('Error deleting mission:', error);
          this.snackBar.open('Error deleting mission', 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
          this.isMissionLoading = false;
        }
      });
    }
  }

  formatMissionStatus(status: string): string {
    switch (status) {
      case 'EN_ATTENTE': return 'En Attente';
      case 'EN_COURS': return 'En Cours';
      case 'TERMINEE': return 'Terminée';
      default: return status;
    }
  }

  getMissionStatusClass(status: string): string {
    switch (status) {
      case 'À Faire':
      case 'EN_ATTENTE':
        return 'status-todo';
      case 'En Cours':
      case 'EN_COURS':
        return 'status-in-progress';
      case 'Terminé':
      case 'TERMINEE':
        return 'status-completed';
      default:
        return '';
    }
  }
} 