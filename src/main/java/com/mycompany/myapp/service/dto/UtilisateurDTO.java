package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.RoleUtilisateur;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Utilisateur} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilisateurDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String nom;

    private String telephone;

    @NotNull(message = "must not be null")
    private RoleUtilisateur role;

    @NotNull(message = "must not be null")
    private Boolean isActive;

    private UserDTO user;

    private CVDTO cv;

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CVDTO getCv() {
        return cv;
    }

    public void setCv(CVDTO cv) {
        this.cv = cv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilisateurDTO)) {
            return false;
        }

        UtilisateurDTO utilisateurDTO = (UtilisateurDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, utilisateurDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilisateurDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", role='" + getRole() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", user=" + getUser() +
            ", cv=" + getCv() +
            "}";
    }
}
