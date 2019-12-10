package com.online.shop.view;

import java.util.List;

import com.online.shop.model.Cart;

public interface HistoryView {

	public void showHistory(List<Cart> carts);
	public void showItemsCart(Cart cart);
	public void removeCart(Cart cart);
}
