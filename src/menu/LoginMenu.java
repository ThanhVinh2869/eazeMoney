package menu;

import login.Account;
import login.AccountsManager;
import util.InputValidator;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu for logging in and registering.
 */
public class LoginMenu extends Menu {
    private final AccountsManager accountsManager;

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    private Account currentAccount;

    /**
     * @param scanner         the shared Scanner from App (only one Scanner may read System.in)
     * @param accountsManager loads, checks and saves accounts
     */
    public LoginMenu(Scanner scanner, AccountsManager accountsManager) {
        super(scanner);
        this.accountsManager = accountsManager;
    }

    @Override
    protected String getTitle() {
        return "Welcome to Eaze Money";
    }

    @Override
    protected List<String> getOptions() {
        return List.of("Log in", "Register");
    }

    @Override
    protected void handleOption(int option) {
        switch (option) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegister();
                break;
        }
    }

    @Override
    public void runMenu() {
        currentAccount = null;
        super.runMenu();
    }

    @Override
    protected boolean shouldExit() {
        return currentAccount != null;
    }

    /**
     * Asks for username and password and checks them.
     *
     */
    private void handleLogin() {
        System.out.println("\n--- Log in ---");
        String username = reader.readString("Username: ");
        String password = reader.readString("Password: ");

        Account account = accountsManager.authenticate(username, password);
        if (account == null) {
            // Same message for unknown username and wrong password, so nobody can guess which usernames exist
            System.out.println("Incorrect username or password.");
            return;
        }

        System.out.println("Welcome back, " + account.getDisplayName() + "!");
        this.setCurrentAccount(account);
    }

    /**
     * Asks for the new account details one field at a time, re-asking a field until it is valid.
     * Type 0 as the username to go back to the menu.
     *
     */
    private void handleRegister() {
        System.out.println("\n--- Register --- (type 0 as username to go back)");

        // 1. Username: valid format and not taken yet
        String username;
        while (true) {
            username = reader.readString("Username: ");
            if (username == null || username.equals("0")) {
                return;
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
            password = reader.readString("Password: ");
            String confirmedPassword = reader.readString("Confirm password: ");
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
            displayName = reader.readString("Display name: ");
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
            setCurrentAccount(account);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not save your account: " + e.getMessage());
        }
    }
}
