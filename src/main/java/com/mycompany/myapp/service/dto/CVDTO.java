package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.CV} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CVDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String urlFichier;

    private String nomFichier;

    private Instant dateUpload;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlFichier() {
        return urlFichier;
    }

    public void setUrlFichier(String urlFichier) {
        this.urlFichier = urlFichier;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public Instant getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(Instant dateUpload) {
        this.dateUpload = dateUpload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CVDTO)) {
            return false;
        }

        CVDTO cVDTO = (CVDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cVDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CVDTO{" +
            "id=" + getId() +
            ", urlFichier='" + getUrlFichier() + "'" +
            ", nomFichier='" + getNomFichier() + "'" +
            ", dateUpload='" + getDateUpload() + "'" +
            "}";
    }
}
