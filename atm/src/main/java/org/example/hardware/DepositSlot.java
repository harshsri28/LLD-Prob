package org.example.hardware;

public class DepositSlot {
    private final Object depositLock = new Object();
    private volatile boolean isInUse = false;

    public boolean acquireSlot() {
        synchronized (depositLock) {
            if (!isInUse) {
                isInUse = true;
                System.out.println("[DEPOSIT SLOT] Slot acquired.");
                return true;
            }
            System.out.println("[DEPOSIT SLOT] Slot is in use.");
            return false;
        }
    }

    public void releaseSlot() {
        synchronized (depositLock) {
            isInUse = false;
            System.out.println("[DEPOSIT SLOT] Slot released.");
        }
    }

    public boolean acceptDeposit(double amount) {
        if (!acquireSlot()) {
            return false;
        }
        try {
            System.out.println("[DEPOSIT SLOT] Accepted deposit of $" + amount);
            return true;
        } finally {
            releaseSlot();
        }
    }

    public boolean isInUse() {
        return isInUse;
    }
}
