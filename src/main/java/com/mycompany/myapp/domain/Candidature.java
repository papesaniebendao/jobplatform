package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Candidature.
 */
@Table("candidature")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Candidature implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("date_postulation")
    private Instant datePostulation;

    @NotNull(message = "must not be null")
    @Column("statut")
    private StatutCandidature statut;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "cv" }, allowSetters = true)
    private Utilisateur candidat;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "typeContrat", "recruteur" }, allowSetters = true)
    private OffreEmploi offreEmploi;

    @Column("candidat_id")
    private Long candidatId;

    @Column("offre_emploi_id")
    private Long offreEmploiId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Candidature id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDatePostulation() {
        return this.datePostulation;
    }

    public Candidature datePostulation(Instant datePostulation) {
        this.setDatePostulation(datePostulation);
        return this;
    }

    public void setDatePostulation(Instant datePostulation) {
        this.datePostulation = datePostulation;
    }

    public StatutCandidature getStatut() {
        return this.statut;
    }

    public Candidature statut(StatutCandidature statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }

    public Utilisateur getCandidat() {
        return this.candidat;
    }

    public void setCandidat(Utilisateur utilisateur) {
        this.candidat = utilisateur;
        this.candidatId = utilisateur != null ? utilisateur.getId() : null;
    }

    public Candidature candidat(Utilisateur utilisateur) {
        this.setCandidat(utilisateur);
        return this;
    }

    public OffreEmploi getOffreEmploi() {
        return this.offreEmploi;
    }

    public void setOffreEmploi(OffreEmploi offreEmploi) {
        this.offreEmploi = offreEmploi;
        this.offreEmploiId = offreEmploi != null ? offreEmploi.getId() : null;
    }

    public Candidature offreEmploi(OffreEmploi offreEmploi) {
        this.setOffreEmploi(offreEmploi);
        return this;
    }

    public Long getCandidatId() {
        return this.candidatId;
    }

    public void setCandidatId(Long utilisateur) {
        this.candidatId = utilisateur;
    }

    public Long getOffreEmploiId() {
        return this.offreEmploiId;
    }

    public void setOffreEmploiId(Long offreEmploi) {
        this.offreEmploiId = offreEmploi;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Candidature)) {
            return false;
        }
        return getId() != null && getId().equals(((Candidature) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Candidature{" +
            "id=" + getId() +
            ", datePostulation='" + getDatePostulation() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
