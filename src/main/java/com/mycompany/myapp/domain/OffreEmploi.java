package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A OffreEmploi.
 */
@Table("offre_emploi")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OffreEmploi implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("titre")
    private String titre;

    @NotNull(message = "must not be null")
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("localisation")
    private String localisation;

    @Column("salaire")
    private Double salaire;

    @NotNull(message = "must not be null")
    @Column("date_publication")
    private Instant datePublication;

    @Column("date_expiration")
    private Instant dateExpiration;

    @Column("is_active")
    private Boolean isActive;

    @org.springframework.data.annotation.Transient
    private TypeContrat typeContrat;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "cv" }, allowSetters = true)
    private Utilisateur recruteur;

    @Column("type_contrat_id")
    private Long typeContratId;

    @Column("recruteur_id")
    private Long recruteurId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OffreEmploi id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public OffreEmploi titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return this.description;
    }

    public OffreEmploi description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocalisation() {
        return this.localisation;
    }

    public OffreEmploi localisation(String localisation) {
        this.setLocalisation(localisation);
        return this;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public Double getSalaire() {
        return this.salaire;
    }

    public OffreEmploi salaire(Double salaire) {
        this.setSalaire(salaire);
        return this;
    }

    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Instant getDatePublication() {
        return this.datePublication;
    }

    public OffreEmploi datePublication(Instant datePublication) {
        this.setDatePublication(datePublication);
        return this;
    }

    public void setDatePublication(Instant datePublication) {
        this.datePublication = datePublication;
    }

    public Instant getDateExpiration() {
        return this.dateExpiration;
    }

    public OffreEmploi dateExpiration(Instant dateExpiration) {
        this.setDateExpiration(dateExpiration);
        return this;
    }

    public void setDateExpiration(Instant dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public OffreEmploi isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public TypeContrat getTypeContrat() {
        return this.typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
        this.typeContratId = typeContrat != null ? typeContrat.getId() : null;
    }

    public OffreEmploi typeContrat(TypeContrat typeContrat) {
        this.setTypeContrat(typeContrat);
        return this;
    }

    public Utilisateur getRecruteur() {
        return this.recruteur;
    }

    public void setRecruteur(Utilisateur utilisateur) {
        this.recruteur = utilisateur;
        this.recruteurId = utilisateur != null ? utilisateur.getId() : null;
    }

    public OffreEmploi recruteur(Utilisateur utilisateur) {
        this.setRecruteur(utilisateur);
        return this;
    }

    public Long getTypeContratId() {
        return this.typeContratId;
    }

    public void setTypeContratId(Long typeContrat) {
        this.typeContratId = typeContrat;
    }

    public Long getRecruteurId() {
        return this.recruteurId;
    }

    public void setRecruteurId(Long utilisateur) {
        this.recruteurId = utilisateur;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OffreEmploi)) {
            return false;
        }
        return getId() != null && getId().equals(((OffreEmploi) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OffreEmploi{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", localisation='" + getLocalisation() + "'" +
            ", salaire=" + getSalaire() +
            ", datePublication='" + getDatePublication() + "'" +
            ", dateExpiration='" + getDateExpiration() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
