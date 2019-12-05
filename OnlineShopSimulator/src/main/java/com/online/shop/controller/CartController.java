package com.online.shop.controller;

import java.util.ArrayList;
import java.util.List;

import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;
import com.online.shop.view.HistoryView;
import com.online.shop.view.ItemsView;

public class CartController {
	private ItemsView itemsView;
	private ItemsRepository itemsRepository;
	private Cart cart;
	private HistoryView historyView;

	public CartController(ItemsView itemsView, ItemsRepository itemsRepository,HistoryView historyView) {
		this.itemsView = itemsView;
		this.itemsRepository = itemsRepository;
		this.historyView = historyView;
		this.cart = new Cart();
	}

	public void addToCart(Item item) {
		List<Item> items = cart.getItems();
		
		if (!items.contains(item)) {
			item.setQuantity(1);
			itemsView.itemAddedToCart(item);
			items.add(item);
		} else {
			if (items.get(items.indexOf(item)).getQuantity() < item.getQuantity()) {
				items.get(items.indexOf(item)).setQuantity((items.get(items.indexOf(item)).getQuantity()+1));
				itemsView.updateItemsCart(items);
			} else
				return;
		}
	}

	public int cartSize() {
		return cart.getItems().size();
	}

	public List<Item> cartItems() {
		return cart.getItems();
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

	public void removeFromCart(Item item) {
		List<Item> items = cart.getItems();

		if (items.get(items.indexOf(item)).getQuantity() == 1) {
			items.remove(items.indexOf(item));
			itemsView.itemRemovedFromCart(item);
		} else {
			items.get(items.indexOf(item)).setQuantity(items.get(items.indexOf(item)).getQuantity() - 1);
			itemsView.updateItemsCart(items);
		}
	}

	public void completePurchase() {
		List<Item> items = cart.getItems();
		Item retrievedItem;

		for (Item item : items) {
			retrievedItem = itemsRepository.findByProductCode(item.getProductCode());
			
			if (retrievedItem.getQuantity() == item.getQuantity()) {
				itemsRepository.remove(item.getProductCode());
			} else {
				itemsRepository.modifyQuantity(retrievedItem, -item.getQuantity());
			}
		}
		
		cart.setItems(new ArrayList<Item>());
		
		itemsView.showItemsCart(null);
		itemsView.showItemsShop(itemsRepository.findAll());
	}

	public void removeFromHistory(Cart cart) {
		historyView.removeCart(cart);
	}
}
