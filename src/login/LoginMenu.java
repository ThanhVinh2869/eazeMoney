package login;

import java.io.IOException;
import java.util.Scanner;

/**
 * Console menu for logging in and registering.
 */
public class LoginMenu {

    private final Scanner scanner;
    private final AccountsManager accountsManager;

    /**
     * @param scanner         the shared Scanner from App (only one Scanner may read System.in)
     * @param accountsManager loads, checks and saves accounts
     */
    public LoginMenu(Scanner scanner, AccountsManager accountsManager) {
        this.scanner = scanner;
        this.accountsManager = accountsManager;
    }

    /**
     * Shows the login menu until the user logs in, registers, or exits.
     * @return the signed-in Account, or null if the user chose to exit
     */
    public Account show() {
        while (true) {
            System.out.println("\n--- Welcome to Eaze Money ---");
            System.out.println("1. Log in");
            System.out.println("2. Register");
            System.out.println("0. Exit");

            String choice = prompt("Choose: ");
            if (choice == null) {
                return null; // no more input (e.g. the input stream was closed)
            }

            switch (choice) {
                case "1":
                    Account loggedIn = handleLogin();
                    if (loggedIn != null) {
                        return loggedIn;
                    }
                    break;
                case "2":
                    Account registered = handleRegister();
                    if (registered != null) {
                        return registered;
                    }
                    break;
                case "0":
                    return null;
                default:
                    System.out.println("Please choose a valid option.");
            }
        }
    }

    /**
     * Asks for username and password and checks them.
     *
     * @return the Account if the credentials are correct, otherwise null
     */
    private Account handleLogin() {
        System.out.println("\n--- Log in ---");
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        Account account = accountsManager.authenticate(username, password);
        if (account == null) {
            // Same message for unknown username and wrong password, so nobody can guess which usernames exist
            System.out.println("Incorrect username or password.");
            return null;
        }

        System.out.println("Welcome back, " + account.getDisplayName() + "!");
        return account;
    }

    /**
     * Asks for the new account details one field at a time, re-asking a field until it is valid.
     * Type 0 as the username to go back to the menu.
     *
     * @return the new Account, or null if the user went back or saving failed
     */
    private Account handleRegister() {
        System.out.println("\n--- Register --- (type 0 as username to go back)");

        // 1. Username: valid format and not taken yet
        String username;
        while (true) {
            username = prompt("Username: ");
            if (username == null || username.equals("0")) {
                return null;
            }
            try {
                InputValidator.checkUsername(username);
                if (accountsManager.isAccountExist(username)) {
                    throw new IllegalArgumentException("This username is already taken.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // 2. Password + confirm password (checked together by InputValidator)
        String password;
        while (true) {
            password = prompt("Password: ");
            String confirmedPassword = prompt("Confirm password: ");
            try {
                InputValidator.checkPassword(password, confirmedPassword);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // 3. Display name
        String displayName;
        while (true) {
            displayName = prompt("Display name: ");
            try {
                InputValidator.checkDisplayName(displayName);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // 4. Save. registerAccount checks everything again, so this is the final safety net.
        try {
            Account account = accountsManager.registerAccount(username, password, password, displayName);
            System.out.println("Account created! Welcome, " + account.getDisplayName() + "!");
            return account;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not save your account: " + e.getMessage());
        }
        return null;
    }

    /**
     * Prints a label and reads one line from the user.
     *
     * @param label the question to show, e.g. "Username: "
     * @return the trimmed line, or null if there is no more input
     */
    private String prompt(String label) {
        System.out.print(label);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }
}
