package dashboard;

import transactions.Transaction;
import transactions.TransactionList;
import wallet.Wallet;
import wallet.WalletList;

import java.io.IOException;
import java.math.BigDecimal;

public class Dashboard {
    private final String userId;
    private final TransactionList transactionList;
    private final WalletList walletList;

    public Dashboard(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId must not be blank.");
        }
        this.userId = userId;
        this.transactionList = new TransactionList(userId);
        this.walletList = new WalletList();
    }

    public String getUserId() {
        return userId;
    }

    // -----------------------Calculations
    public BigDecimal getCurrentBalance() throws IOException {
        BigDecimal balance = BigDecimal.ZERO;
        walletList.loadWallets(userId);
        for (Wallet wallet : walletList.getWallets()) {
            balance = balance.add(BigDecimal.valueOf(wallet.getStartingBalance()));
        }
        transactionList.loadTransactions();
        for (Transaction transaction : transactionList.getTransactions()) {
            balance = balance.add(transaction.getAmount());
        }
        return balance;
    }

    public BigDecimal getTotalIncome() throws IOException {
        BigDecimal income = BigDecimal.ZERO;
        transactionList.loadTransactions();
        for (Transaction transaction : transactionList.getTransactions()) {
            if (transaction.getAmount().signum() > 0) {
                income = income.add(transaction.getAmount());
            }
        }
        return income;
    }

    public BigDecimal getTotalExpense() throws IOException {
        BigDecimal expense = BigDecimal.ZERO;
        transactionList.loadTransactions();
        for (Transaction transaction : transactionList.getTransactions()) {
            if (transaction.getAmount().signum() < 0) {
                expense = expense.add(transaction.getAmount());
            }
        }
        return expense;
    }

    public BigDecimal getBalanceByWallet(Wallet wallet) throws IOException {
        BigDecimal balance = BigDecimal.valueOf(wallet.getStartingBalance());
        transactionList.loadTransactions();
        for (Transaction transaction : transactionList.getTransactions()) {
            if (wallet.getWalletId().equals(transaction.getWalletId())) {
                balance = balance.add(transaction.getAmount());
            }
        }
        return balance;
    }

    public BigDecimal getTotalByCategory(String category) throws IOException {
        BigDecimal total = BigDecimal.ZERO;
        transactionList.loadTransactions();
        for (Transaction transaction : transactionList.getTransactions()) {
            if (transaction.getCategory().equalsIgnoreCase(category)) {
                total = total.add(transaction.getAmount());
            }
        }
        return total;
    }

}
