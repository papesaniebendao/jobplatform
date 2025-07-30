package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OffreEmploiDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OffreEmploiDTO.class);
        OffreEmploiDTO offreEmploiDTO1 = new OffreEmploiDTO();
        offreEmploiDTO1.setId(1L);
        OffreEmploiDTO offreEmploiDTO2 = new OffreEmploiDTO();
        assertThat(offreEmploiDTO1).isNotEqualTo(offreEmploiDTO2);
        offreEmploiDTO2.setId(offreEmploiDTO1.getId());
        assertThat(offreEmploiDTO1).isEqualTo(offreEmploiDTO2);
        offreEmploiDTO2.setId(2L);
        assertThat(offreEmploiDTO1).isNotEqualTo(offreEmploiDTO2);
        offreEmploiDTO1.setId(null);
        assertThat(offreEmploiDTO1).isNotEqualTo(offreEmploiDTO2);
    }
}
