package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CandidatureTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Candidature getCandidatureSample1() {
        return new Candidature().id(1L);
    }

    public static Candidature getCandidatureSample2() {
        return new Candidature().id(2L);
    }

    public static Candidature getCandidatureRandomSampleGenerator() {
        return new Candidature().id(longCount.incrementAndGet());
    }
}
