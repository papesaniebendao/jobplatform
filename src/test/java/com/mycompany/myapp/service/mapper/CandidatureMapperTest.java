package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.CandidatureAsserts.*;
import static com.mycompany.myapp.domain.CandidatureTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CandidatureMapperTest {

    private CandidatureMapper candidatureMapper;

    @BeforeEach
    void setUp() {
        candidatureMapper = new CandidatureMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCandidatureSample1();
        var actual = candidatureMapper.toEntity(candidatureMapper.toDto(expected));
        assertCandidatureAllPropertiesEquals(expected, actual);
    }
}
