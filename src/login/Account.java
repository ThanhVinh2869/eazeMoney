package login;

import java.time.LocalDate;

/**
 * Hold accounts
 */


public class Account {
    private String userID;
    private String username;
    private String passwordHash;
    private String salt;
    private String displayName;
    private String currency;
    private LocalDate createdDate;
    private boolean onboardingComplete;

    public Account(String userID, String username, String passwordHash, String salt, String displayName, String currency, LocalDate createdDate, boolean onboardingComplete) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.displayName = displayName;
        this.currency = currency;
        this.createdDate = createdDate;
        this.onboardingComplete = onboardingComplete;
    }

    /**
     *
     * @return
     */
    public String getUserID() {
        return userID;
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }


    /**
     *
     * @return
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     *
     * @param passwordHash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     *
     * @return
     */
    public String getSalt() {
        return salt;
    }

    /**
     *
     * @param salt
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     *
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    /**
     *
     * @return
     */
    public LocalDate getCreatedDate() {
        return createdDate;
    }


    /**
     *
     * @return
     */
    public boolean isOnboardingComplete() {
        return onboardingComplete;
    }

    public void setOnboardingComplete(boolean onBoardingComplete) {
        this.onboardingComplete = onBoardingComplete;
    }

}
