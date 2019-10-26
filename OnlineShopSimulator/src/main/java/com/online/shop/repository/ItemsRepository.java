package com.online.shop.repository;

import java.util.List;

import com.online.shop.model.Item;

public interface ItemsRepository {
	public List<Item> findAll();

	public Item findByProductCode(String productCode);

	public Item findByName(String name);

	public void store(Item item);

	public void increaseQuantity(Item itemToAdd);

	public void remove(Item itemToRemove);

}
