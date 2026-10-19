import login.Account;

/**
 * Holds the currently logged-in Account for the duration of the program run.
 * Every menu receives the Account from here so it knows which user's data to operate on.
 */
public class Session {

    private Account currentAccount;

    /**
     *
     * Starts a session for the given account.
     */
    public void login(Account account) {
        this.currentAccount = account;
    }

    /**
     *
     * Ends the current session (sign out).
     */
    public void logout() {
        this.currentAccount = null;
    }

    public boolean isLoggedIn() {
        return currentAccount != null;
    }

    /**
     *
     * @return the logged-in Account, or null if nobody is logged in.
     */
    public Account getCurrentAccount() {
        return currentAccount;
    }

    /**
     *
     * @return the logged-in user's id, or null if nobody is logged in.
     */
    public String getUserID() {
        return currentAccount == null ? null : currentAccount.getUserID();
    }
}
