package org.example.hardware;

public class Keypad {

    public int getInput(int value) {
        System.out.println("[KEYPAD] Input received: " + value);
        return value;
    }

    public int getPin(int pin) {
        System.out.println("[KEYPAD] PIN entered: ****");
        return pin;
    }

    public double getAmount(double amount) {
        System.out.println("[KEYPAD] Amount entered: $" + amount);
        return amount;
    }
}
