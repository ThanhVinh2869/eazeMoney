package dashboard;

import transactions.Transaction;
import transactions.TransactionList;
import wallet.Wallet;
import wallet.WalletList;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DashboardServices {
    private final Dashboard dashboard;
    private final TransactionList transactionList;
    private final WalletList walletList;
    private final String userId;

    public DashboardServices(String userId) {
        this.userId = userId;
        this.dashboard = new Dashboard(userId);
        this.transactionList = new TransactionList(userId);
        this.walletList = new WalletList();
    }

    public void displaySummaries() throws IOException {
        System.out.println("Current balance: " + dashboard.getCurrentBalance());
        System.out.println("Total income: " + dashboard.getTotalIncome());
        System.out.println("Total expense: " + dashboard.getTotalExpense());

        System.out.println("Wallet balances:");
        for (Map.Entry<String, BigDecimal> entry : getAllWalletBalances().entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Totals by category:");
        for (Map.Entry<String, BigDecimal> entry : getAllCategoryTotal().entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }
    }

    public Map<String, BigDecimal> getAllWalletBalances() throws IOException {
        walletList.loadWallets(userId);
        Map<String, BigDecimal> balances = new LinkedHashMap<>();
        for (Wallet wallet : walletList.getWallets()) {
            balances.put(wallet.getWalletName(), dashboard.getBalanceByWallet(wallet));
        }
        return balances;
    }

    public Map<String, BigDecimal> getAllCategoryTotal() throws IOException {
        transactionList.loadTransactions();
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        Set<String> seenCategories = new LinkedHashSet<>();
        for (Transaction transaction : transactionList.getTransactions()) {
            String category = transaction.getCategory();
            if (category != null && seenCategories.add(category.toLowerCase(java.util.Locale.ROOT))) {
                totals.put(category, dashboard.getTotalByCategory(category));
            }
        }
        return totals;
    }
}
