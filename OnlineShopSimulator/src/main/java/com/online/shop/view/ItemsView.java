
package com.online.shop.view;

import java.util.List;

import com.online.shop.model.Cart;
import com.online.shop.model.Item;

public interface ItemsView {
  
	void itemAdded(Item item);
	void itemQuantityAdded(Item item);
	void itemRemoved(Item item);
  
	void showItemsShop(List<Item> items);
	void errorLog(String error, List<Item> items);
	void showSearchResult(Item item);
	void itemAddedToCart(Item item); 
	void itemRemovedFromCart(Item item);
	void updateItemsCart(List<Item> items);
	void updateItemsShop(List<Item> items);

}
