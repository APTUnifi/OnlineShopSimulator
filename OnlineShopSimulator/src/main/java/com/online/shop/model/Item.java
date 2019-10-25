package com.online.shop.model;

public class Item {
	private String productCode;
	private int quantity;

	// Used by Unit Testing
	public Item() {

	}

	public Item(String productCode, int quantity) {
		this.productCode = productCode;
		this.quantity = quantity;
	}

	public String getProductCode() {
		return productCode;
	}

	public int getQuantity() {
		return quantity;
	}
}
