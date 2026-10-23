package wallet;

import budgets.Budget;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * All wallets of one user, backed by data/{userId}/wallets.csv.
 * Every change (add, update, delete) is written to the file immediately.
 */
public class WalletList {
    private static final String HEADER = "walletId,walletName,startingBalance";

    private String userId;
    private final List<Wallet> wallets = new ArrayList<>();

    /** Reads data/{userId}/wallets.csv. A missing file simply gives an empty list. */
    public void loadWallets(String userId) {
        setUserId(userId);
        wallets.clear();

        Path file = getFilePath();
        if (!Files.exists(file)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 1; i < lines.size(); i++) {          // line 0 is the header
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    wallets.add(convertLineToWallet(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping bad wallet row: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read wallets file: " + e.getMessage());
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId must not be blank.");
        }
        this.userId = userId;
    }

    /** Read-only view of the wallets. Use add/update/delete to change them. */
    public List<Wallet> getWallets() {
        return Collections.unmodifiableList(wallets);
    }

    /** Read-only view retained for callers using the previous accessor name. */
    public List<Wallet> getAll() {
        return getWallets();
    }

    /** @return the wallet with this id, or null if there is none */
    public Wallet findWalletById(String walletId) {
        for (Wallet w : wallets) {
            if (w.getWalletId().equals(walletId)) {
                return w;
            }
        }
        return null;
    }

    /** True if a wallet with this name already exists (ignores upper/lower case). */
    public boolean isWalletExists(String walletId) {
        return this.findWalletById(walletId) != null;
    }

    public void addWallet(Wallet wallet) {
        wallets.add(wallet);
        saveWalletToFile();
    }

    public String nextWalletId() {
        int max = 1000;
        for (Wallet w : wallets) {
            String id = w.getWalletId();
            if (id != null && id.length() > 1 && (id.charAt(0) == 'w' || id.charAt(0) == 'W')) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(1)));
                } catch (NumberFormatException ignored) {
                    // non-numeric id, skip it
                }
            }
        }
        return "w" + (max + 1);
    }

    /**
     * Saves changes made to a wallet that is already in the list
     * (call it after using the wallet's setters).
     * @return false if no wallet with that id exists
     */
    public boolean updateWallet(Wallet wallet) {
        for (int i = 0; i < wallets.size(); i++) {
            if (wallets.get(i).getWalletId().equals(wallet.getWalletId())) {
                wallets.set(i, wallet);
                saveWalletToFile();
                return true;
            }
        }
        return false;
    }

    /** @return false if no wallet with that id exists */
    public boolean deleteWallet(String walletId) {
        boolean removed = wallets.removeIf(w -> w.getWalletId().equals(walletId));
        if (removed) {
            saveWalletToFile();
        }
        return removed;
    }

    private Path getFilePath() {
        if (userId == null) {
            throw new IllegalStateException("WalletList userId must be set before accessing its file.");
        }
        return Paths.get("data", userId, "wallets.csv");
    }

    private void saveWalletToFile() {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Wallet w : wallets) {
            lines.add(convertWalletToLine(w));
        }
        try {
            Path file = getFilePath();
            Files.createDirectories(file.getParent());
            Files.write(file, lines);
        } catch (IOException e) {
            System.out.println("Could not save wallets file: " + e.getMessage());
        }
    }

    /** Builds a Wallet from a CSV line. Throws IllegalArgumentException if malformed. */
    private Wallet convertLineToWallet(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected 3 fields but found " + parts.length + ": " + line);
        }
        try {
            return new Wallet(parts[0].trim(), parts[1].trim(), Double.parseDouble(parts[2].trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad startingBalance in row: " + line, e);
        }
    }

    /** Converts a wallet to a CSV line (no trailing newline). */
    private String convertWalletToLine(Wallet wallet) {
        return wallet.getWalletId() + "," + wallet.getWalletName() + ","
                + String.format(Locale.US, "%.2f", wallet.getStartingBalance());
    }
}