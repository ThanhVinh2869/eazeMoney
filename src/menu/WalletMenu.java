package menu;

import login.Account;
import wallet.Wallet;
import wallet.WalletList;

import java.util.Locale;
import java.util.Scanner;
import java.util.List;

/** CRUD menu for the logged-in user's wallets. Start it with: new WalletMenu(scanner, account).run(); */
public class WalletMenu extends Menu {

    private final WalletList wallets;

    public WalletMenu(Scanner scanner, Account account) {
        super(scanner);
        this.wallets = account.getWalletList();
    }

    @Override
    protected String getTitle() {
        return "Wallets";
    }

    @Override
    protected List<String> getOptions() {
        return List.of("View wallets", "Add wallet", "Edit wallet", "Delete wallet");
    }

    @Override
    protected void handleOption(int choice) {
        switch (choice) {
            case 1:
                handleViewWallets();
                break;
            case 2:
                handleAddWallet();
                break;
            case 3:
                handleEditWallet();
                break;
            case 4:
                handleDeleteWallet();
                break;
        }
    }

    private void handleViewWallets() {
        List<Wallet> walletEntries = wallets.getWallets();
        System.out.println("\n===== YOUR WALLETS =====");
        if (walletEntries.isEmpty()) {
            System.out.println("No wallets yet.");
            return;
        }

        System.out.printf("%-38s %-24s %15s%n", "ID", "NAME", "STARTING BALANCE");
        System.out.println("----------------------------------------------------------------------------");
        for (Wallet wallet : walletEntries) {
            System.out.printf("%-38s %-24.24s %15.2f%n",
                    wallet.getWalletId(), wallet.getWalletName(), wallet.getStartingBalance());
        }
    }

    private void handleAddWallet() {
        System.out.println("\n--- Add wallet ---");
        String name = promptRequired("Wallet name: ");
        if (name == null) {
            return;
        }

        Double startingBalance = promptBalance("Starting balance: ");
        if (startingBalance == null) {
            return;
        }

        Wallet wallet = new Wallet(name, startingBalance);
        wallets.addWallet(wallet);
        System.out.println("Wallet " + wallet.getWalletId() + " added.");
    }

    private void handleEditWallet() {
        if (wallets.getWallets().isEmpty()) {
            System.out.println("No wallets to edit.");
            return;
        }

        handleViewWallets();
        String walletId = promptRequired("Wallet ID to edit: ");
        if (walletId == null) {
            return;
        }
        Wallet existing = wallets.findWalletById(walletId);
        if (existing == null) {
            System.out.println("No wallet with ID " + walletId + ".");
            return;
        }

        System.out.println("Press Enter to keep a value.");
        String name = promptOptional("Wallet name [" + existing.getWalletName() + "]: ",
                existing.getWalletName());
        if (name == null) {
            return;
        }
        Double balance = promptOptionalBalance(
                "Starting balance [" + String.format(Locale.US, "%.2f", existing.getStartingBalance()) + "]: ",
                existing.getStartingBalance());
        if (balance == null) {
            return;
        }

        Wallet updated = new Wallet(existing.getWalletId(), name, balance);
        if (wallets.updateWallet(updated)) {
            System.out.println("Wallet " + walletId + " updated.");
        } else {
            System.out.println("Wallet " + walletId + " could not be updated.");
        }
    }

    private void handleDeleteWallet() {
        if (wallets.getWallets().isEmpty()) {
            System.out.println("No wallets to delete.");
            return;
        }

        handleViewWallets();
        String walletId = promptRequired("Wallet ID to delete: ");
        if (walletId == null) {
            return;
        }
        Wallet wallet = wallets.findWalletById(walletId);
        if (wallet == null) {
            System.out.println("No wallet with ID " + walletId + ".");
            return;
        }

        if (!scanner.hasNextLine()) {
            return;
        }
        if (!reader.confirm("Delete wallet \"" + wallet.getWalletName() + "\"?")) {
            System.out.println("Wallet not deleted.");
            return;
        }

        if (wallets.deleteWallet(walletId)) {
            System.out.println("Wallet " + walletId + " deleted.");
        } else {
            System.out.println("Wallet " + walletId + " could not be deleted.");
        }
    }

    private String promptRequired(String message) {
        while (true) {
            String input = reader.readString(message);
            if (input == null) {
                return null;
            }
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field is required.");
        }
    }

    private String promptOptional(String message, String currentValue) {
        String input = reader.readString(message);
        return input == null ? null : input.isEmpty() ? currentValue : input;
    }

    private Double promptBalance(String message) {
        while (true) {
            String input = promptRequired(message);
            if (input == null) {
                return null;
            }
            Double balance = parseBalance(input);
            if (balance != null) {
                return balance;
            }
            System.out.println("Enter a valid, non-negative balance.");
        }
    }

    private Double promptOptionalBalance(String message, double currentValue) {
        while (true) {
            String input = reader.readString(message);
            if (input == null) {
                return null;
            }
            if (input.isEmpty()) {
                return currentValue;
            }
            Double balance = parseBalance(input);
            if (balance != null) {
                return balance;
            }
            System.out.println("Enter a valid, non-negative balance or press Enter to keep the current value.");
        }
    }

    private Double parseBalance(String input) {
        try {
            double balance = Double.parseDouble(input);
            return Double.isFinite(balance) && balance >= 0 ? balance : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}