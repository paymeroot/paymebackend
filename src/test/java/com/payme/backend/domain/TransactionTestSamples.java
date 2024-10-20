package com.payme.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Transaction getTransactionSample1() {
        return new Transaction()
            .id(1L)
            .reference("reference1")
            .senderNumber("senderNumber1")
            .senderWallet("senderWallet1")
            .receiverNumber("receiverNumber1")
            .receiverWallet("receiverWallet1")
            .object("object1")
            .payInFailureReason("payInFailureReason1")
            .payOutFailureReason("payOutFailureReason1")
            .senderCountryName("senderCountryName1")
            .receiverCountryName("receiverCountryName1");
    }

    public static Transaction getTransactionSample2() {
        return new Transaction()
            .id(2L)
            .reference("reference2")
            .senderNumber("senderNumber2")
            .senderWallet("senderWallet2")
            .receiverNumber("receiverNumber2")
            .receiverWallet("receiverWallet2")
            .object("object2")
            .payInFailureReason("payInFailureReason2")
            .payOutFailureReason("payOutFailureReason2")
            .senderCountryName("senderCountryName2")
            .receiverCountryName("receiverCountryName2");
    }

    public static Transaction getTransactionRandomSampleGenerator() {
        return new Transaction()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .senderNumber(UUID.randomUUID().toString())
            .senderWallet(UUID.randomUUID().toString())
            .receiverNumber(UUID.randomUUID().toString())
            .receiverWallet(UUID.randomUUID().toString())
            .object(UUID.randomUUID().toString())
            .payInFailureReason(UUID.randomUUID().toString())
            .payOutFailureReason(UUID.randomUUID().toString())
            .senderCountryName(UUID.randomUUID().toString())
            .receiverCountryName(UUID.randomUUID().toString());
    }
}
