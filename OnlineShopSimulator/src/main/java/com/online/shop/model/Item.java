package com.online.shop.model;

import org.testcontainers.shaded.org.apache.commons.lang.builder.EqualsBuilder;

public class Item {
	
	private String productCode;
	private int quantity;

	public Item(String productCode, int quantity) {
		this.productCode = productCode;
		this.quantity = quantity;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;

	    if (o == null || getClass() != o.getClass()) return false;

	    Item student = (Item) o;

	    return new EqualsBuilder()
	    		.append(productCode, student.productCode)
	            .append(quantity, student.quantity)
	            .isEquals();
	}
}