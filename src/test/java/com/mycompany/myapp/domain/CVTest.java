package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CVTestSamples.*;
import static com.mycompany.myapp.domain.UtilisateurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CVTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CV.class);
        CV cV1 = getCVSample1();
        CV cV2 = new CV();
        assertThat(cV1).isNotEqualTo(cV2);

        cV2.setId(cV1.getId());
        assertThat(cV1).isEqualTo(cV2);

        cV2 = getCVSample2();
        assertThat(cV1).isNotEqualTo(cV2);
    }

    @Test
    void utilisateurTest() {
        CV cV = getCVRandomSampleGenerator();
        Utilisateur utilisateurBack = getUtilisateurRandomSampleGenerator();

        cV.setUtilisateur(utilisateurBack);
        assertThat(cV.getUtilisateur()).isEqualTo(utilisateurBack);
        assertThat(utilisateurBack.getCv()).isEqualTo(cV);

        cV.utilisateur(null);
        assertThat(cV.getUtilisateur()).isNull();
        assertThat(utilisateurBack.getCv()).isNull();
    }
}
