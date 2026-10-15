package login;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 */
public class AccountsManager {

    private static final String DEFAULT_CURRENCY = "USD";
    private static final String HEADER = "userId,username,passwordHash,salt,displayName,currency,createdDate,onboardingComplete";
    private static final String DELIMITER = ",";

    private ArrayList<Account> accounts;
    private final String accountsFileName = "data/accounts.csv";

    public AccountsManager() throws IOException {
        this.accounts = new ArrayList<>();
        loadAccounts();
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
     * @return
     */
    public String getAccountsFileName() {
        return accountsFileName;
    }

    /**
     *
     */
    private void loadAccounts () throws IOException {
        try (Scanner scanner = new Scanner(new File(accountsFileName))) {
            String line;
            Account account;

            if (scanner.hasNextLine()) {
                scanner.nextLine(); // skip header row
            }
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                account = convertCsvToAccount(line, DELIMITER);
                if(account != null) {
                    accounts.add(account);
                }
            }
        }
    }

    /**
     *
     */
    private void saveAccounts() throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(accountsFileName))){
            bw.write(HEADER);
            bw.newLine();
            for (Account account : accounts) {
                bw.write(convertAccountToCsv(account, DELIMITER));
                bw.newLine();
            }
        }
    }

    /**
     *
     * @param account
     * @return
     */
    private String convertAccountToCsv(Account account,  String delimiter) {
        return account.getUserID() + DELIMITER + account.getUsername()  + DELIMITER + account.getPasswordHash() + DELIMITER + account.getSalt() + DELIMITER + account.getDisplayName() + DELIMITER + account.getCurrency() + DELIMITER + account.getCreatedDate() + DELIMITER + account.isOnboardingComplete();
    }

    /**
     *
     */
    private Account convertCsvToAccount(String line, String delimiter) {
        String[] fields = line.trim().split(DELIMITER);
        if (fields.length != 8) {
            return null;
        }
        return new Account(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], LocalDate.parse(fields[6]), Boolean.parseBoolean(fields[7]));
    }

    /**
     * ACTION METHODS
     */


    /**
     *
     */
    public Account findByUsername(String username){
        for (Account a : accounts) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                return a;
            }
        }
        return null;
    }

    /**
     *
     */
    public boolean isAccountExist(String username){
        for (Account a : accounts) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public Account authenticate(String username, String password){
        Account account = findByUsername(username);

        if(account == null || password == null)
            return null;

        if(account.getPasswordHash().equals(password))
            return account;

        return null;
    }

    /**
     *
     */
    public Account registerAccount(String username, String password, String confirmedPassword, String displayName) throws IOException, IllegalArgumentException {
        InputValidator.checkUsername(username);
        InputValidator.checkPassword(password, confirmedPassword);
        InputValidator.checkDisplayName(displayName);

        String uname = username.trim();
        String disName = displayName.trim();

        if(isAccountExist(username))
            throw new IllegalArgumentException("This username is already taken.");

        String userId = generateNewUserId();

        String passwordHash = password;
        String salt = "SH123";

        Account account = new Account(userId, uname, passwordHash, salt, disName, "USD", LocalDate.now(), false);
        accounts.add(account);
        saveAccounts();

        createUserDataFolder(userId);

        return account;
    }

    //TO DO: a method to generate Id
    private String generateNewUserID(){}

    //TO DO: a method to creata new folder for new user
    private void createUserDataFolder(String userId) throws IOException {}
}
