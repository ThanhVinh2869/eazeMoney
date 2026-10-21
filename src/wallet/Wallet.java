package wallet;

import transactions.TransactionList;

import java.util.UUID;

/** One row of wallets.csv: walletId, walletName, startingBalance. */
public class Wallet {

    private final String walletId;
    private String walletName;
    private double startingBalance;
    private TransactionList transactionList;

    /** Creates a brand-new wallet with a generated id. */
    public Wallet(String walletName, double startingBalance) {
        this(UUID.randomUUID().toString(), walletName, startingBalance);
    }

    /** Used when loading an existing wallet from the CSV. */
    public Wallet(String walletId, String walletName, double startingBalance) {
        this.walletId = walletId;
        setWalletName(walletName);
        this.startingBalance = startingBalance;
    }

    // -----------Getters-----------
    public String getWalletId() {
        return this.walletId;
    }
    public String getWalletName() {
        return this.walletName;
    }
    public double getStartingBalance() {
        return this.startingBalance;
    }
    public TransactionList getTransactionList() {
        return this.transactionList;
    }

    // -----------Setters-----------
    /** Commas are replaced with spaces so they can't break the CSV row. */
    public void setWalletName(String walletName) {
        this.walletName = walletName.replace(",", " ").trim();
    }
    public void setStartingBalance(double startingBalance) {
        this.startingBalance = startingBalance;
    }
    public void setTransactionList(TransactionList transactionList) {
        this.transactionList = transactionList;
    }
}