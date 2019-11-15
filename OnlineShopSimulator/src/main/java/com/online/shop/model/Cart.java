package com.online.shop.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
	private List<Item> items;

	public Cart() {
		items = new ArrayList<>();
	}

	public Cart(List<Item> items) {
		this.items = items;
	}

	public List<Item> getItems() {
		return items;
	}
	
	public void setItems(List<Item> items) {
		this.items = items;
	}
}
