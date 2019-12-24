package com.online.shop.controller;

import java.util.ArrayList;
import java.util.List;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;
import com.online.shop.view.HistoryView;
import com.online.shop.view.ShopView;

public class CartController {
	private ShopView itemsView;
	private ItemsRepository itemsRepository;
	private HistoryView historyView;
	private Cart cart;

	public CartController(ShopView ShopView, ItemsRepository itemsRepository,HistoryView historyView) {
		this.itemsView = ShopView;
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
				items.get(items.indexOf(item)).setQuantity(items.get(items.indexOf(item)).getQuantity() + 1);
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

	public void completePurchase(String label) {
		cart.setLabel(label);
		//TODO : check it!
		for(Cart carts : itemsRepository.findAllCarts()) {
			if(cart.getLabel() == carts.getLabel() ) {
				itemsView.errorLog("Label already exist. Change it" + label, null);
				return;
			}
		}
		List<Item> items = cart.getItems();
		List<Item> itemsNotStored = new ArrayList<Item>();
		Item retrievedItem;
		boolean isStored = true;

		for (Item item : items) {
			retrievedItem = itemsRepository.findByProductCode(item.getProductCode());
			if (retrievedItem == null) {
				isStored = false;
				itemsNotStored.add(item);
			} else {
				if (retrievedItem.getQuantity() == item.getQuantity()) {
					itemsRepository.remove(item.getProductCode());
				} else {
					itemsRepository.modifyQuantity(retrievedItem, -item.getQuantity());
				}
			}
		}
		if (isStored && !items.isEmpty()) {
			itemsRepository.storeCart(cart);
			cart.setItems(new ArrayList<Item>());
			items = cart.getItems();
			itemsView.updateItemsCart(items);
			itemsView.updateItemsShop(itemsRepository.findAll());
		} else {
			itemsView.errorLog("Item/s not found", itemsNotStored);
		}
	}

	public void allCarts() {
		historyView.showHistory(itemsRepository.findAllCarts());
	}

	public List<Cart> getListCart() {
		List<Cart>  carts = itemsRepository.findAllCarts();
		if(carts.isEmpty()) {
			return null;
		}
		return carts;
	}

	public void allItemsCart(Cart cart) {
		historyView.showItemsCart(cart);
	}

	public void removeCart(Cart cartToRemove) {
		if (itemsRepository.findCart(cartToRemove.getDate(), cartToRemove.getLabel()) == null) {
			itemsView.errorLogCart("Cart not found", cartToRemove );
			return;
		}
		itemsRepository.removeCart(cartToRemove.getDate(), cartToRemove.getLabel());
		historyView.removeCart(cartToRemove);
	}
}