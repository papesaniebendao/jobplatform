package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CandidatureTestSamples.*;
import static com.mycompany.myapp.domain.OffreEmploiTestSamples.*;
import static com.mycompany.myapp.domain.UtilisateurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CandidatureTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Candidature.class);
        Candidature candidature1 = getCandidatureSample1();
        Candidature candidature2 = new Candidature();
        assertThat(candidature1).isNotEqualTo(candidature2);

        candidature2.setId(candidature1.getId());
        assertThat(candidature1).isEqualTo(candidature2);

        candidature2 = getCandidatureSample2();
        assertThat(candidature1).isNotEqualTo(candidature2);
    }

    @Test
    void candidatTest() {
        Candidature candidature = getCandidatureRandomSampleGenerator();
        Utilisateur utilisateurBack = getUtilisateurRandomSampleGenerator();

        candidature.setCandidat(utilisateurBack);
        assertThat(candidature.getCandidat()).isEqualTo(utilisateurBack);

        candidature.candidat(null);
        assertThat(candidature.getCandidat()).isNull();
    }

    @Test
    void offreEmploiTest() {
        Candidature candidature = getCandidatureRandomSampleGenerator();
        OffreEmploi offreEmploiBack = getOffreEmploiRandomSampleGenerator();

        candidature.setOffreEmploi(offreEmploiBack);
        assertThat(candidature.getOffreEmploi()).isEqualTo(offreEmploiBack);

        candidature.offreEmploi(null);
        assertThat(candidature.getOffreEmploi()).isNull();
    }
}
