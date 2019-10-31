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
		itemsView.showItems(itemsRepository.findAll());
	}

	public void newItem(Item item) {
		Item retrievedItem = itemsRepository.findByProductCode(item.getProductCode());

		if (item.getQuantity() <= 0) {
			throw new IllegalArgumentException("Negative amount: " + item.getQuantity());
		}

		if (retrievedItem != null) {
			itemsRepository.modifyQuantity(retrievedItem, item.getQuantity());
			itemsView.itemQuantityAdded(retrievedItem);
			return;
		}

		itemsRepository.store(item);
		itemsView.itemAdded(item);
	}

	public void removeItem(Item item) {

		if (itemsRepository.findByProductCode(item.getProductCode()) == null) {
			itemsView.errorLog("Item with product code " + item.getProductCode() + " does not exists", item);
			return;
		}
		itemsRepository.remove(item.getProductCode());
		itemsView.itemRemoved(item);
	}

	public void searchItem(Item item) {
		Item retrievedItem = itemsRepository.findByName(item.getName());

		if (retrievedItem == null) {
			itemsView.errorLog("Item with name " + item.getName() + " doest not exists", item);
			return;
		}

		itemsView.showSearchResult(item);
	}

	public void modifyItemQuantity(Item item, int modifier) {
		// TODO Information is already obtained from database, is the control necessary?
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
