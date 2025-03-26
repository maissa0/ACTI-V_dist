import { Component, OnInit } from '@angular/core';
import { Ressource, RessourceService } from 'src/app/services/ressource.service';
import { TypeRessourceService } from 'src/app/services/type-ressource-service.service';
import { jsPDF } from 'jspdf'; // Importer jsPDF
import autoTable from 'jspdf-autotable'; // Importer autoTable



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
  selectedTypeId: number | null = null;
  types: any[] = [];



  constructor(private ressourceService: RessourceService  ,
    private typeRessourceService: TypeRessourceService

  ) {}

  ngOnInit(): void {
    this.ressourceService.getAllRessources().subscribe(data => {
      console.log("Ressources récupérées :", data); // 🔍 Vérifie ici
  
      this.ressources = data.map(r => ({
        nom: r.nom,
        quantite: r.quantite,
        type: r.type ? r.type.nom : 'N/A' // Vérifie si `type.nom` est bien défini
      }));
  
      this.filteredRessources = [...this.ressources]; // Copie initiale pour le filtre
  
      this.typeRessourceService.getAllTypes().subscribe(data => {
        console.log("Types récupérés :", data); // 🔍 Vérifie ici aussi
        this.types = data;
        this.uniqueTypes = [...new Set(this.ressources.map(r => r.type))];
      });
  
    }, error => console.error("Erreur lors de la récupération des ressources :", error));
  }
  
  filterByType() {
    if (this.selectedTypeId) {
      this.ressourceService.getRessourcesByType(this.selectedTypeId).subscribe(data => {
        this.filteredRessources = data.map(r => ({
          nom: r.nom,
          quantite: r.quantite,
          type: r.type ? r.type.nom : 'N/A'
        }));
      });
    } else {
      this.filteredRessources = [...this.ressources]; // Réinitialise si aucun type sélectionné
    }
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
  downloadAllRessourcesPDF(): void {
    const doc = new jsPDF();
  
    // Définir le titre du PDF
    doc.setFontSize(16);
    doc.text('Liste des Ressources', 14, 20);
  
    // Ajouter un tableau moderne
    const tableData = this.filteredRessources.map(r => [
      r.nom, r.quantite, r.type && r.type !== 'N/A' ? r.type : 'Non spécifié'
    ]);
  
    // Définir les colonnes du tableau
    const columns = ['Nom', 'Quantité', 'Type'];
  
    // Utilisation de autoTable pour générer le tableau dans le PDF
    autoTable(doc, {
      head: [columns],
      body: tableData,
      startY: 30, // Positionner le tableau un peu plus bas
      theme: 'grid', // Utiliser un thème moderne avec des bordures
      margin: { top: 10, left: 14, right: 14 }, // Ajouter des marges pour une meilleure lisibilité
      headStyles: {
        fillColor: [22, 160, 133], // Couleur de fond des en-têtes
        textColor: [255, 255, 255], // Couleur du texte des en-têtes
        fontStyle: 'bold', // Style en gras
      },
      bodyStyles: {
        fillColor: [255, 255, 255], // Couleur de fond des cellules
        textColor: [0, 0, 0], // Couleur du texte des cellules
      },
    });
  
    // Sauvegarder le PDF
    doc.save('ressources.pdf');
  }
  
}
