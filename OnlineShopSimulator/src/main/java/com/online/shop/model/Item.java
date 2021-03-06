package com.online.shop.model;

import java.util.Objects;

public class Item {

	private String productCode;
	private String name;
	private int quantity;

	public Item() {
	}

	public Item(String productCode, String name) {
		this.productCode = productCode;
		this.name = name;
		this.quantity = 1;
	}

	public Item(String productCode, String name, int quantity) {
		this.productCode = productCode;
		this.name = name;
		this.quantity = quantity;
	}

	public Item(Item item) {
		this.productCode = item.getProductCode();
		this.name = item.getName();
		this.quantity = item.getQuantity();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Item) {
			final Item other = (Item) obj;
			return Objects.equals(productCode, other.productCode) && Objects.equals(name, other.name);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(productCode, name);
	}

	public String getProductCode() {
		return productCode;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return name + " : quantity " + quantity;
	}
}