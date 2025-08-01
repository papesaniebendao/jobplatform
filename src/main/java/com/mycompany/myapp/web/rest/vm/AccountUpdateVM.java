package com.mycompany.myapp.web.rest.vm;

import com.mycompany.myapp.domain.enumeration.RoleUtilisateur;

/**
 * View Model pour la mise à jour du compte utilisateur connecté.
 */
public class AccountUpdateVM {
    private String prenom;
    private String nom;
    private String nomEntreprise;
    private String secteurActivite;
    private String telephone;
    private RoleUtilisateur role;

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
}
