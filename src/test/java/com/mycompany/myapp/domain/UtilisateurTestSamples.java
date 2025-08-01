package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UtilisateurTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Utilisateur getUtilisateurSample1() {
        return new Utilisateur()
            .id(1L)
            .prenom("prenom1")
            .nom("nom1")
            .nomEntreprise("nomEntreprise1")
            .secteurActivite("secteurActivite1")
            .telephone("telephone1");
    }

    public static Utilisateur getUtilisateurSample2() {
        return new Utilisateur()
            .id(2L)
            .prenom("prenom2")
            .nom("nom2")
            .nomEntreprise("nomEntreprise2")
            .secteurActivite("secteurActivite2")
            .telephone("telephone2");
    }

    public static Utilisateur getUtilisateurRandomSampleGenerator() {
        return new Utilisateur()
            .id(longCount.incrementAndGet())
            .prenom(UUID.randomUUID().toString())
            .nom(UUID.randomUUID().toString())
            .nomEntreprise(UUID.randomUUID().toString())
            .secteurActivite(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString());
    }
}
