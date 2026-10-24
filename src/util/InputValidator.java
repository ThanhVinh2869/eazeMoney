package util;

/**
 *
 */
public final class InputValidator {

    /**
     *
     */
    private InputValidator() {
    }

    /**
     *
     * @param username
     */
    public static void checkUsername(String username) {
        if(username == null || username.isBlank())
            throw new IllegalArgumentException("Username is required.");

        if(username.length() <3 || username.length() > 20)
            throw new IllegalArgumentException("Username must be between 3 and 30 characters.");

        if(!username.matches("[A-Za-z0-9]+"))
            throw new IllegalArgumentException("Username must be in letters or numbers.");
    }

    /**
     *
     * @param password
     * @param confirmedPassword
     */
    public static void checkPassword(String password, String confirmedPassword) {
        if(password == null || password.isBlank())
            throw new IllegalArgumentException("Password is required.");

        if(password.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters.");

        if(password.contains(" "))
            throw new IllegalArgumentException("Password cannot contain spaces.");

        if(!password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*"))
            throw new IllegalArgumentException("Password must contain a letter and a number.");

        if(!password.equals(confirmedPassword))
            throw new IllegalArgumentException("Passwords do not match.");
    }
    /**
     *
     * @param displayName
     */
    public static void checkDisplayName(String displayName) {
        if(displayName == null || displayName.isBlank())
            throw new IllegalArgumentException("Name is required.");

        String name = displayName.trim();
        if(name.length() > 30)
            throw new IllegalArgumentException("Display name is too long.");

        if(!name.matches("[A-Za-z.'-]+")) {
            throw new IllegalArgumentException("Display name can only contains letters, periods, apostrophes, hyphens");
        }
    }
}
