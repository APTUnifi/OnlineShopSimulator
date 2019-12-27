package com.online.shop.view;

import java.util.List;

import javax.swing.DefaultListModel;

import com.online.shop.model.Cart;

public interface HistoryView {

	void showHistory(List<Cart> carts);
	void showItemsCart(Cart cart);
	void removeCart(Cart cart);

}
