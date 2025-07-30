package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CandidatureDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CandidatureDTO.class);
        CandidatureDTO candidatureDTO1 = new CandidatureDTO();
        candidatureDTO1.setId(1L);
        CandidatureDTO candidatureDTO2 = new CandidatureDTO();
        assertThat(candidatureDTO1).isNotEqualTo(candidatureDTO2);
        candidatureDTO2.setId(candidatureDTO1.getId());
        assertThat(candidatureDTO1).isEqualTo(candidatureDTO2);
        candidatureDTO2.setId(2L);
        assertThat(candidatureDTO1).isNotEqualTo(candidatureDTO2);
        candidatureDTO1.setId(null);
        assertThat(candidatureDTO1).isNotEqualTo(candidatureDTO2);
    }
}
