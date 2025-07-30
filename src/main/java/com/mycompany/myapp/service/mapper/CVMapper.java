package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.CV;
import com.mycompany.myapp.service.dto.CVDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CV} and its DTO {@link CVDTO}.
 */
@Mapper(componentModel = "spring")
public interface CVMapper extends EntityMapper<CVDTO, CV> {}
