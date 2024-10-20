package com.payme.backend.web.utils;

public final class StringUtil {

    /**
     * Cette fonction prend l'Enum TypeContrat et le converti en code Correspondant à la valeur en base de données.
     * @param input
     * @return
     */
    public static String formatCodeTypeContract(String input) {
        // Sépare la chaîne en utilisant les underscores comme délimiteurs
        String[] parts = input.toLowerCase().split("_");

        // Conserve la première partie en majuscules si elle commence par 'E'
        StringBuilder result = new StringBuilder(parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1));

        // Pour chaque partie restante, on met la première lettre en majuscule et on concatène
        for (int i = 1; i < parts.length; i++) {
            result.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
        }

        return result.toString();
    }
}
