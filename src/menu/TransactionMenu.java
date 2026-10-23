package menu;
import transactions.Transaction;
import transactions.TransactionList;
import wallet.WalletList;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.io.IOException;
import java.util.Scanner;

public class TransactionMenu extends Menu {
    private TransactionList transactionList;
    private final WalletList walletList;


    public TransactionMenu(Scanner scanner, String userId) throws IOException {
        super(scanner);
        this.transactionList = new TransactionList(userId);
        this.transactionList.loadTransactions();
        this.walletList = new WalletList();
        this.walletList.loadWallets(userId);
    }

    public TransactionList getTransactionList(){
        return transactionList;
    }


    @Override
    protected String getTitle() {
        return "TRANSACTIONS MENU";
    }

    @Override
    protected List<String> getOptions() {
        return List.of("Show all transactions", "Filter by category", "Filter by wallet",
                "Filter by date range", "Add transaction", "Update transaction", "Delete transaction");
    }

    @Override
    protected void handleOption(int option) {
        String transactionId;

        switch (option) {
            case 1:
                handleDisplayTransactions(getTransactionList().getTransactions());
                break;
            case 2:
                String category = reader.readString("Enter category: ");
                if (category != null) {
                    handleDisplayByCategory(category);
                }
                break;
            case 3:
                String walletId = reader.readString("Wallet ID: ");
                if (walletId != null) {
                    handleDisplayByWallet(walletId);
                }
                break;
            case 4:
                LocalDate startDate = reader.readDate("Start date (YYYY-MM-DD): ");
                if (startDate != null) {
                    LocalDate endDate = reader.readDate("End date (YYYY-MM-DD): ");
                    if (endDate != null) {
                        if (endDate.isBefore(startDate)) {
                            System.out.println("[!] End date must not be before start date.");
                        } else {
                            handleDisplayByDateRange(startDate, endDate);
                        }
                    }
                }
                break;
            case 5:
                transactionId = transactionList.nextTransactionId();
                Transaction transaction = readTransaction(transactionId);
                if (transaction != null) {
                    try {
                        handleAddTransaction(transaction);
                    } catch (IOException e) {
                        System.out.println("[!] IO Error: " + e.getMessage());
                    }
                }
                break;
            case 6:
                handleDisplayTransactions(getTransactionList().getTransactions());

                transactionId = reader.readString("Transaction ID to update: ");
                if (transactionId != null) {
                    Transaction toBeUpdatedTransaction = transactionList.findTransactionById(transactionId);
                    if (toBeUpdatedTransaction != null) {
                        try {
                            Transaction newTransaction = readTransaction(transactionId);
                            handleUpdateTransaction(transactionId, newTransaction);
                        } catch (IOException e) {
                            System.out.println("[!] IO Error: " + e.getMessage());
                        } catch (NullPointerException e) {
                            System.out.println("[!] Fail to update transaction. Please try again!");
                        }
                    }
                    else {
                        System.out.println("[!] Transaction ID not found.");
                    }
                }
                break;
            case 7:
                handleDisplayTransactions(getTransactionList().getTransactions());

                transactionId = reader.readString("Transaction ID to delete: ");
                if (transactionId != null) {
                    try {
                        handleDeleteTransaction(transactionId);
                    } catch (IOException e) {
                        System.out.println("[!] IO Error: " + e.getMessage());
                    }
                }
                break;
            case 0:

        }
    }

    // -----------Handle Menu Options-----------
    public void handleDisplayTransactions(List<Transaction> transactions) {
        System.out.println("\n===== TRANSACTIONS =====");

        if (transactions.isEmpty()) {
            System.out.println("[!] No transactions found.");
            return;
        }

        System.out.printf("%-12s %-12s %-20s %-15s %-12s %12s%n",
                "ID", "DATE", "NOTE", "CATEGORY", "WALLET ID", "AMOUNT");

        System.out.println("-------------------------------------------------------------------------------------------------");

        for (Transaction transaction : transactions) {
            System.out.printf("%-12s %-12s %-20.20s %-15s %-12s %12s%n",
                    transaction.getTransactionId(),
                    transaction.getDate(),
                    transaction.getNote(),
                    transaction.getCategory(),
                    transaction.getWalletId(),
                    transaction.getAmount());
        }
    }

    public void handleDisplayByCategory(String category) {
        List<Transaction> filtered =
                transactionList.findByCategory(category);

        handleDisplayTransactions(filtered);
    }

    public void handleDisplayByWallet(String walletId) {
        List<Transaction> filtered =
                transactionList.findByWallet(walletId);

        handleDisplayTransactions(filtered);
    }

    public void handleDisplayByDateRange(LocalDate start, LocalDate end) {
        List<Transaction> filtered =
                transactionList.findByDateRange(start, end);

        handleDisplayTransactions(filtered);
    }

    public void handleAddTransaction(Transaction transaction) throws IOException {
        boolean added = transactionList.addTransaction(transaction);

        if (added) {
            transactionList.saveTransactions();
            System.out.println("Transaction added successfully!");
        } else {
            System.out.println("[!] Transaction ID already exists!"); // this should never be reached
        }
    }

    public void handleUpdateTransaction(String transactionId, Transaction updatedTransaction) throws IOException {

        boolean updated = transactionList.updateTransaction(transactionId, updatedTransaction);

        if (updated) {
            transactionList.saveTransactions();
            System.out.println("Transaction updated successfully!");
        } else {
            System.out.println("[!] Transaction not found.");
        }
    }

    public void handleDeleteTransaction(String transactionId) throws IOException {

        boolean deleted = transactionList.deleteTransaction(transactionId);

        if (deleted) {
            transactionList.saveTransactions();
            System.out.println("Transaction deleted successfully!");
        } else {
            System.out.println("[!] Transaction not found.");
        }
    }

    // Helper functions
    private Transaction readTransaction(String transactionId) {
        LocalDate date = reader.readDate("Date (YYYY-MM-DD): ");
        if (date == null) {
            return null;
        }
        String note = reader.readString("Note: ");
        if (note == null) {
            return null;
        }
        String category = reader.readString("Category: ");
        if (category == null) {
            return null;
        }
        String walletId = reader.readString("Wallet ID: ");
        if (walletId == null) {
            return null;
        }
        if (!walletList.isWalletExists(walletId)) {
            System.out.println("[!] Wallet ID does not exist for this user.");
            return null;
        }
        BigDecimal amount = reader.readBigDecimal();
        if (amount == null) {
            return null;
        }
        return new Transaction(transactionId, date, note, category, walletId, amount);
    }
}
