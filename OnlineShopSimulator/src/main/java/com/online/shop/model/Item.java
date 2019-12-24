package com.online.shop.model;

import java.util.Objects;

public class Item {

	private String productCode;
	private String name;
	private int quantity;

	// Used by Unit Testing
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
	
	//TODO update with the other methods
	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof Item){
	        final Item other = (Item) obj;
	        return Objects.equals(productCode, other.productCode)
	            && Objects.equals(name, other.name);
	    } else{
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

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public void setName(String name) {
		this.name = name;
	}
}