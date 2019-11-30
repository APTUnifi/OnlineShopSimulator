package com.online.shop.model;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate; // import the LocalDate class

public class Cart {
	private List<Item> items;
	private String date;
	private String label;


	public Cart() {
		items = new ArrayList<>();
		date = LocalDate.now().toString(); // Create a date object
		label = "";
	}

	public Cart(List<Item> items, String label) {
		this.items = items;
		this.label = label;
	}

	public List<Item> getItems() {
		return items;
	}
	
	public void setItems(List<Item> items) {
		this.items = items;
	}
}

