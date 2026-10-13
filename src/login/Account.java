package login;

public class Account {
    private String userID;
    private String username;
    private String passwordHash;
    private String salt;
    private String displayName;
    private String currency = "USD";        // hard-coded: defaulted currency is USD
    private LocalDate createdDate;
    private boolean onBoardingComplete = false;
}
