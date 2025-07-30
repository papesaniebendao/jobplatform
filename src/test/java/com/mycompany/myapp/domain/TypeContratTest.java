package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TypeContratTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TypeContratTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TypeContrat.class);
        TypeContrat typeContrat1 = getTypeContratSample1();
        TypeContrat typeContrat2 = new TypeContrat();
        assertThat(typeContrat1).isNotEqualTo(typeContrat2);

        typeContrat2.setId(typeContrat1.getId());
        assertThat(typeContrat1).isEqualTo(typeContrat2);

        typeContrat2 = getTypeContratSample2();
        assertThat(typeContrat1).isNotEqualTo(typeContrat2);
    }
}
