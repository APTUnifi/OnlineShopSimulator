package com.online.shop.model;

import java.util.Objects;

import org.testcontainers.shaded.org.apache.commons.lang.builder.EqualsBuilder;

public class Item {
	
	private String productCode;
	private String name;
	private int quantity;
  
  	// Used by Unit Testing
  	public Item() {

	}
  
  	// Used by Unit Testing
	public Item(String productCode, int quantity) {
		this.productCode = productCode;
		this.quantity = quantity;
	}
  
  	// Used by Unit Testing
	public Item(String name) {
		this.name = name;
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
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;

	    if (o == null || getClass() != o.getClass()) return false;

	    Item item = (Item) o;

	    return new EqualsBuilder()
	    		.append(productCode, item.productCode)
	    		.append(name, item.name)
	            .isEquals();
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
	
}
