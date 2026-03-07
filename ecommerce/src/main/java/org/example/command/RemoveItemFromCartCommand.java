package org.example.command;

import org.example.models.ShoppingCart;

public class RemoveItemFromCartCommand implements Command {
    private ShoppingCart cart;
    private String productId;

    public RemoveItemFromCartCommand(ShoppingCart cart, String productId) {
        this.cart = cart;
        this.productId = productId;
    }

    @Override
    public void execute() {
        cart.removeItem(productId);
    }
}
