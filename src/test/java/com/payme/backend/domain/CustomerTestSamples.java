package com.payme.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Customer getCustomerSample1() {
        return new Customer().id(1L).lastname("lastname1").firstname("firstname1").phone("phone1").countryCode("countryCode1");
    }

    public static Customer getCustomerSample2() {
        return new Customer().id(2L).lastname("lastname2").firstname("firstname2").phone("phone2").countryCode("countryCode2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(longCount.incrementAndGet())
            .lastname(UUID.randomUUID().toString())
            .firstname(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .countryCode(UUID.randomUUID().toString());
    }
}
