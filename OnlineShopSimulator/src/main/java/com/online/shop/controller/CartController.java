package com.online.shop.controller;

import java.util.ArrayList;
import java.util.List;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.ShopRepository;
import com.online.shop.view.ShopView;

public class CartController {
	
	private ShopView shopView;
	private ShopRepository shopRepository;
	private Cart cart;

	public CartController(ShopView ShopView, ShopRepository shopRepository) {
		this.shopView = ShopView;
		this.shopRepository = shopRepository;
		this.cart = new Cart();
	}

	public void addToCart(Item item) {
		List<Item> items = cart.getItems();
		if (!items.contains(item)) {
			item.setQuantity(1);
			shopView.itemAddedToCart(item);
			items.add(item);
		} else {
			if (items.get(items.indexOf(item)).getQuantity() < item.getQuantity()) {
				items.get(items.indexOf(item)).setQuantity(items.get(items.indexOf(item)).getQuantity() + 1);
				shopView.updateItemsCart(items);
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
			shopView.itemRemovedFromCart(item);
		} else {
			items.get(items.indexOf(item)).setQuantity(items.get(items.indexOf(item)).getQuantity() - 1);
			shopView.updateItemsCart(items);
		}
	}

	public void completePurchase(String label) {
		cart.setLabel(label);
		for(Cart carts : shopRepository.findAllCarts()) {
			if(cart.getLabel().equals(carts.getLabel())) {
				shopView.errorLogCart("Cart with this label already exists: " , cart);
				return;
			}
		}
		List<Item> items = cart.getItems();
		List<Item> itemsNotStored = new ArrayList<Item>();
		Item retrievedItem;
		boolean isStored = true;

		for (Item item : items) {
			retrievedItem = shopRepository.findItemByProductCode(item.getProductCode());
			if (retrievedItem == null) {
				isStored = false;
				itemsNotStored.add(item);
			} else {
				if (retrievedItem.getQuantity() == item.getQuantity()) {
					shopRepository.removeItem(item.getProductCode());
				} else {
					shopRepository.modifyItemQuantity(retrievedItem, -item.getQuantity());
				}
			}
		}
		if (isStored && !items.isEmpty()) {
			shopRepository.storeCart(cart);
			cart.setItems(new ArrayList<Item>());
			items = cart.getItems();
			shopView.showHistory(shopRepository.findAllCarts());
			shopView.updateItemsCart(items);
			shopView.updateItemsShop(shopRepository.findAllItems());
		} else {
			shopView.errorLog("Item/s not found", itemsNotStored);
		}
	}

	public void allCarts() {
		shopView.showHistory(shopRepository.findAllCarts());
	}

	public void allItemsCart(Cart cart) {
		shopView.showItemsCart(cart);
	}

	public void removeCart(Cart cartToRemove) {
		if (shopRepository.findCart(cartToRemove.getDate(), cartToRemove.getLabel()) == null) {
			shopView.errorLogCart("Cart not found", cartToRemove );
			return;
		}
		shopRepository.removeCart(cartToRemove.getDate(), cartToRemove.getLabel());
		shopView.removeCart(cartToRemove);
	}
}