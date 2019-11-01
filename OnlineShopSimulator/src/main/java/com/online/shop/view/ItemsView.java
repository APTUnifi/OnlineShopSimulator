package com.online.shop.view;

import java.util.List;

import com.online.shop.model.Item;

public interface ItemsView {
	void showItems(List<Item> items);

	void itemAdded(Item item);

	void itemQuantityAdded(Item item);

	void itemRemoved(Item item);

	void errorLog(String error, Item item);

	void showSearchResult(Item item);

}
