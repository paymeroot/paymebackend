package com.payme.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KycTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Kyc getKycSample1() {
        return new Kyc()
            .id(1L)
            .reference("reference1")
            .typePiece("typePiece1")
            .numberPiece("numberPiece1")
            .photoPieceUrl("photoPieceUrl1")
            .photoSelfieUrl("photoSelfieUrl1");
    }

    public static Kyc getKycSample2() {
        return new Kyc()
            .id(2L)
            .reference("reference2")
            .typePiece("typePiece2")
            .numberPiece("numberPiece2")
            .photoPieceUrl("photoPieceUrl2")
            .photoSelfieUrl("photoSelfieUrl2");
    }

    public static Kyc getKycRandomSampleGenerator() {
        return new Kyc()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .typePiece(UUID.randomUUID().toString())
            .numberPiece(UUID.randomUUID().toString())
            .photoPieceUrl(UUID.randomUUID().toString())
            .photoSelfieUrl(UUID.randomUUID().toString());
    }
}
