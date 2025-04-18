import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EventService, Event } from '../../services/event.service';
import { AuthService } from '../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { EventDetailsDialogComponent } from './event-details-dialog/event-details-dialog.component';

@Component({
  selector: 'app-event-management',
  templateUrl: './event-management.component.html',
  styleUrls: ['./event-management.component.scss']
})
export class EventManagementComponent implements OnInit {
  events: Event[] = [];
  filteredEvents: Event[] = [];
  eventForm: FormGroup;
  isLoading = false;
  isEditMode = false;
  currentEventId: number | null = null;
  searchText = '';
  selectedStatus: string | null = null;
  displayedColumns: string[] = ['name', 'dates', 'type', 'status', 'actions'];

  constructor(
    private eventService: EventService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.eventForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      dateDeb: ['', Validators.required],
      dateFin: ['', Validators.required],
      type: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.isLoading = true;
    
    this.eventService.getMyEvents().subscribe({
      next: (data) => {
        this.events = data;
        this.filteredEvents = [...this.events];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading events:', error);
        this.isLoading = false;
        
        // Show error message
        this.snackBar.open(
          error.message || 'Failed to load events. Please check your network connection.', 
          'Close', 
          { duration: 5000 }
        );
        
        // Load fallback data if in development environment
        if (this.shouldLoadFallbackData()) {
          this.loadFallbackData();
        }
      }
    });
  }

  // Helper method to check if we should load fallback data
  // In a production environment, you might want to check if we're in development mode
  private shouldLoadFallbackData(): boolean {
    // For testing purposes, always return true
    // In production, you might check environment.production
    return true;
  }
  
  // Load fallback/demo data for testing when backend is unavailable
  private loadFallbackData(): void {
    // Mock data for demonstration
    this.events = [
      {
        id: 1,
        name: 'Tech Conference 2023',
        description: 'Annual technology conference with industry leaders',
        type: 'conference',
        dateDeb: new Date('2023-12-15T09:00:00'),
        dateFin: new Date('2023-12-15T18:00:00')
      },
      {
        id: 2,
        name: 'Web Development Workshop',
        description: 'Hands-on workshop for modern web technologies',
        type: 'workshop',
        dateDeb: new Date('2023-12-01T14:00:00'),
        dateFin: new Date('2023-12-01T17:00:00')
      },
      {
        id: 3,
        name: 'Product Management Seminar',
        description: 'Learn best practices in product management',
        type: 'seminar',
        dateDeb: new Date('2024-01-20T10:00:00'),
        dateFin: new Date('2024-01-20T16:00:00')
      }
    ];
    
    this.filteredEvents = [...this.events];
    this.snackBar.open('Loaded demo data for testing', 'Close', { duration: 3000 });
  }

  createEvent(): void {
    this.isEditMode = false;
    this.currentEventId = null;
    this.eventForm.reset();
    // Set default values if needed
    this.eventForm.patchValue({
      type: 'conference'
    });
  }

  editEvent(event: Event): void {
    this.isEditMode = true;
    this.currentEventId = event.id || null;
    
    // Format dates for datetime-local input
    const dateDeb = new Date(event.dateDeb);
    const dateFin = new Date(event.dateFin);
    
    const formatDate = (date: Date) => {
      return date.toISOString().slice(0, 16); // Format as "yyyy-MM-ddThh:mm"
    };
    
    this.eventForm.patchValue({
      name: event.name,
      description: event.description,
      dateDeb: formatDate(dateDeb),
      dateFin: formatDate(dateFin),
      type: event.type
    });
  }

  deleteEvent(event: Event): void {
    if (confirm(`Are you sure you want to delete "${event.name}"?`)) {
      this.eventService.deleteEvent(event.id!).subscribe({
        next: () => {
          this.loadEvents();
          this.snackBar.open('Event deleted successfully', 'Close', {
            duration: 3000
          });
        },
        error: (error) => {
          this.snackBar.open('Failed to delete event: ' + error.message, 'Close', {
            duration: 3000
          });
        }
      });
    }
  }

  onSubmit(): void {
    if (this.eventForm.invalid) {
      return;
    }

    const eventData: Event = {
      name: this.eventForm.value.name,
      description: this.eventForm.value.description,
      dateDeb: new Date(this.eventForm.value.dateDeb),
      dateFin: new Date(this.eventForm.value.dateFin),
      type: this.eventForm.value.type
    };

    if (this.isEditMode && this.currentEventId) {
      // Update existing event
      this.eventService.updateEvent(this.currentEventId, eventData).subscribe({
        next: (updatedEvent) => {
          this.loadEvents();
          this.cancelEdit();
          this.snackBar.open('Event updated successfully', 'Close', {
            duration: 3000
          });
        },
        error: (error) => {
          this.snackBar.open('Failed to update event: ' + error.message, 'Close', {
            duration: 3000
          });
        }
      });
    } else {
      // Create new event
      this.eventService.createEvent(eventData).subscribe({
        next: (newEvent) => {
          this.loadEvents();
          this.cancelEdit();
          this.snackBar.open('Event created successfully', 'Close', {
            duration: 3000
          });
        },
        error: (error) => {
          this.snackBar.open('Failed to create event: ' + error.message, 'Close', {
            duration: 3000
          });
        }
      });
    }
  }

  cancelEdit(): void {
    this.isEditMode = false;
    this.currentEventId = null;
    this.eventForm.reset();
  }

  searchEvents(): void {
    if (!this.searchText) {
      this.applyStatusFilter(this.events);
    } else {
      const filtered = this.events.filter(event => 
        event.name.toLowerCase().includes(this.searchText.toLowerCase()) ||
        event.description.toLowerCase().includes(this.searchText.toLowerCase())
      );
      this.applyStatusFilter(filtered);
    }
  }

  filterByStatus(): void {
    this.applyStatusFilter(this.events);
  }

  private applyStatusFilter(events: Event[]): void {
    if (!this.selectedStatus) {
      this.filteredEvents = [...events];
    } else {
      const now = new Date();
      this.filteredEvents = events.filter(event => {
        const status = this.getEventStatus(event);
        return status === this.selectedStatus;
      });
    }
  }

  getEventStatus(event: Event): string {
    const now = new Date();
    const startDate = new Date(event.dateDeb);
    const endDate = new Date(event.dateFin);
    
    if (now < startDate) {
      return 'upcoming';
    } else if (now > endDate) {
      return 'past';
    } else {
      return 'ongoing';
    }
  }

  viewEventDetails(event: Event): void {
    this.isLoading = true;
    this.eventService.getEventById(event.id!).subscribe({
      next: (eventDetails) => {
        this.isLoading = false;
        this.openEventDetailsDialog(eventDetails);
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open('Failed to load event details: ' + error.message, 'Close', {
          duration: 3000
        });
      }
    });
  }

  openEventDetailsDialog(event: Event): void {
    const dialogRef = this.dialog.open(EventDetailsDialogComponent, {
      width: '800px',
      maxWidth: '95vw',
      data: event
    });
  }
} 