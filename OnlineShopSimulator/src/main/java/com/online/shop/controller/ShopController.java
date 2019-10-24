package com.online.shop.controller;

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

}
