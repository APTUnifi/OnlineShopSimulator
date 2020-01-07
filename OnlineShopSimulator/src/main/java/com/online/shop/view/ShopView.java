package com.online.shop.view;

import java.util.List;

import com.online.shop.model.Item;

public interface ShopView {
  
	void showItemsShop(List<Item> items);
	void errorLog(String error, List<Item> items);
	void errorLogItem(String error, String Item);
	void errorLogCart(String error, String cart);
	void showSearchResult(Item item);
	void itemAddedToCart(Item item); 
	void itemRemovedFromCart(Item item);
	void updateItemsCart(List<Item> items);
	void updateItemsShop(List<Item> items);
}
