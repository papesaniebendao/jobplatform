package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TypeContratAsserts.*;
import static com.mycompany.myapp.domain.TypeContratTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypeContratMapperTest {

    private TypeContratMapper typeContratMapper;

    @BeforeEach
    void setUp() {
        typeContratMapper = new TypeContratMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTypeContratSample1();
        var actual = typeContratMapper.toEntity(typeContratMapper.toDto(expected));
        assertTypeContratAllPropertiesEquals(expected, actual);
    }
}
