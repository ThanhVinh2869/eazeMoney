import budgets.BudgetMenu;
import dashboard.DashboardMenu;
import login.LoginMenu;
import transactions.TransactionMenu;

import java.util.Scanner;

/**
 * CLI entry point and top-level menu navigation loop.
 */
public class App {
    private final Scanner scanner = new Scanner(System.in);
    private Class<?> activeMenu = LoginMenu.class;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        System.out.println("==================================================");
        System.out.println("                 [Eaze Money]");
        System.out.println("==================================================");

        while (activeMenu != null) {
            if (activeMenu == LoginMenu.class) {
                showLoginMenu();
            } else if (activeMenu == DashboardMenu.class) {
                showDashboardMenu();
            } else if (activeMenu == TransactionMenu.class) {
                showTransactionMenu();
            } else if (activeMenu == BudgetMenu.class) {
                showBudgetMenu();
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private void showLoginMenu() {
        System.out.println("\nLoginMenu");
        System.out.println("1. Log in");
        System.out.println("2. Register");
        System.out.println("0. Exit");

        switch (readChoice()) {
            case 1:
            case 2:
                activeMenu = DashboardMenu.class;
                break;
            case 0:
                activeMenu = null;
                break;
            default:
                System.out.println("Please choose a valid option.");
        }
    }

    private void showDashboardMenu() {
        System.out.println("\nDashboardMenu");
        System.out.println("1. Transactions");
        System.out.println("2. Budgets");
        System.out.println("3. Log out");
        System.out.println("0. Exit");

        switch (readChoice()) {
            case 1:
                activeMenu = TransactionMenu.class;
                break;
            case 2:
                activeMenu = BudgetMenu.class;
                break;
            case 3:
                activeMenu = LoginMenu.class;
                break;
            case 0:
                activeMenu = null;
                break;
            default:
                System.out.println("Please choose a valid option.");
        }
    }

    private void showTransactionMenu() {
        System.out.println("\nTransactionMenu");
        System.out.println("0. Back to dashboard");

        if (readChoice() == 0) {
            activeMenu = DashboardMenu.class;
        } else {
            System.out.println("Please choose a valid option.");
        }
    }

    private void showBudgetMenu() {
        System.out.println("\nBudgetMenu");
        System.out.println("0. Back to dashboard");

        if (readChoice() == 0) {
            activeMenu = DashboardMenu.class;
        } else {
            System.out.println("Please choose a valid option.");
        }
    }

    private int readChoice() {
        if (!scanner.hasNextLine()) {
            activeMenu = null;
            return -1;
        }

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
