package com.online.shop.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.view.ItemsView;

import static org.assertj.core.api.Assertions.*;

public class CartControllerTest {

	@Mock
	ItemsView itemsView;

	@InjectMocks
	CartController cartController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testFindItemQuantityWhenItemIsNotPresent() {
		// setup
		Item item = new Item("1", "test1");
		cartController.setCart(new Cart());
		// exercise + verify
		assertThat(cartController.findItemQuantity(item)).isEqualTo(0);
	}

	@Test
	public void testAddItemToCartWhenItemIsNotPresent() {
		// setup
		Item itemToAdd = new Item("1", "test1", 3);
		cartController.setCart(new Cart());
		// exercise
		cartController.add(itemToAdd);
		// verify
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(itemToAdd)).isEqualTo(1);
		verify(itemsView).addItemToCart(itemToAdd);
	}

	@Test
	public void testAddItemToCartWhenItemIsAlreadyPresentWithQuantityBelowMaxQuantity() {
		// setup
		Item itemToAdd = new Item("1", "test1", 3);
		Item existingItem = new Item("1", "test1", 1);
		List<Item> items = new ArrayList<>();
		items.add(existingItem);
		cartController.setCart(new Cart(items));
		// exercise
		cartController.add(itemToAdd);
		// verify
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(existingItem)).isEqualTo(2);
		verify(itemsView).addItemToCart(itemToAdd);
	}

	@Test
	public void testAddItemToCartWhenItemIsAlreadyPresentWithQuantityAboveMaxQuantity() {
		// setup
		Item itemToAdd = new Item("1", "test1", 3);
		Item existingItem = new Item("1", "test1", 3);
		List<Item> items = new ArrayList<>();
		items.add(existingItem);
		cartController.setCart(new Cart(items));
		// exercise
		cartController.add(itemToAdd);
		// verify
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(existingItem)).isEqualTo(3);
		verifyNoMoreInteractions(itemsView);
	}

}
