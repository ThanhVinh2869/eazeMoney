package budgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds one user's budgets and reads/writes them from data/&lt;userId&gt;/budgets.csv.
 *
 * Rule: each budget covers exactly one category, and a user can have at most one
 * budget per category.
 */
public class BudgetList {

    private static final String HEADER = "budgetId,budgetName,budgetAmount,category,budgetPeriod";

    private final ArrayList<Budget> budgets = new ArrayList<>();
    private final Path budgetsFile;
    private final Path transactionsFile;

    /** Uses data/&lt;userId&gt;/budgets.csv and data/&lt;userId&gt;/transactions.csv. */
    public BudgetList(String userId) {
        this(Paths.get("data", userId, "budgets.csv"),
             Paths.get("data", userId, "transactions.csv"));
    }

    /** Explicit file locations (useful for tests). */
    public BudgetList(Path budgetsFile, Path transactionsFile) {
        this.budgetsFile = budgetsFile;
        this.transactionsFile = transactionsFile;
    }

    // ------------------------------------------------------------------ load / save

    /** Replaces the in-memory list with the contents of budgets.csv (empty if the file doesn't exist). */
    public void load() throws IOException {
        ArrayList<Budget> loaded = new ArrayList<>();

        if (Files.exists(budgetsFile)) {
            List<String> lines = Files.readAllLines(budgetsFile, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.isBlank() || line.startsWith("budgetId,")) {
                    continue;
                }

                String[] data = parseCsvLine(line);
                if (data.length != 5) {
                    throw new IOException("Invalid budget row: " + line);
                }

                // Column order matches HEADER: id, name, amount, category, period
                loaded.add(new Budget(
                        data[0].trim(),
                        data[1],
                        new BigDecimal(data[2].trim()),
                        data[4],
                        data[3]));
            }
        }

        budgets.clear();
        budgets.addAll(loaded);
    }

    /** Writes every budget back to budgets.csv, creating the user's folder if needed. */
    public void save() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);

        for (Budget b : budgets) {
            lines.add(String.join(",",
                    b.getBudgetId(),
                    escapeCsv(b.getBudgetName()),
                    b.getBudgetAmount().toPlainString(),
                    escapeCsv(b.getCategory()),
                    b.getBudgetPeriod()));
        }

        if (budgetsFile.getParent() != null) {
            Files.createDirectories(budgetsFile.getParent());
        }
        Files.write(budgetsFile, lines, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------ CRUD

    /** True if this user already has a budget for the category (case-insensitive). */
    public boolean categoryAlreadyBudgeted(String category) {
        return findByCategory(category) != null;
    }

    /**
     * Adds a budget. Rejected (returns false) if a budget for the same category or with the
     * same id already exists. If the budget has no id, the next free one (b1001, b1002, ...)
     * is assigned. Call save() afterwards to persist.
     */
    public boolean create(Budget b) {
        if (categoryAlreadyBudgeted(b.getCategory())) {
            return false;
        }

        if (b.getBudgetId() == null || b.getBudgetId().isBlank()) {
            b.setBudgetId(nextBudgetId());
        } else if (findById(b.getBudgetId()) != null) {
            return false;
        }

        budgets.add(b);
        return true;
    }

    /** @return a copy of all budgets. */
    public ArrayList<Budget> getBudgets() {
        return new ArrayList<>(budgets);
    }

    /** @return the budget with this id, or null. */
    public Budget findById(String budgetId) {
        if (budgetId == null) {
            return null;
        }
        for (Budget b : budgets) {
            if (b.getBudgetId().equalsIgnoreCase(budgetId.trim())) {
                return b;
            }
        }
        return null;
    }

    /** @return the (only) budget for this category, or null. */
    public Budget findByCategory(String category) {
        String wanted = Budget.normalize(category);
        if (wanted == null) {
            return null;
        }
        for (Budget b : budgets) {
            if (b.getCategory().equals(wanted)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Replaces the budget with this id. The id cannot change, and the new category must not
     * belong to a different budget. Call save() afterwards to persist.
     */
    public boolean update(String budgetId, Budget updated) {
        Budget existing = findById(budgetId);
        if (existing == null || !existing.getBudgetId().equalsIgnoreCase(updated.getBudgetId())) {
            return false;
        }

        Budget sameCategory = findByCategory(updated.getCategory());
        if (sameCategory != null && sameCategory != existing) {
            return false;
        }

        budgets.set(budgets.indexOf(existing), updated);
        return true;
    }

    /** Removes the budget with this id. Call save() afterwards to persist. */
    public boolean delete(String budgetId) {
        Budget existing = findById(budgetId);
        return existing != null && budgets.remove(existing);
    }

    // ------------------------------------------------------------------ spending

    /**
     * Total spent in the budget's category: reads transactions.csv directly and adds up the
     * expenses (negative amounts) whose category matches. Income/refunds (positive amounts)
     * are not counted. Returns 0 if the user has no transactions file.
     */
    public BigDecimal getSpent(Budget b) throws IOException {
        BigDecimal spent = BigDecimal.ZERO;

        if (!Files.exists(transactionsFile)) {
            return spent;
        }

        try (BufferedReader reader = Files.newBufferedReader(transactionsFile, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                return spent;
            }

            // Locate columns by name so a reordered file still works.
            String[] columns = parseCsvLine(header);
            int categoryCol = indexOf(columns, "category");
            int amountCol = indexOf(columns, "amount");
            if (categoryCol < 0 || amountCol < 0) {
                throw new IOException("transactions.csv is missing a category or amount column");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] data = parseCsvLine(line);
                if (data.length <= Math.max(categoryCol, amountCol)) {
                    throw new IOException("Invalid transaction row: " + line);
                }

                if (!Budget.normalize(data[categoryCol]).equals(b.getCategory())) {
                    continue;
                }

                BigDecimal amount = new BigDecimal(data[amountCol].trim());
                if (amount.signum() < 0) {
                    spent = spent.add(amount.negate());
                }
            }
        }

        return spent;
    }

    /** True if spending in the budget's category is greater than the budget amount. */
    public boolean isExceeded(Budget b) throws IOException {
        return getSpent(b).compareTo(b.getBudgetAmount()) > 0;
    }

    // ------------------------------------------------------------------ helpers

    /** Next id after the highest numeric "bNNNN" id in this user's list. */
    private String nextBudgetId() {
        int max = 1000;
        for (Budget b : budgets) {
            String id = b.getBudgetId();
            if (id != null && id.length() > 1 && (id.charAt(0) == 'b' || id.charAt(0) == 'B')) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(1)));
                } catch (NumberFormatException ignored) {
                    // non-numeric id, skip it
                }
            }
        }
        return "b" + (max + 1);
    }

    private static int indexOf(String[] columns, String name) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].trim().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    // Wrap text fields containing commas, quotes or newlines in quotes.
    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Split a CSV row, honouring quoted fields and "" escapes.
    private static String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (c == ',' && !insideQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        values.add(current.toString());
        return values.toArray(new String[0]);
    }
}
