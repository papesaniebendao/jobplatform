package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TypeContratTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TypeContrat getTypeContratSample1() {
        return new TypeContrat().id(1L).nom("nom1");
    }

    public static TypeContrat getTypeContratSample2() {
        return new TypeContrat().id(2L).nom("nom2");
    }

    public static TypeContrat getTypeContratRandomSampleGenerator() {
        return new TypeContrat().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString());
    }
}
