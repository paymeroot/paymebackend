package com.payme.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OperatorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Operator getOperatorSample1() {
        return new Operator().id(1L).nom("nom1").code("code1").countryCode("countryCode1").logoUrl("logoUrl1");
    }

    public static Operator getOperatorSample2() {
        return new Operator().id(2L).nom("nom2").code("code2").countryCode("countryCode2").logoUrl("logoUrl2");
    }

    public static Operator getOperatorRandomSampleGenerator() {
        return new Operator()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .countryCode(UUID.randomUUID().toString())
            .logoUrl(UUID.randomUUID().toString());
    }
}
