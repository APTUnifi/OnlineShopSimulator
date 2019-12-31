package com.online.shop.controller;

import com.online.shop.model.Item;
import com.online.shop.repository.ShopRepository;
import com.online.shop.view.ShopView;

public class ShopController {

	private ShopView itemsView;
	private ShopRepository itemsRepository;

	public ShopController(ShopView itemsView, ShopRepository itemsRepository) {
		this.itemsView = itemsView;
		this.itemsRepository = itemsRepository;
	}

	public void allItems() {
		itemsView.updateItemsShop(itemsRepository.findAllItems());
	}

	public void newItem(Item item) {
		Item retrievedItem = itemsRepository.findItemByProductCode(item.getProductCode());

		if (item.getQuantity() <= 0) {
			throw new IllegalArgumentException("Negative amount: " + item.getQuantity());
		}

		if (retrievedItem != null) {
			itemsRepository.modifyItemQuantity(retrievedItem, item.getQuantity());
			return;
		}

		itemsRepository.storeItem(item);
		allItems();
	}

	public void removeItem(Item item) {
		if (itemsRepository.findItemByProductCode(item.getProductCode()) == null) {
			return;
		}
		itemsRepository.removeItem(item.getProductCode());
	}

	public void searchItem(String itemName) {
		Item retrievedItem = itemsRepository.findItemByName(itemName);

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
			itemsRepository.removeItem(item.getProductCode());
			return;
		}
		if (modifier + item.getQuantity() < 0) {
			return;
		}
		itemsRepository.modifyItemQuantity(item, modifier);
	}
}