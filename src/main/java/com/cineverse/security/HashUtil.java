package com.cineverse.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utilitário simples de hash de senha usando SHA-256.
 *
 * Observação didática: em um projeto real de produção o ideal é usar
 * BCrypt/Argon2 (com "salt" por senha), disponíveis via Spring Security.
 * Aqui usamos SHA-256 puro para manter o projeto simples e sem
 * dependências extras, já que o objetivo é didático.
 */
public final class HashUtil {

    private HashUtil() {
    }

    public static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }
}
