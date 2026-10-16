package login;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Turns passwords into salted SHA-256 hashes so the real password is never stored.
 */
public final class PasswordHasher {

    private static final int SALT_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    /**
     * Creates a new random salt.
     * @return 16 random bytes as a 32-character lowercase hex string
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    /**
     * Hashes a password together with its salt (salt first, then password).
     * @param password - the plain password the user typed
     * @param salt - the salt stored for this account
     * @return the SHA-256 hash as a 64-character lowercase hex string
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e); // every Java version has SHA-256
        }
    }

    /**
     * Checks whether a typed password matches a stored hash.
     * @param password - the plain password the user typed
     * @param salt - the salt stored for this account
     * @param expectedHash - the hash stored for this account
     * @return true if the password is correct
     */
    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        return hashPassword(password, salt).equals(expectedHash);
    }
}
