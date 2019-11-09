package com.online.shop.controller;

import java.util.List;

import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.view.ItemsView;

public class CartController {
	private ItemsView itemsView;
	private Cart cart;

	public CartController(ItemsView itemsView) {
		this.itemsView = itemsView;
	}

	public void add(Item item) {
		List<Item> items = cart.getItems();

		if (!items.contains(item)) {
			item.setQuantity(1);
			items.add(item);
		} else {
			if (items.get(items.indexOf(item)).getQuantity() < item.getQuantity())
				items.get(items.indexOf(item)).
				setQuantity(items.get(items.indexOf(item)).
						getQuantity() + 1);
			else
				return;
		}

		itemsView.itemAddedToCart(item);
	}

	public int cartSize() {
		return cart.getItems().size();
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public int findItemQuantity(Item item) {
		List<Item> items = cart.getItems();

		if (items.contains(item)) {
			return items.get(items.indexOf(item)).getQuantity();
		}

		return 0;
	}

	public void remove(Item item) {
		List<Item> items = cart.getItems();
		
		if (items.get(items.indexOf(item)).getQuantity() == 1) {
			items.remove(items.indexOf(item));
		}
		else {
			items.get(items.indexOf(item)).
			setQuantity(items.get(items.indexOf(item)).
					getQuantity() - 1);
		}
		
		itemsView.itemRemovedFromCart(item);
	}

}
