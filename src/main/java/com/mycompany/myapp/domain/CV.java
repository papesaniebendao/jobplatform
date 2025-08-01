package com.mycompany.myapp.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CV.
 */
@Table("cv")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CV implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("url_fichier")
    private String urlFichier;

    @Column("nom_fichier")
    private String nomFichier;

    @Column("date_upload")
    private Instant dateUpload;

    @org.springframework.data.annotation.Transient
    private Utilisateur utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CV id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlFichier() {
        return this.urlFichier;
    }

    public CV urlFichier(String urlFichier) {
        this.setUrlFichier(urlFichier);
        return this;
    }

    public void setUrlFichier(String urlFichier) {
        this.urlFichier = urlFichier;
    }

    public String getNomFichier() {
        return this.nomFichier;
    }

    public CV nomFichier(String nomFichier) {
        this.setNomFichier(nomFichier);
        return this;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public Instant getDateUpload() {
        return this.dateUpload;
    }

    public CV dateUpload(Instant dateUpload) {
        this.setDateUpload(dateUpload);
        return this;
    }

    public void setDateUpload(Instant dateUpload) {
        this.dateUpload = dateUpload;
    }

    public Utilisateur getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        if (this.utilisateur != null) {
            this.utilisateur.setCv(null);
        }
        if (utilisateur != null) {
            utilisateur.setCv(this);
        }
        this.utilisateur = utilisateur;
    }

    public CV utilisateur(Utilisateur utilisateur) {
        this.setUtilisateur(utilisateur);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CV)) {
            return false;
        }
        return getId() != null && getId().equals(((CV) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CV{" +
            "id=" + getId() +
            ", urlFichier='" + getUrlFichier() + "'" +
            ", nomFichier='" + getNomFichier() + "'" +
            ", dateUpload='" + getDateUpload() + "'" +
            "}";
    }
}
