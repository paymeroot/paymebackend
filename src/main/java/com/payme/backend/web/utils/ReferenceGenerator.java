package com.payme.backend.web.utils;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class ReferenceGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Set<String> generatedNumbers = new HashSet<>();
    private static final SecureRandom random = new SecureRandom();
    private static final int NUM_LENGTH = 6;

    // Méthode pour générer une référence avec un préfixe
    public static String generateReference(String prefix) {
        StringBuilder reference = new StringBuilder(prefix);
        for (int i = 0; i < LENGTH; i++) {
            reference.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return reference.toString();
    }

    // Surcharge de la méthode pour générer une référence sans préfixe
    public static String generateReference() {
        return generateReference("");
    }

    public static String generateContractReference() {
        String uniqueString;
        do {
            String randomNum = generateRandomNumeric();
            uniqueString = "CA" + randomNum + "C";
        } while (generatedNumbers.contains(uniqueString)); // Ensure uniqueness

        generatedNumbers.add(uniqueString); // Track generated strings
        return uniqueString;
    }

    // Generate a random numeric string of the specified length
    private static String generateRandomNumeric() {
        StringBuilder numBuilder = new StringBuilder(ReferenceGenerator.NUM_LENGTH);
        for (int i = 0; i < ReferenceGenerator.NUM_LENGTH; i++) {
            int digit = random.nextInt(10); // Generate a random digit (0-9)
            numBuilder.append(digit);
        }
        return numBuilder.toString();
    }
}
