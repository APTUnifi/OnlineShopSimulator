package com.online.shop.controller;

import com.online.shop.model.Item;
import com.online.shop.repository.ShopRepository;
import com.online.shop.view.ShopView;

public class ShopController {

	private ShopView itemsView;
	private ShopRepository shopRepository;

	public ShopController(ShopView itemsView, ShopRepository shopRepository) {
		this.itemsView = itemsView;
		this.shopRepository = shopRepository;
	}

	public void allItems() {
		itemsView.updateItemsShop(shopRepository.findAllItems());
	}

	public void newItem(Item item) {
		Item retrievedItem = shopRepository.findItemByProductCode(item.getProductCode());

		if (item.getQuantity() <= 0) {
			throw new IllegalArgumentException("Negative amount: " + item.getQuantity());
		}

		if (retrievedItem != null) {
			shopRepository.modifyItemQuantity(retrievedItem, item.getQuantity());
			return;
		}

		shopRepository.storeItem(item);
		allItems();
	}

	public void removeItem(Item item) {
		if (shopRepository.findItemByProductCode(item.getProductCode()) == null) {
			return;
		}
		shopRepository.removeItem(item.getProductCode());
	}

	public void searchItem(String itemName) {
		Item retrievedItem = shopRepository.findItemByName(itemName);

		if (retrievedItem == null) {
			itemsView.errorLog("Item with name " + itemName + " doest not exists", null);
			return;
		}
		itemsView.showSearchResult(retrievedItem);
	} 

	public void modifyItemQuantity(Item item, int modifier) {
		if (modifier == 0) {
			return;
		}
		if (modifier + item.getQuantity() == 0) {
			shopRepository.removeItem(item.getProductCode());
			return;
		}
		if (modifier + item.getQuantity() < 0) {
			return;
		}
		shopRepository.modifyItemQuantity(item, modifier);
	}
}