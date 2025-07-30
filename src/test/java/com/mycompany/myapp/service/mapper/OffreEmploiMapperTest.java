package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.OffreEmploiAsserts.*;
import static com.mycompany.myapp.domain.OffreEmploiTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OffreEmploiMapperTest {

    private OffreEmploiMapper offreEmploiMapper;

    @BeforeEach
    void setUp() {
        offreEmploiMapper = new OffreEmploiMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOffreEmploiSample1();
        var actual = offreEmploiMapper.toEntity(offreEmploiMapper.toDto(expected));
        assertOffreEmploiAllPropertiesEquals(expected, actual);
    }
}
