package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.OffreEmploiTestSamples.*;
import static com.mycompany.myapp.domain.TypeContratTestSamples.*;
import static com.mycompany.myapp.domain.UtilisateurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OffreEmploiTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OffreEmploi.class);
        OffreEmploi offreEmploi1 = getOffreEmploiSample1();
        OffreEmploi offreEmploi2 = new OffreEmploi();
        assertThat(offreEmploi1).isNotEqualTo(offreEmploi2);

        offreEmploi2.setId(offreEmploi1.getId());
        assertThat(offreEmploi1).isEqualTo(offreEmploi2);

        offreEmploi2 = getOffreEmploiSample2();
        assertThat(offreEmploi1).isNotEqualTo(offreEmploi2);
    }

    @Test
    void typeContratTest() {
        OffreEmploi offreEmploi = getOffreEmploiRandomSampleGenerator();
        TypeContrat typeContratBack = getTypeContratRandomSampleGenerator();

        offreEmploi.setTypeContrat(typeContratBack);
        assertThat(offreEmploi.getTypeContrat()).isEqualTo(typeContratBack);

        offreEmploi.typeContrat(null);
        assertThat(offreEmploi.getTypeContrat()).isNull();
    }

    @Test
    void recruteurTest() {
        OffreEmploi offreEmploi = getOffreEmploiRandomSampleGenerator();
        Utilisateur utilisateurBack = getUtilisateurRandomSampleGenerator();

        offreEmploi.setRecruteur(utilisateurBack);
        assertThat(offreEmploi.getRecruteur()).isEqualTo(utilisateurBack);

        offreEmploi.recruteur(null);
        assertThat(offreEmploi.getRecruteur()).isNull();
    }
}
