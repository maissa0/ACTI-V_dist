import { Component, OnInit } from '@angular/core';
import { Ressource, RessourceService } from 'src/app/services/ressource.service';
import { TypeRessourceServiceService } from 'src/app/services/type-ressource-service.service';

@Component({
  selector: 'app-ressources',
  templateUrl: './ressources.component.html',
  styleUrls: ['./ressources.component.css']
})
export class RessourcesComponent implements OnInit {
  displayedColumns: string[] = ['nom', 'quantite', 'type']; // No IDs
  ressources: any[] = [];
  filteredRessources: any[] = [];
  searchText: string = ''; // Search text
  selectedType: string = ''; // Selected type for filtering
  uniqueTypes: string[] = [];

  constructor(private ressourceService: RessourceService) {}

  ngOnInit(): void {
    this.ressourceService.getAllRessources().subscribe(data => {
      this.ressources = data.map(r => ({
        nom: r.nom,
        quantite: r.quantite,
        type: r.type ? r.type.nom : 'N/A' // Handle missing or undefined type
      }));
      this.filteredRessources = this.ressources; // Initialize filtered resources
      this.uniqueTypes = [...new Set(this.ressources.map(r => r.type))];
    });
  }
  filterByType(): void {
    let filtered = this.ressources;
    if (this.selectedType) {
      filtered = filtered.filter(ressource => ressource.type === this.selectedType);
    }
    this.filteredRessources = filtered;
    this.searchRessourceByName();  // Apply search filter after type filtering
  }
  searchRessourceByName(): void {
    let filtered = this.ressources;
    if (this.searchText) {
      filtered = filtered.filter(ressource =>
        ressource.nom.toLowerCase().includes(this.searchText.toLowerCase())
      );
    }
    if (this.selectedType) {
      filtered = filtered.filter(ressource => ressource.type === this.selectedType);
    }
    this.filteredRessources = filtered;
  }

  // Method to download PDF (just a placeholder)
  downloadPDF(ressource: any): void {
    console.log('Download PDF for: ', ressource.nom);
    // Example: Integration with jsPDF to generate PDF
    // const doc = new jsPDF();
    // doc.text(ressource.nom, 10, 10);
    // doc.save(`${ressource.nom}.pdf`);
  }


}
