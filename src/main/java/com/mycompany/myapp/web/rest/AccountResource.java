package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.enumeration.RoleUtilisateur;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.repository.UtilisateurRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.MailService;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.AccountDetailsDTO;
import com.mycompany.myapp.service.dto.AdminUserDTO;
import com.mycompany.myapp.service.dto.PasswordChangeDTO;
import com.mycompany.myapp.web.rest.errors.*;
import com.mycompany.myapp.web.rest.vm.AccountUpdateVM;
import com.mycompany.myapp.web.rest.vm.KeyAndPasswordVM;
import com.mycompany.myapp.web.rest.vm.ManagedUserVM;
import jakarta.validation.Valid;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;



/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final UtilisateurRepository utilisateurRepository;

    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        MailService mailService,
        UtilisateurRepository utilisateurRepository
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.utilisateurRepository = utilisateurRepository; // ✅ OK maintenant
    }
        

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            return Mono.error(new InvalidPasswordException());
        }
    
        return userService.registerUser(managedUserVM, managedUserVM.getPassword())
            .doOnSuccess(mailService::sendActivationEmail)
            .then();
    }
    
    

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public Mono<Void> activateAccount(@RequestParam(value = "key") String key) {
        return userService
            .activateRegistration(key)
            .switchIfEmpty(Mono.error(new AccountResourceException("No user was found for this activation key")))
            .then();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public Mono<ResponseEntity<AccountDetailsDTO>> getAccount() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userService::getUserWithAuthoritiesByLogin)
            .flatMap(user -> utilisateurRepository.findByUser(user.getId())
                .next() // car findByUser retourne un Flux
                .map(utilisateur -> {
                    AccountDetailsDTO dto = new AccountDetailsDTO();
                    dto.setLogin(user.getLogin());
                    dto.setEmail(user.getEmail());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setPrenom(utilisateur.getPrenom());
                    dto.setNom(utilisateur.getNom());
                    dto.setNomEntreprise(utilisateur.getNomEntreprise());
                    dto.setSecteurActivite(utilisateur.getSecteurActivite());
                    dto.setTelephone(utilisateur.getTelephone());
                    dto.setRole(utilisateur.getRole());
                    return ResponseEntity.ok(dto);
                })
            );
    }


    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public Mono<Void> saveAccount(@Valid @RequestBody AccountUpdateVM updateVM) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userService::getUserWithAuthoritiesByLogin)
            .flatMap(user -> utilisateurRepository.findByUser(user.getId())
                .next()
                .flatMap(utilisateur -> {
                    // ---- Validation métier ----
                    if (updateVM.getRole() == RoleUtilisateur.CANDIDAT) {
                        if (updateVM.getNom() == null || updateVM.getNom().isBlank()) {
                            return Mono.error(new BadRequestAlertException("Nom obligatoire pour un candidat", "utilisateur", "nomrequired"));
                        }
                        if (updateVM.getPrenom() == null || updateVM.getPrenom().isBlank()) {
                            return Mono.error(new BadRequestAlertException("Prénom obligatoire pour un candidat", "utilisateur", "prenomrequired"));
                        }
                    }
                    if (updateVM.getRole() == RoleUtilisateur.RECRUTEUR) {
                        if (updateVM.getNomEntreprise() == null || updateVM.getNomEntreprise().isBlank()) {
                            return Mono.error(new BadRequestAlertException("Nom entreprise obligatoire pour un recruteur", "utilisateur", "nomentrepriserequired"));
                        }
                        if (updateVM.getSecteurActivite() == null || updateVM.getSecteurActivite().isBlank()) {
                            return Mono.error(new BadRequestAlertException("Secteur activité obligatoire pour un recruteur", "utilisateur", "secteuractiviterequired"));
                        }
                    }
    
                    // ---- Mise à jour de Utilisateur uniquement ----
                    utilisateur.setPrenom(updateVM.getPrenom());
                    utilisateur.setNom(updateVM.getNom());
                    utilisateur.setNomEntreprise(updateVM.getNomEntreprise());
                    utilisateur.setSecteurActivite(updateVM.getSecteurActivite());
                    utilisateur.setTelephone(updateVM.getTelephone());
                    utilisateur.setRole(updateVM.getRole());
    
                    return utilisateurRepository.save(utilisateur).then();
                })
            );
    }


    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public Mono<Void> changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        return userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public Mono<Void> requestPasswordReset(@RequestBody String mail) {
        return userService
            .requestPasswordReset(mail)
            .doOnSuccess(user -> {
                if (Objects.nonNull(user)) {
                    mailService.sendPasswordResetMail(user);
                } else {
                    // Pretend the request has been successful to prevent checking which emails really exist
                    // but log that an invalid attempt has been made
                    LOG.warn("Password reset requested for non existing mail");
                }
            })
            .then();
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public Mono<Void> finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        return userService
            .completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
            .switchIfEmpty(Mono.error(new AccountResourceException("No user was found for this reset key")))
            .then();
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
