package com.online.shop.repository;

import java.util.List;

import com.online.shop.model.Cart;
import com.online.shop.model.Item;

//TODO change ItemsRepository name to something like ShopRepository and methods referring to Items 
public interface ItemsRepository {
	public List<Item> findAll();

	public Item findByProductCode(String productCode);

	public Item findByName(String name);

	public void store(Item itemToAdd);

	public void remove(String productCode);

	public void modifyQuantity(Item itemToBeModified, int modifier);
	
	//TODO implements cart methods
	public void storeCart(Cart cartToStore);
	
	public Cart findCart(String label, String data);
	
	public List<Cart> findAllCarts();
	
	public void removeCart(String label, String data);
}