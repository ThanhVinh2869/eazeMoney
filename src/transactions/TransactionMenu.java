package transactions;
import java.util.List;
import java.time.LocalDate;
import java.io.IOException;

public class TransactionMenu {

    private TransactionList transactionList;


    public TransactionMenu(String userId) throws IOException {
        this.transactionList = new TransactionList(userId);
        this.transactionList.loadTransactions();
    }
    public TransactionList getTransactionList(){
        return transactionList;
    }


    public void displayTransactions(List<Transaction> transactions) {

        System.out.println("\n===== TRANSACTIONS =====");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.printf("%-12s %-12s %-20s %-15s %12s%n",
                "ID", "DATE", "NOTE", "CATEGORY", "AMOUNT");

        System.out.println("--------------------------------------------------------------");

        for (Transaction transaction : transactions) {
            System.out.printf("%-12s %-12s %-20.20s %-15s %12s%n",
                    transaction.getTransactionId(),
                    transaction.getDate(),
                    transaction.getNote(),
                    transaction.getCategory(),
                    transaction.getAmount());
        }
    }


    public void displayByCategory(String category) {
        List<Transaction> filtered =
                transactionList.findByCategory(category);

        displayTransactions(filtered);
    }


    public void displayByWallet(String walletId) {
        List<Transaction> filtered =
                transactionList.findByWallet(walletId);

        displayTransactions(filtered);
    }


    public void displayByDateRange(LocalDate start, LocalDate end) {
        List<Transaction> filtered =
                transactionList.findByDateRange(start, end);

        displayTransactions(filtered);
    }

    public void addTransaction(Transaction transaction) throws IOException {

        boolean added = transactionList.addTransaction(transaction);

        if (added) {
            transactionList.saveTransactions();
            System.out.println("Transaction added successfully!");
        } else {
            System.out.println("Transaction ID already exists!");
        }
    }



    public void updateTransaction(String transactionId,
                                  Transaction updatedTransaction) throws IOException {

        boolean updated = transactionList.updateTransaction(
                transactionId, updatedTransaction);

        if (updated) {
            transactionList.saveTransactions();
            System.out.println("Transaction updated successfully!");
        } else {
            System.out.println("Transaction not found.");
        }
    }


    public void deleteTransaction(String transactionId) throws IOException {

        boolean deleted = transactionList.deleteTransaction(transactionId);

        if (deleted) {
            transactionList.saveTransactions();
            System.out.println("Transaction deleted successfully!");
        } else {
            System.out.println("Transaction not found.");
        }
    }





}
