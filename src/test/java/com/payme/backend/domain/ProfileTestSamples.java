package com.payme.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Profile getProfileSample1() {
        return new Profile().id(1L).email("email1").address("address1").ville("ville1").countryCode("countryCode1").avatarUrl("avatarUrl1");
    }

    public static Profile getProfileSample2() {
        return new Profile().id(2L).email("email2").address("address2").ville("ville2").countryCode("countryCode2").avatarUrl("avatarUrl2");
    }

    public static Profile getProfileRandomSampleGenerator() {
        return new Profile()
            .id(longCount.incrementAndGet())
            .email(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .ville(UUID.randomUUID().toString())
            .countryCode(UUID.randomUUID().toString())
            .avatarUrl(UUID.randomUUID().toString());
    }
}
