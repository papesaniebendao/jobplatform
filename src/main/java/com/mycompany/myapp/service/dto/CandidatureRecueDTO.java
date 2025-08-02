package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour représenter une candidature reçue par un recruteur
 * incluant les informations du candidat et de l'offre.
 */
public class CandidatureRecueDTO implements Serializable {

    private Long candidatureId;
    private Instant datePostulation;
    private String statut;

    private Long candidatId;
    private String candidatNom;
    private String candidatTelephone;
    private String candidatEmail;
    private String cvUrl;

    private Long offreId;
    private String offreTitre;

    public Long getCandidatureId() {
        return candidatureId;
    }

    public void setCandidatureId(Long candidatureId) {
        this.candidatureId = candidatureId;
    }

    public Instant getDatePostulation() {
        return datePostulation;
    }

    public void setDatePostulation(Instant datePostulation) {
        this.datePostulation = datePostulation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Long getCandidatId() {
        return candidatId;
    }

    public void setCandidatId(Long candidatId) {
        this.candidatId = candidatId;
    }

    public String getCandidatNom() {
        return candidatNom;
    }

    public void setCandidatNom(String candidatNom) {
        this.candidatNom = candidatNom;
    }

    public String getCandidatTelephone() {
        return candidatTelephone;
    }

    public void setCandidatTelephone(String candidatTelephone) {
        this.candidatTelephone = candidatTelephone;
    }

    public String getCandidatEmail() {
        return candidatEmail;
    }

    public void setCandidatEmail(String candidatEmail) {
        this.candidatEmail = candidatEmail;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public Long getOffreId() {
        return offreId;
    }

    public void setOffreId(Long offreId) {
        this.offreId = offreId;
    }

    public String getOffreTitre() {
        return offreTitre;
    }

    public void setOffreTitre(String offreTitre) {
        this.offreTitre = offreTitre;
    }

    @Override
    public String toString() {
        return "CandidatureRecueDTO{" +
            "candidatureId=" + candidatureId +
            ", datePostulation=" + datePostulation +
            ", statut='" + statut + '\'' +
            ", candidatId=" + candidatId +
            ", candidatNom='" + candidatNom + '\'' +
            ", candidatTelephone='" + candidatTelephone + '\'' +
            ", candidatEmail='" + candidatEmail + '\'' +
            ", cvUrl='" + cvUrl + '\'' +
            ", offreId=" + offreId +
            ", offreTitre='" + offreTitre + '\'' +
            '}';
    }
}
