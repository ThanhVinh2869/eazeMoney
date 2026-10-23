package menu;

import budgets.Budget;
import budgets.BudgetList;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;

/**
 * CLI menu for a user's budgets: add, list, update and delete.
 *
 * Usage from the main loop:
 *     new BudgetMenu(userId, scanner).run();
 * run() returns when the user chooses "Back".
 */
public class BudgetMenu extends Menu {

    private final BudgetList budgetList;

    /**
     * Loads data/&lt;userId&gt;/budgets.csv. Pass the App's existing Scanner so System.in
     * isn't wrapped twice.
     */
    public BudgetMenu(String userId, Scanner scanner) throws IOException {
        super(scanner);
        this.budgetList = new BudgetList(userId);
        this.budgetList.load();
    }

    public BudgetList getBudgetList() {
        return budgetList;
    }

    @Override
    protected String getTitle() {
        return "BUDGETS";
    }

    @Override
    protected List<String> getOptions() {
        return List.of("Add budget", "List budgets", "Update budget", "Delete budget");
    }

    @Override
    protected void handleOption(int option) {
        try {
            switch (option) {
                case 1:
                    handleAddBudget();
                    break;
                case 2:
                    handleListBudgets();
                    break;
                case 3:
                    handleUpdateBudget();
                    break;
                case 4:
                    handleDeleteBudget();
                    break;
                default:
                    System.out.println("Please choose a valid option.");
            }
        } catch (IOException e) {
            System.out.println("Could not read or save budget data: " + e.getMessage());
        } catch (InputCancelled e) {
            System.out.println("Cancelled.");
        }
    }

    // ------------------------------------------------------------------ actions

    private void handleAddBudget() throws IOException {
        System.out.println("\n--- Add budget (type 'cancel' at any prompt to go back) ---");

        String category = promptRequired("Category (e.g. FOOD): ");
        if (budgetList.categoryAlreadyBudgeted(category)) {
            Budget existing = budgetList.findByCategory(category);
            System.out.println("A budget for " + Budget.normalize(category) + " already exists ("
                    + existing.getBudgetId() + " - " + existing.getBudgetName()
                    + "). Update that one instead.");
            return;
        }

        String name = promptRequired("Budget name: ");
        BigDecimal amount = promptAmount("Limit amount: ", null);
        String period = promptPeriod("Period " + periodsHint() + ": ", null);

        Budget budget = new Budget(null, name, amount, period, category);
        if (budgetList.create(budget)) {
            budgetList.save();
            System.out.println("Budget " + budget.getBudgetId() + " added.");
        } else {
            System.out.println("A budget for " + budget.getCategory() + " already exists. Budget not added.");
        }
    }

    private void handleListBudgets() throws IOException {
        List<Budget> budgets = budgetList.getBudgets();

        System.out.println("\n===== YOUR BUDGETS =====");
        if (budgets.isEmpty()) {
            System.out.println("No budgets yet.");
            return;
        }

        System.out.printf("%-8s %-22s %-16s %-8s %10s %10s %6s  %s%n",
                "ID", "NAME", "CATEGORY", "PERIOD", "LIMIT", "SPENT", "USED", "STATUS");
        System.out.println("-----------------------------------------------------------------------------------------------");

        for (Budget b : budgets) {
            BigDecimal spent = budgetList.getSpent(b);
            String used = b.getBudgetAmount().signum() == 0 ? "-"
                    : spent.multiply(BigDecimal.valueOf(100))
                    .divide(b.getBudgetAmount(), 0, RoundingMode.HALF_UP) + "%";
            String status = budgetList.isExceeded(b) ? "EXCEEDED" : "OK";

            System.out.printf("%-8s %-22.22s %-16.16s %-8s %10s %10s %6s  %s%n",
                    b.getBudgetId(), b.getBudgetName(), b.getCategory(), b.getBudgetPeriod(),
                    b.getBudgetAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    spent.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    used, status);
        }
    }

    private void handleUpdateBudget() throws IOException {
        Budget existing = promptExistingBudget("Budget ID to update: ");
        if (existing == null) {
            return;
        }

        System.out.println("Editing " + existing + "  (press Enter to keep a value, 'cancel' to go back)");

        String name = promptOptional("Name [" + existing.getBudgetName() + "]: ", existing.getBudgetName());

        String category = existing.getCategory();
        while (true) {
            String input = promptOptional("Category [" + existing.getCategory() + "]: ", existing.getCategory());
            Budget owner = budgetList.findByCategory(input);
            if (owner == null || owner == existing) {
                category = input;
                break;
            }
            System.out.println("Budget " + owner.getBudgetId() + " already covers "
                    + Budget.normalize(input) + ". Choose another category.");
        }

        BigDecimal amount = promptAmount("Limit amount [" + existing.getBudgetAmount().toPlainString() + "]: ",
                existing.getBudgetAmount());
        String period = promptPeriod("Period " + periodsHint() + " [" + existing.getBudgetPeriod() + "]: ",
                existing.getBudgetPeriod());

        Budget updated = new Budget(existing.getBudgetId(), name, amount, period, category);
        if (budgetList.update(existing.getBudgetId(), updated)) {
            budgetList.save();
            System.out.println("Budget " + updated.getBudgetId() + " updated.");
        } else {
            System.out.println("Budget could not be updated.");
        }
    }

    private void handleDeleteBudget() throws IOException {
        Budget existing = promptExistingBudget("Budget ID to delete: ");
        if (existing == null) {
            return;
        }

        String confirm = promptRequired("Delete " + existing + "? (y/n): ");
        if (!confirm.equalsIgnoreCase("y") && !confirm.equalsIgnoreCase("yes")) {
            System.out.println("Not deleted.");
            return;
        }

        if (budgetList.delete(existing.getBudgetId())) {
            budgetList.save();
            System.out.println("Budget " + existing.getBudgetId() + " deleted.");
        }
    }

    // ------------------------------------------------------------------ input helpers

    /** Thrown when the user types "cancel" or input runs out mid-action. */
    private static class InputCancelled extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /** Reads one trimmed line, or null at end of input. */
    private String prompt(String message) {
        System.out.print(message);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    private String promptRequired(String message) {
        while (true) {
            String input = prompt(message);
            if (input == null || input.equalsIgnoreCase("cancel")) {
                throw new InputCancelled();
            }
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field is required.");
        }
    }

    /** Blank input keeps currentValue. */
    private String promptOptional(String message, String currentValue) {
        String input = prompt(message);
        if (input == null || input.equalsIgnoreCase("cancel")) {
            throw new InputCancelled();
        }
        return input.isEmpty() ? currentValue : input;
    }

    /** Positive amount with up to 2 decimals. Blank keeps currentValue when one is given. */
    private BigDecimal promptAmount(String message, BigDecimal currentValue) {
        while (true) {
            String input = currentValue == null ? promptRequired(message)
                    : promptOptional(message, currentValue.toPlainString());
            try {
                BigDecimal amount = new BigDecimal(input.replace("$", "").replace(",", ""));
                if (amount.signum() <= 0) {
                    System.out.println("Amount must be greater than 0.");
                } else if (amount.scale() > 2) {
                    System.out.println("Use at most 2 decimal places.");
                } else {
                    return amount.setScale(2, RoundingMode.UNNECESSARY);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number, e.g. 250.00");
            }
        }
    }

    /** One of Budget.VALID_PERIODS (first letter is enough). Blank keeps currentValue when given. */
    private String promptPeriod(String message, String currentValue) {
        while (true) {
            String input = currentValue == null ? promptRequired(message)
                    : promptOptional(message, currentValue);
            String normalized = Budget.normalize(input);
            for (String valid : Budget.VALID_PERIODS) {
                if (valid.equals(normalized) || valid.startsWith(normalized) && normalized.length() == 1) {
                    return valid;
                }
            }
            System.out.println("Please enter one of " + periodsHint() + ".");
        }
    }

    private Budget promptExistingBudget(String message) throws IOException {
        if (budgetList.getBudgets().isEmpty()) {
            System.out.println("No budgets yet.");
            return null;
        }
        handleListBudgets();
        String id = promptRequired(message);
        Budget budget = budgetList.findById(id);
        if (budget == null) {
            System.out.println("No budget with ID " + id + ".");
        }
        return budget;
    }

    private static String periodsHint() {
        return "(" + String.join("/", Budget.VALID_PERIODS) + ")";
    }
}
