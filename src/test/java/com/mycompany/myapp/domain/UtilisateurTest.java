package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CVTestSamples.*;
import static com.mycompany.myapp.domain.UtilisateurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtilisateurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Utilisateur.class);
        Utilisateur utilisateur1 = getUtilisateurSample1();
        Utilisateur utilisateur2 = new Utilisateur();
        assertThat(utilisateur1).isNotEqualTo(utilisateur2);

        utilisateur2.setId(utilisateur1.getId());
        assertThat(utilisateur1).isEqualTo(utilisateur2);

        utilisateur2 = getUtilisateurSample2();
        assertThat(utilisateur1).isNotEqualTo(utilisateur2);
    }

    @Test
    void cvTest() {
        Utilisateur utilisateur = getUtilisateurRandomSampleGenerator();
        CV cVBack = getCVRandomSampleGenerator();

        utilisateur.setCv(cVBack);
        assertThat(utilisateur.getCv()).isEqualTo(cVBack);

        utilisateur.cv(null);
        assertThat(utilisateur.getCv()).isNull();
    }
}
