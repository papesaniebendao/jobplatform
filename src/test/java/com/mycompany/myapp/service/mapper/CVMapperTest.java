package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.CVAsserts.*;
import static com.mycompany.myapp.domain.CVTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CVMapperTest {

    private CVMapper cVMapper;

    @BeforeEach
    void setUp() {
        cVMapper = new CVMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCVSample1();
        var actual = cVMapper.toEntity(cVMapper.toDto(expected));
        assertCVAllPropertiesEquals(expected, actual);
    }
}
