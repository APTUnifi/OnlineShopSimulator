package com.online.shop.repository;

import java.util.List;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;

public interface ShopRepository {

	public List<Item> findAllItems();

	public Item findItemByProductCode(String productCode);

	public Item findItemByName(String name);

	public void storeItem(Item itemToAdd);

	public void removeItem(String productCode);

	public void modifyItemQuantity(Item itemToBeModified, int modifier);

	public List<Cart> findAllCarts();

	public Cart findCart(String date, String label);

	public void storeCart(Cart cartToStore);

	public void removeCart(String date, String label);

}