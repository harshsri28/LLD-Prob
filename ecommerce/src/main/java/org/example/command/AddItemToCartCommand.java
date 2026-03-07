package org.example.command;

import org.example.models.Item;
import org.example.models.ShoppingCart;

public class AddItemToCartCommand implements Command {
    private ShoppingCart cart;
    private Item item;

    public AddItemToCartCommand(ShoppingCart cart, Item item) {
        this.cart = cart;
        this.item = item;
    }

    @Override
    public void execute() {
        cart.addItem(item);
    }
}
