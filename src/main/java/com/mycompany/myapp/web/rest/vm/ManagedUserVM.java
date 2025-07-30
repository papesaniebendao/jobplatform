package com.mycompany.myapp.web.rest.vm;

import com.mycompany.myapp.service.dto.AdminUserDTO;
import com.mycompany.myapp.domain.enumeration.RoleUtilisateur;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends AdminUserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    // ---- Champs supplémentaires ----
    @NotNull
    private String nom;

    private String telephone;

    @NotNull
    private RoleUtilisateur role;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
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

    // prettier-ignore
    @Override
    public String toString() {
        return "ManagedUserVM{" +
            "password='" + password + '\'' +
            ", nom='" + nom + '\'' +
            ", telephone='" + telephone + '\'' +
            ", role=" + role +
            "} " + super.toString();
    }
}
