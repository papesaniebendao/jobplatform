package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.service.dto.TypeContratDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TypeContrat} and its DTO {@link TypeContratDTO}.
 */
@Mapper(componentModel = "spring")
public interface TypeContratMapper extends EntityMapper<TypeContratDTO, TypeContrat> {}
