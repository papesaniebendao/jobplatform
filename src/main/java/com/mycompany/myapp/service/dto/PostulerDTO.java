package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.NotNull;

public class PostulerDTO {
    @NotNull
    private Long offreId;

    @NotNull
    private Long candidatId;

    public Long getOffreId() {
        return offreId;
    }
    public void setOffreId(Long offreId) {
        this.offreId = offreId;
    }

    public Long getCandidatId() {
        return candidatId;
    }
    public void setCandidatId(Long candidatId) {
        this.candidatId = candidatId;
    }
}
