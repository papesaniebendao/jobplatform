package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Candidature} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CandidatureDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant datePostulation;

    @NotNull(message = "must not be null")
    private StatutCandidature statut;

    private UtilisateurDTO candidat;

    private OffreEmploiDTO offreEmploi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDatePostulation() {
        return datePostulation;
    }

    public void setDatePostulation(Instant datePostulation) {
        this.datePostulation = datePostulation;
    }

    public StatutCandidature getStatut() {
        return statut;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }

    public UtilisateurDTO getCandidat() {
        return candidat;
    }

    public void setCandidat(UtilisateurDTO candidat) {
        this.candidat = candidat;
    }

    public OffreEmploiDTO getOffreEmploi() {
        return offreEmploi;
    }

    public void setOffreEmploi(OffreEmploiDTO offreEmploi) {
        this.offreEmploi = offreEmploi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CandidatureDTO)) {
            return false;
        }

        CandidatureDTO candidatureDTO = (CandidatureDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, candidatureDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CandidatureDTO{" +
            "id=" + getId() +
            ", datePostulation='" + getDatePostulation() + "'" +
            ", statut='" + getStatut() + "'" +
            ", candidat=" + getCandidat() +
            ", offreEmploi=" + getOffreEmploi() +
            "}";
    }
}
