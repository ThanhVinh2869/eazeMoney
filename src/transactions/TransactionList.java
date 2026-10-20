package transactions;

import java.util.ArrayList;
import java.time.LocalDate;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class TransactionList {
    private ArrayList<Transaction> transactions;
    private Path filePath;

    public TransactionList(String userId) {
        transactions = new ArrayList<>();

        filePath = Paths.get("data", userId, "transactions.csv");
    }

    public TransactionList(Path filePath) {
        transactions = new ArrayList<>();
        this.filePath = filePath;
    }


    // -----------CRUD Operations-----------

    // CREATE: Add a new transaction
    public boolean addTransaction(Transaction transaction) {
        // Check whether the transaction ID already exists
        for (Transaction existing : transactions) {
            if (existing.getTransactionId().equals(transaction.getTransactionId())) {
                return false;
            }
        }

        // Only add the transaction if its ID is unique
        transactions.add(transaction);
        return true;
    }

    // READ:Retrieve all transactions
    public ArrayList<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    // UPDATE: Replace an existing transaction
    public boolean updateTransaction(String transactionId, Transaction updatedTransaction) {
        for (int i = 0; i < transactions.size(); i++) {
            Transaction current = transactions.get(i);

            if (current.getTransactionId().equals(transactionId)) {
                if (!updatedTransaction.getTransactionId().equals(transactionId)) {
                    return false;
                }

                transactions.set(i, updatedTransaction);
                return true;
            }
        }

        return false;
    }

    // DELETE: Remove a transaction using its ID
    public boolean deleteTransaction(String transactionId) {
        for (int i = 0; i < transactions.size(); i++) {
            Transaction current = transactions.get(i);

            if (current.getTransactionId().equals(transactionId)) {
                transactions.remove(i);
                return true;
            }
        }

        return false;
    }


    // -----------Filter methods-----------

    // Find transactions by category
    public ArrayList<Transaction> findByCategory(String category) {
        ArrayList<Transaction> results = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equalsIgnoreCase(category)) {
                results.add(transaction);
            }
        }

        return results;
    }

    // Find transactions by wallet ID
    public ArrayList<Transaction> findByWallet(String walletId) {
        ArrayList<Transaction> results = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getWalletId().equals(walletId)) {
                results.add(transaction);
            }
        }

        return results;
    }

    // Find transactions within a date range
    public ArrayList<Transaction> findByDateRange(LocalDate start, LocalDate end) {
        ArrayList<Transaction> results = new ArrayList<>();

        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getDate();

            if (!date.isBefore(start) && !date.isAfter(end)) {
                results.add(transaction);
            }
        }

        return results;
    }


    // -----------CSV IO Handling-----------
    public void loadTransactions() throws IOException {
        ArrayList<Transaction> loaded = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return;
        }

        // Read all lines from the CSV file
        for (String line : Files.readAllLines(filePath)) {

            // Skip the header and empty lines
            if (line.startsWith("transactionId,") || line.isBlank()) {
                continue;
            }

            // Split each row into individual values
            String[] data = parseCsvLine(line);

            if (data.length != 6) {
                throw new IOException("Invalid transaction row: " + line);
            }

            // Convert CSV values into the correct Java types
            Transaction transaction = new Transaction(
                    data[0],
                    LocalDate.parse(data[1]),
                    data[2],
                    data[3],
                    data[4],
                    new BigDecimal(data[5])
            );

            loaded.add(transaction);
        }

        transactions.clear();
        transactions.addAll(loaded);
    }

    public void saveTransactions() throws IOException {

        List<String> lines = new ArrayList<>();

        // CSV header
        lines.add("transactionId,date,note,category,walletId,amount");

        // Convert each transaction into a CSV row
        for (Transaction transaction : transactions) {

            String line = String.join(",",
                    transaction.getTransactionId(),
                    transaction.getDate().toString(),
                    escapeCsv(transaction.getNote()),
                    escapeCsv(transaction.getCategory()),
                    transaction.getWalletId(),
                    transaction.getAmount().toPlainString()
            );

            lines.add(line);
        }


        //Create the users folder if it doesn't exist
        Files.createDirectories(filePath.getParent());
        // Write all rows/transactions to the CSV file
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    // Handle commas and quotation marks in text fields
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")
                || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Two quotation marks represent one literal quote
                if (insideQuotes && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {
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
