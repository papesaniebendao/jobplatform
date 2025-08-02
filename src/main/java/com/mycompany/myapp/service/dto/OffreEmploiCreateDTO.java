package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * DTO utilisé lors de la création d'une offre d'emploi par un recruteur.
 */
public class OffreEmploiCreateDTO implements Serializable {

    @NotNull
    @Size(min = 2, max = 100)
    private String titre;

    @NotNull
    @Size(min = 10, max = 2000)
    private String description;

    @NotNull
    @Size(min = 2, max = 100)
    private String localisation;

    private Double salaire;

    @NotNull
    private Instant dateExpiration; // La date de publication est générée côté serveur

    @NotNull
    private Long typeContratId; // on envoie juste l'ID du type de contrat

    // --- GETTERS & SETTERS ---

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public Double getSalaire() {
        return salaire;
    }

    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Instant getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Instant dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Long getTypeContratId() {
        return typeContratId;
    }

    public void setTypeContratId(Long typeContratId) {
        this.typeContratId = typeContratId;
    }
}
