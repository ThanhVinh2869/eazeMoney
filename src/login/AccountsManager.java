package login;

import java.time.LocalDate;
import java.util.ArrayList;

public class AccountsManager {

    private ArrayList<Account> accounts;
    private String accountsFileName = "data/accounts.csv";

    /**
     *
     */
    private Account convertCsvToAccount(String line, String delimiter) {
        String[] fields = line.trim().split(delimiter);
        if (fields.length != 8) {
            return null;
        }
        return new Account(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], LocalDate.parse(fields[6]), Boolean.parseBoolean(fields[7]));
    }

    /**
     *
     * @param accounts
     */
    public AccountsManager(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     *
     * @return
     */
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    /**
     *
     * @param accounts
     */
    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     *
     * @return
     */
    public String getAccountsFileName() {
        return accountsFileName;
    }

    /**
     *
     * @param accountsFileName
     */
    public void setAccountsFileName(String accountsFileName) {
        this.accountsFileName = accountsFileName;
    }

    /**
     *
     * @param account
     * @param delimiter
     * @return
     */
    private String convertAccountToCsv(Account account, String delimiter) {
        return account.getUserID() + delimiter + account.getUsername()  + delimiter + account.getPasswordHash() + delimiter + account.getSalt() + delimiter + account.getDisplayName() + delimiter + account.getCurrency() + delimiter + account.getCreatedDate() + delimiter + account.isOnboardingComplete();
    }
}
