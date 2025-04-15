import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CountryISO, PhoneNumberFormat, SearchCountryField } from 'ngx-intl-tel-input';
import { RessourceService, Ressource } from 'src/app/services/ressource.service';
import { TypeRessourceService } from 'src/app/services/type-ressource-service.service';
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
    private typeRessourceService: TypeRessourceService
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
        fillColor: [255, 255, 255],
        textColor: [0, 0, 0],
      },
    });
  
    doc.save('ressources.pdf');
  }

  deleteRessource(ressource: Ressource): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette ressource ?')) {
      this.ressourceService.deleteRessource(ressource.id).subscribe({
        next: () => {
          this.loadResources();
        },
        error: (error: Error) => {
          console.error('Error deleting resource:', error);
        }
      });
    }
  }

  editRessource(ressource: Ressource): void {
    // Implement edit functionality
    console.log('Edit resource:', ressource);
  }

  addRessource(): void {
    this.currentSection = 'addResource';
    this.typeForm.reset();
    this.resourceForm.reset();
  }

  cancelAdd(): void {
    this.currentSection = 'content';
    this.typeForm.reset();
    this.resourceForm.reset();
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

  onSubmitProfile(): void {
    if (this.profileForm.valid) {
      console.log(this.profileForm.value);
    }
  }
}
