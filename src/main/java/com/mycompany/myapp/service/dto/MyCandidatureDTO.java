package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.StatutCandidature;

public class MyCandidatureDTO {

    private Long candidatureId;
    private String titreOffre;
    private String descriptionOffre;
    private String typeContrat;
    private StatutCandidature statut;

    public MyCandidatureDTO(Long candidatureId, String titreOffre, String descriptionOffre, String typeContrat, StatutCandidature statut) {
        this.candidatureId = candidatureId;
        this.titreOffre = titreOffre;
        this.descriptionOffre = descriptionOffre;
        this.typeContrat = typeContrat;
        this.statut = statut;
    }

    public Long getCandidatureId() {
        return candidatureId;
    }

    public void setCandidatureId(Long candidatureId) {
        this.candidatureId = candidatureId;
    }

    public String getTitreOffre() {
        return titreOffre;
    }

    public void setTitreOffre(String titreOffre) {
        this.titreOffre = titreOffre;
    }

    public String getDescriptionOffre() {
        return descriptionOffre;
    }

    public void setDescriptionOffre(String descriptionOffre) {
        this.descriptionOffre = descriptionOffre;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public StatutCandidature getStatut() {
        return statut;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }
}
