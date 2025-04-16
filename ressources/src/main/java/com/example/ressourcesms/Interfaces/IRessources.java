package com.example.ressourcesms.Interfaces;

import com.example.ressourcesms.Entities.Ressource;

import java.util.List;

public interface IRessources {
    public Ressource saveRessource(Ressource ressource);
    public Ressource deleteRessource(int id);
    public Ressource updateRessource(Ressource ressource);
    public Ressource getRessourceById(int id);
    public List<Ressource> getAllRessources();
    public List<Ressource> getRessourcesByEventId(Long eventId);
}
