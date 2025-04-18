import { Component, OnInit } from '@angular/core';
import { Ressource, RessourceService } from 'src/app/services/ressource.service';
import { TypeRessourceService } from 'src/app/services/type-ressource-service.service';
import { jsPDF } from 'jspdf'; // Importer jsPDF
import autoTable from 'jspdf-autotable'; // Importer autoTable
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-ressources',
  templateUrl: './ressources.component.html',
  styleUrls: ['./ressources.component.css']
})

export class RessourcesComponent implements OnInit {
  
  displayedColumns: string[] = ['nom', 'type', 'quantite', 'actions']; // Include type column
  ressources: Ressource[] = [];
  filteredRessources: Ressource[] = [];
  searchText: string = ''; // Search text
  selectedType: string = ''; // Selected type for filtering
  uniqueTypes: string[] = [];
  selectedTypeId: number | null = null;
  types: any[] = [];
  isLoading: boolean = false;

  constructor(
    private ressourceService: RessourceService,
    private typeRessourceService: TypeRessourceService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadResources();
    this.loadResourceTypes();
  }

  loadResources(): void {
    this.isLoading = true;
    this.ressourceService.getAllRessources().subscribe({
      next: (data) => {
        console.log("Resources received:", data);
        
        // Debug output for the first item if available
        if (data && data.length > 0) {
          console.log("First resource structure:", JSON.stringify(data[0]));
          console.log("Type field structure:", data[0].type);
          console.log("Quantity field:", data[0].quantite);
        }
        
        this.ressources = data;
        this.filteredRessources = [...this.ressources];
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error loading resources:", error);
        this.snackBar.open("Erreur lors du chargement des ressources", "Fermer", {
          duration: 3000
        });
        this.isLoading = false;
      }
    });
  }

  loadResourceTypes(): void {
    this.typeRessourceService.getAllTypes().subscribe({
      next: (data) => {
        console.log("Resource types received:", data);
        this.types = data;
      },
      error: (error) => {
        console.error("Error loading resource types:", error);
        this.snackBar.open("Erreur lors du chargement des types de ressources", "Fermer", {
          duration: 3000
        });
      }
    });
  }
  
  filterByType(): void {
    if (this.selectedTypeId) {
      this.ressourceService.getRessourcesByType(this.selectedTypeId).subscribe({
        next: (data) => {
          console.log("Resources by type received:", data);
          this.filteredRessources = data;
        },
        error: (error) => {
          console.error("Error filtering resources by type:", error);
          this.snackBar.open("Erreur lors du filtrage des ressources", "Fermer", {
            duration: 3000
          });
        }
      });
    } else {
      this.filteredRessources = [...this.ressources]; // Reset if no type selected
    }
  }
  
  searchRessourceByName(): void {
    if (!this.searchText) {
      this.filteredRessources = [...this.ressources];
      return;
    }
    
    this.filteredRessources = this.ressources.filter(ressource =>
      ressource.nom.toLowerCase().includes(this.searchText.toLowerCase())
    );
  }

  // Method to download PDF
  downloadAllRessourcesPDF(): void {
    const doc = new jsPDF();
  
    // Set PDF title
    doc.setFontSize(16);
    doc.text('Liste des Ressources', 14, 20);
  
    // Prepare table data
    const tableData = this.filteredRessources.map(r => [
      r.nom, 
      r.type?.nom || 'Non spécifié', 
      r.quantite.toString()
    ]);
  
    // Define table columns
    const columns = ['Nom', 'Type', 'Quantité'];
  
    // Generate table in the PDF
    autoTable(doc, {
      head: [columns],
      body: tableData,
      startY: 30,
      theme: 'grid',
      margin: { top: 10, left: 14, right: 14 },
      headStyles: {
        fillColor: [22, 160, 133],
        textColor: [255, 255, 255],
        fontStyle: 'bold',
      },
      bodyStyles: {
        fillColor: [255, 255, 255],
        textColor: [0, 0, 0],
      },
    });
  
    // Save the PDF
    doc.save('ressources.pdf');
  }
  
  deleteRessource(ressource: Ressource): void {
    if (!ressource.id) {
      this.snackBar.open("Impossible de supprimer une ressource sans identifiant", "Fermer", {
        duration: 3000
      });
      return;
    }
    
    if (confirm('Êtes-vous sûr de vouloir supprimer cette ressource ?')) {
      this.ressourceService.deleteRessource(ressource.id).subscribe({
        next: () => {
          // Remove the resource from the arrays
          this.ressources = this.ressources.filter(r => r.id !== ressource.id);
          this.filteredRessources = this.filteredRessources.filter(r => r.id !== ressource.id);
          this.snackBar.open("Ressource supprimée avec succès", "Fermer", {
            duration: 3000
          });
        },
        error: (error) => {
          console.error('Erreur lors de la suppression de la ressource:', error);
          this.snackBar.open("Erreur lors de la suppression de la ressource", "Fermer", {
            duration: 3000
          });
        }
      });
    }
  }

  addRessource(): void {
    // Implement resource addition (could open a dialog)
    console.log('Add resource clicked');
    this.snackBar.open('Add resource functionality coming soon!', 'Close', {
      duration: 3000
    });
  }

  editRessource(ressource: Ressource): void {
    // Implement resource editing (could open a dialog)
    console.log('Edit resource clicked', ressource);
    this.snackBar.open('Edit resource functionality coming soon!', 'Close', {
      duration: 3000
    });
  }
}
