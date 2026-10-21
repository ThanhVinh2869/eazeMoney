package budgets;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * A spending limit for exactly one transaction category over a period.
 */
public class Budget {

    /** Periods a budget can cover. Stored in budgets.csv as these exact strings. */
    public static final String[] VALID_PERIODS = {"WEEKLY", "MONTHLY", "YEARLY"};

    private String budgetId;
    private String budgetName;
    private BigDecimal budgetAmount;
    private String budgetPeriod;
    private String category;

    public Budget(String budgetId, String budgetName, BigDecimal budgetAmount,
                  String budgetPeriod, String category) {
        this.budgetId = budgetId;
        this.budgetName = budgetName;
        this.budgetAmount = budgetAmount;
        this.budgetPeriod = normalize(budgetPeriod);
        this.category = normalize(category);
    }

    /**
     * Categories and periods are stored upper-case so that "food", "Food" and "FOOD"
     * are treated as the same category everywhere.
     */
    public static String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isValidPeriod(String period) {
        String p = normalize(period);
        for (String valid : VALID_PERIODS) {
            if (valid.equals(p)) {
                return true;
            }
        }
        return false;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public String getBudgetPeriod() {
        return budgetPeriod;
    }

    public void setBudgetPeriod(String budgetPeriod) {
        this.budgetPeriod = normalize(budgetPeriod);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = normalize(category);
    }

    @Override
    public String toString() {
        return budgetId + " | " + budgetName + " | " + category + " | "
                + budgetPeriod + " | " + budgetAmount.toPlainString();
    }
}
