package com.mycompany.myapp.web.rest.vm;

import com.mycompany.myapp.service.dto.AdminUserDTO;
import com.mycompany.myapp.domain.enumeration.RoleUtilisateur;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI
 * and during registration.
 */
public class ManagedUserVM extends AdminUserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    // ---- Champs supplémentaires ----
    private String prenom; 
    private String nom;             // Pour candidats
    @NotNull                 // Nom ou nom entreprise
    private String nomEntreprise;       // Pour recruteurs
    private String secteurActivite;     // Pour recruteurs
    private String telephone;
    @NotNull
    private RoleUtilisateur role;       // CANDIDAT ou RECRUTEUR

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }
    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getSecteurActivite() {
        return secteurActivite;
    }
    public void setSecteurActivite(String secteurActivite) {
        this.secteurActivite = secteurActivite;
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
            ", prenom='" + prenom + '\'' +
            ", nom='" + nom + '\'' +
            ", nomEntreprise='" + nomEntreprise + '\'' +
            ", secteurActivite='" + secteurActivite + '\'' +
            ", telephone='" + telephone + '\'' +
            ", role=" + role +
            "} " + super.toString();
    }
}
