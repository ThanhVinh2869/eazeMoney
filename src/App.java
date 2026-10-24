import login.Account;
import login.AccountsManager;
import dashboard.DashboardServices;
import menu.BudgetMenu;
import menu.LoginMenu;
import menu.TransactionMenu;
import menu.WalletMenu;
import util.InputReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * CLI entry point and top-level menu navigation loop.
 */
public class App {
    private final Scanner scanner = new Scanner(System.in);
    private final Session session = new Session();
    private final InputReader reader = new InputReader(scanner);
    private boolean running = true;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        System.out.println("==================================================");
        System.out.println("                 [Eaze Money]");
        System.out.println("==================================================");

        try {
            LoginMenu loginMenu = new LoginMenu(scanner, new AccountsManager());
            while (running) {
                if (session.isLoggedIn()) {
                    showDashboardMenu();
                } else {
                    loginMenu.runMenu();
                    Account account = loginMenu.getCurrentAccount();
                    if (account == null) {
                        running = false;
                    } else {
                        session.login(account);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load accounts: " + e.getMessage());
        } finally {
            System.out.println("Goodbye!");
            scanner.close();
        }
    }

    private void showDashboardMenu() {
        System.out.println("\n--- Dashboard ---");
        System.out.println("1. Summary");
        System.out.println("2. Transactions");
        System.out.println("3. Budgets");
        System.out.println("4. Wallets");
        System.out.println("5. Log out");
        System.out.println("0. Exit");

        int choice = reader.readInt("Choose an option: ");

        switch (choice) {
            case 1:
                showSummary();
                break;
            case 2:
                showTransactionMenu();
                break;
            case 3:
                showBudgetMenu();
                break;
            case 4:
                showWalletMenu();
                break;
            case 5:
                session.logout();
                break;
            case 0:
                running = false;
                break;
        }
    }

    private void showSummary() {
        try {
            new DashboardServices(session.getUserID()).displaySummaries();
        } catch (IOException e) {
            System.out.println("Could not display dashboard summary: " + e.getMessage());
        }
    }

    private void showTransactionMenu() {
        try {
            TransactionMenu transactionMenu = new TransactionMenu(scanner, session.getUserID());
            transactionMenu.runMenu();
        } catch (IOException e) {
            System.out.println("Could not load transactions: " + e.getMessage());
        }
    }

    private void showBudgetMenu() {
        try {
            BudgetMenu budgetMenu = new BudgetMenu(session.getUserID(), scanner);
            budgetMenu.runMenu();
        } catch (IOException e) {
            System.out.println("Could not load budgets: " + e.getMessage());
        }
    }

    private void showWalletMenu() {
        Account account = session.getCurrentAccount();
        if (account == null) {
            System.out.println("Please log in to manage wallets.");
            return;
        }
        WalletMenu walletMenu = new WalletMenu(scanner, account);
        walletMenu.runMenu();
    }
}
