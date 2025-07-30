package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TypeContrat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypeContratDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String nom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeContratDTO)) {
            return false;
        }

        TypeContratDTO typeContratDTO = (TypeContratDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, typeContratDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypeContratDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
