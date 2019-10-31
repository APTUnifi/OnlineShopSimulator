package com.online.shop.model;

public class Item {
	private String productCode;
	private String name;
	private int quantity;

	// Used by Unit Testing
	public Item() {

	}

	public Item(String productCode, int quantity) {
		this.productCode = productCode;
		this.quantity = quantity;
	}

	// Used by Unit Testing
	public Item(String name) {
		this.name = name;
	}

	public String getProductCode() {
		return productCode;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getName() {
		return name;
	}
}
