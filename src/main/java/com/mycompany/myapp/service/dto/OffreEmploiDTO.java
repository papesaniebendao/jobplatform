package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.OffreEmploi} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OffreEmploiDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String titre;

    @NotNull(message = "must not be null")
    private String description;

    @NotNull(message = "must not be null")
    private String localisation;

    private Double salaire;

    @NotNull(message = "must not be null")
    private Instant datePublication;

    private Instant dateExpiration;

    private Boolean isActive;

    private TypeContratDTO typeContrat;

    private UtilisateurDTO recruteur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Instant getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Instant datePublication) {
        this.datePublication = datePublication;
    }

    public Instant getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Instant dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public TypeContratDTO getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContratDTO typeContrat) {
        this.typeContrat = typeContrat;
    }

    public UtilisateurDTO getRecruteur() {
        return recruteur;
    }

    public void setRecruteur(UtilisateurDTO recruteur) {
        this.recruteur = recruteur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OffreEmploiDTO)) {
            return false;
        }

        OffreEmploiDTO offreEmploiDTO = (OffreEmploiDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, offreEmploiDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OffreEmploiDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", localisation='" + getLocalisation() + "'" +
            ", salaire=" + getSalaire() +
            ", datePublication='" + getDatePublication() + "'" +
            ", dateExpiration='" + getDateExpiration() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", typeContrat=" + getTypeContrat() +
            ", recruteur=" + getRecruteur() +
            "}";
    }
}
