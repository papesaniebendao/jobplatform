package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.domain.Utilisateur;
import com.mycompany.myapp.service.dto.CandidatureDTO;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.dto.UtilisateurDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Candidature} and its DTO {@link CandidatureDTO}.
 */
@Mapper(componentModel = "spring")
public interface CandidatureMapper extends EntityMapper<CandidatureDTO, Candidature> {
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "utilisateurId")
    @Mapping(target = "offreEmploi", source = "offreEmploi", qualifiedByName = "offreEmploiId")
    CandidatureDTO toDto(Candidature s);

    @Named("utilisateurId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UtilisateurDTO toDtoUtilisateurId(Utilisateur utilisateur);

    @Named("offreEmploiId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OffreEmploiDTO toDtoOffreEmploiId(OffreEmploi offreEmploi);
}
