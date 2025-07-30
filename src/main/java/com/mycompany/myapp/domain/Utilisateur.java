package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.RoleUtilisateur;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Utilisateur.
 */
@Table("utilisateur")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nom")
    private String nom;

    @Column("telephone")
    private String telephone;

    @NotNull(message = "must not be null")
    @Column("role")
    private RoleUtilisateur role;

    @NotNull(message = "must not be null")
    @Column("is_active")
    private Boolean isActive;

    @org.springframework.data.annotation.Transient
    private User user;

    @org.springframework.data.annotation.Transient
    private CV cv;

    @Column("user_id")
    private Long userId;

    @Column("cv_id")
    private Long cvId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Utilisateur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Utilisateur nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Utilisateur telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public RoleUtilisateur getRole() {
        return this.role;
    }

    public Utilisateur role(RoleUtilisateur role) {
        this.setRole(role);
        return this;
    }

    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Utilisateur isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Utilisateur user(User user) {
        this.setUser(user);
        return this;
    }

    public CV getCv() {
        return this.cv;
    }

    public void setCv(CV cV) {
        this.cv = cV;
        this.cvId = cV != null ? cV.getId() : null;
    }

    public Utilisateur cv(CV cV) {
        this.setCv(cV);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getCvId() {
        return this.cvId;
    }

    public void setCvId(Long cV) {
        this.cvId = cV;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utilisateur)) {
            return false;
        }
        return getId() != null && getId().equals(((Utilisateur) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Utilisateur{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", role='" + getRole() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
