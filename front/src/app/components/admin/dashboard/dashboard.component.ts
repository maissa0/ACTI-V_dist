import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CountryISO, PhoneNumberFormat, SearchCountryField } from 'ngx-intl-tel-input';
import { RessourceService, Ressource } from 'src/app/services/ressource.service';
import { TypeRessourceService } from 'src/app/services/type-ressource-service.service';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentSection: string = 'dashboard';
  profileForm: FormGroup;
  typeForm: FormGroup;
  resourceForm: FormGroup;
  
  // User role related properties
  currentUser: any;
  isAdmin: boolean = false;
  userRoleDisplay: string = 'User';
  profileImage: string | null = null;
  
  // Resources related properties
  displayedColumns: string[] = ['nom', 'quantite', 'type', 'actions'];
  ressources: Ressource[] = [];
  filteredRessources: Ressource[] = [];
  searchText: string = '';
  selectedTypeId: number | null = null;
  types: any[] = [];
  
  // Phone input configuration
  separateDialCode = true;
  SearchCountryField = SearchCountryField;
  CountryISO = CountryISO;
  PhoneNumberFormat = PhoneNumberFormat;
  preferredCountries: CountryISO[] = [CountryISO.UnitedStates, CountryISO.UnitedKingdom];

  constructor(
    private fb: FormBuilder,
    private ressourceService: RessourceService,
    private typeRessourceService: TypeRessourceService,
    private authService: AuthService,
    private router: Router
  ) {
    this.profileForm = this.fb.group({
      name: ['John', Validators.required],
      surname: ['Doe', Validators.required],
      headline: ['UI/UX Developer', Validators.required],
      currentPosition: ['UI/UX Developer at Boston', Validators.required],
      location: ['Boston University', Validators.required],
      country: ['USA', Validators.required],
      stateRegion: ['Boston', Validators.required],
      phone: [undefined, [Validators.required]]
    });

    this.typeForm = this.fb.group({
      nomType: ['', Validators.required]
    });

    this.resourceForm = this.fb.group({
      nom: ['', Validators.required],
      quantite: ['', [Validators.required, Validators.min(0)]],
      typeId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadResources();
    this.loadTypes();
    this.checkUserRole();
    this.loadProfileImage();
  }

  checkUserRole(): void {
    this.currentUser = this.authService.currentUserValue;
    if (this.currentUser) {
      // Check if user has admin role in the roles array
      if (this.currentUser.roles) {
        this.isAdmin = this.currentUser.roles.includes('ROLE_ADMIN') || 
                      this.currentUser.roles.includes('ADMIN');
        
        // Set display value for user role
        if (this.isAdmin) {
          this.userRoleDisplay = 'Administrator';
        } else if (this.currentUser.roles.includes('ROLE_USER')) {
          this.userRoleDisplay = 'Regular User';
        }
      } else if (this.currentUser.role) {
        // Fallback for older role format
        this.isAdmin = this.currentUser.role === 'ROLE_ADMIN';
        this.userRoleDisplay = this.currentUser.role === 'ROLE_ADMIN' ? 
                              'Administrator' : 'Regular User';
      }
    }
  }

  loadProfileImage(): void {
    // This is a placeholder for actual profile image loading
    // In a real implementation, this would fetch from a user profile service
    if (this.currentUser && this.currentUser.profileImage) {
      this.profileImage = this.currentUser.profileImage;
    }
  }

  getPageTitle(): string {
    switch (this.currentSection) {
      case 'dashboard':
        return 'Dashboard';
      case 'content':
        return 'Resource Management';
      case 'user-management':
        return 'User Management';
      case 'event-management':
        return 'Event Management';
      case 'analytics':
        return 'Analytics';
      case 'likes':
        return 'Likes Overview';
      case 'comments':
        return 'Comments Management';
      case 'addResource':
        return 'Add New Resource';
      default:
        return 'Dashboard';
    }
  }

  showSection(section: string): void {
    this.currentSection = section;
    if (section === 'content') {
      this.loadResources();
    }
  }

  loadResources(): void {
    this.ressourceService.getAllRessources().subscribe({
      next: (data: Ressource[]) => {
        this.ressources = data;
        this.filteredRessources = [...this.ressources];
      },
      error: (error: Error) => {
        console.error('Error loading resources:', error);
      }
    });
  }

  loadTypes(): void {
    this.typeRessourceService.getAllTypes().subscribe({
      next: (data: any[]) => {
        this.types = data;
      },
      error: (error: Error) => {
        console.error('Error loading types:', error);
      }
    });
  }

  searchRessourceByName(): void {
    if (!this.searchText) {
      this.filteredRessources = [...this.ressources];
    } else {
      this.filteredRessources = this.ressources.filter(ressource =>
        ressource.nom.toLowerCase().includes(this.searchText.toLowerCase())
      );
    }
  }

  filterByType(): void {
    if (!this.selectedTypeId) {
      this.loadResources();  // Load all resources if no type is selected
    } else {
      this.ressourceService.getRessourcesByType(this.selectedTypeId).subscribe({
        next: (data: Ressource[]) => {
          this.filteredRessources = data;
        },
        error: (error: Error) => {
          console.error('Error filtering resources:', error);
        }
      });
    }
  }

  downloadAllRessourcesPDF(): void {
    const doc = new jsPDF();
  
    doc.setFontSize(16);
    doc.text('Liste des Ressources', 14, 20);
  
    const tableData = this.filteredRessources.map(r => [
      r.nom, 
      r.quantite, 
      r.type ? r.type.nom : 'N/A'
    ]);
  
    const columns = ['Nom', 'Quantité', 'Type'];
  
    autoTable(doc, {
      head: [columns],
      body: tableData,
      startY: 30,
      theme: 'grid',
      margin: { top: 10, left: 14, right: 14 },
      headStyles: {
        fillColor: [196, 110, 239],
        textColor: [255, 255, 255],
        fontStyle: 'bold',
      },
      bodyStyles: {
        textColor: [50, 50, 50]
      }
    });
  
    doc.save('ressources.pdf');
  }

  // Resource CRUD operations
  addRessource(): void {
    this.resourceForm.reset();
    // Show the add resource form/modal logic would go here
    this.currentSection = 'addResource';
  }

  editRessource(resource: Ressource): void {
    this.resourceForm.patchValue({
      nom: resource.nom,
      quantite: resource.quantite,
      typeId: resource.type?.id
    });
    // Show the edit resource form/modal logic would go here
  }

  deleteRessource(resource: Ressource): void {
    if (confirm(`Are you sure you want to delete ${resource.nom}?`)) {
      this.ressourceService.deleteRessource(resource.id).subscribe({
        next: () => {
          this.loadResources();
        },
        error: (error: Error) => {
          console.error('Error deleting resource:', error);
        }
      });
    }
  }

  // Methods for the Add Resource section
  onSubmitResource(): void {
    if (this.resourceForm.valid) {
      const resourceData: Partial<Ressource> = {
        nom: this.resourceForm.get('nom')?.value,
        quantite: this.resourceForm.get('quantite')?.value,
        type: {
          id: this.resourceForm.get('typeId')?.value,
          nom: '' // This will be filled by the backend
        }
      };
      
      this.ressourceService.createRessource(resourceData as Ressource).subscribe({
        next: () => {
          this.loadResources();
          this.resourceForm.reset();
          this.currentSection = 'content';
        },
        error: (error: Error) => {
          console.error('Error creating resource:', error);
        }
      });
    }
  }

  onSubmitType(): void {
    if (this.typeForm.valid) {
      const typeData = {
        nom: this.typeForm.get('nomType')?.value
      };
      
      this.typeRessourceService.createType(typeData).subscribe({
        next: () => {
          this.loadTypes();
          this.typeForm.reset();
        },
        error: (error: Error) => {
          console.error('Error creating type:', error);
        }
      });
    }
  }

  cancelAdd(): void {
    this.currentSection = 'content';
    this.typeForm.reset();
    this.resourceForm.reset();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
