package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.CV;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.Utilisateur;
import com.mycompany.myapp.service.dto.CVDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.dto.UtilisateurDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Utilisateur} and its DTO {@link UtilisateurDTO}.
 */
@Mapper(componentModel = "spring")
public interface UtilisateurMapper extends EntityMapper<UtilisateurDTO, Utilisateur> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "cv", source = "cv", qualifiedByName = "cVId")
    UtilisateurDTO toDto(Utilisateur s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("cVId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CVDTO toDtoCVId(CV cV);
}
