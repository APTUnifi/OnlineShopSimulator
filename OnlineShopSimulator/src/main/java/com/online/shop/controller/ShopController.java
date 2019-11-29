package com.online.shop.controller;

import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;
import com.online.shop.view.ItemsView;

public class ShopController {

	private ItemsView itemsView;
	private ItemsRepository itemsRepository;

	public ShopController(ItemsView itemsView, ItemsRepository itemsRepository) {
		this.itemsView = itemsView;
		this.itemsRepository = itemsRepository;
	}


	public void allItems() {
		itemsView.showItemsShop(itemsRepository.findAll());
	}

	public void newItem(Item item) {
		Item retrievedItem = itemsRepository.findByProductCode(item.getProductCode());

		if (item.getQuantity() <= 0) {
			throw new IllegalArgumentException("Negative amount: " + item.getQuantity());
		}

		if (retrievedItem != null) {
			itemsRepository.modifyQuantity(retrievedItem, item.getQuantity());
			return;
		}

		itemsRepository.store(item);
		allItems();
	}
	
	public void removeItem(Item item) {
		if (itemsRepository.findByProductCode(item.getProductCode()) == null) {
			itemsView.errorLog("Item with product code " + item.getProductCode() + " does not exists", item);
			return;
		}
		itemsRepository.remove(item.getProductCode());
	}

	public void searchItem(String itemName) {
		Item retrievedItem = itemsRepository.findByName(itemName);

		if (retrievedItem == null) {
			itemsView.errorLog("Item with name " + itemName + " doest not exists", null);
			return;
		}

		itemsView.showSearchResult(retrievedItem);
	} 


	public void modifyItemQuantity(Item item, int modifier) {
		if (modifier + item.getQuantity() == 0) {
			itemsRepository.remove(item.getProductCode());
			return;
		}
		if (modifier + item.getQuantity() < 0) {
			itemsView.errorLog("Item has quantity " + item.getQuantity() + ", can't remove more items", item);
			return;
		}
		itemsRepository.modifyQuantity(item, modifier);
	}

}
