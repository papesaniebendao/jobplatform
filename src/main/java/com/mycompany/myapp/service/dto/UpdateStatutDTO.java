package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import jakarta.validation.constraints.NotNull;

public class UpdateStatutDTO {

    @NotNull
    private StatutCandidature statut;

    public StatutCandidature getStatut() {
        return statut;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }
}
