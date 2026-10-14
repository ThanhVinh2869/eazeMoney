package transactions;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Transaction {

    private String transactionId;
    private LocalDate date;
    private String note;
    private String category;
    private String walletId;
    private BigDecimal amount;

    public Transaction(String transactionId, LocalDate date, String note, String category, String walletId, BigDecimal amount){

        this.transactionId=transactionId;
        this.date=date;
        this.note=note;
        this.category=category;
        this.walletId=walletId;
        this.amount=amount;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }


}
