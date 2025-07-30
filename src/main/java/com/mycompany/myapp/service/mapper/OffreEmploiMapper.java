package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.domain.Utilisateur;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.dto.TypeContratDTO;
import com.mycompany.myapp.service.dto.UtilisateurDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OffreEmploi} and its DTO {@link OffreEmploiDTO}.
 */
@Mapper(componentModel = "spring")
public interface OffreEmploiMapper extends EntityMapper<OffreEmploiDTO, OffreEmploi> {
    @Mapping(target = "typeContrat", source = "typeContrat", qualifiedByName = "typeContratId")
    @Mapping(target = "recruteur", source = "recruteur", qualifiedByName = "utilisateurId")
    OffreEmploiDTO toDto(OffreEmploi s);

    @Named("typeContratId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TypeContratDTO toDtoTypeContratId(TypeContrat typeContrat);

    @Named("utilisateurId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UtilisateurDTO toDtoUtilisateurId(Utilisateur utilisateur);
}
