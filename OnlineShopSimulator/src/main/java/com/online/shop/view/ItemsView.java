
package com.online.shop.view;

import java.util.List;

import com.online.shop.model.Item;

public interface ItemsView {
  
	void showItemsShop(List<Item> items);
	void showItemsCart(List<Item> items);
	void errorLog(String error, Item item);
	void showSearchResult(Item item);
	void itemAddedToCart(Item item);
	void itemRemovedFromCart(Item item);
	void updateItemsCart(List<Item> items);
}
