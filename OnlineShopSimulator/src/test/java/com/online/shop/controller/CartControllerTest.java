package com.online.shop.controller;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;
import com.online.shop.view.ItemsView;

import static org.assertj.core.api.Assertions.*;

public class CartControllerTest {

	@Mock
	ItemsView itemsView;

	@Mock
	ItemsRepository itemsRepository;

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
		cartController.addToCart(itemToAdd);
		// verify
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(itemToAdd)).isEqualTo(1);
		verify(itemsView).itemAddedToCart(itemToAdd);
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
		cartController.addToCart(itemToAdd);
		// verify
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(existingItem)).isEqualTo(2);
		verify(itemsView).updateItemsCart(cartController.cartItems());
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
		cartController.addToCart(itemToAdd);
		// verify
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(existingItem)).isEqualTo(3);
		verifyNoMoreInteractions(itemsView);
	}

	@Test
	public void testRemoveItemFromCartWhenItemQuantityIsEqualToOne() {
		// setup
		Item itemToRemove = new Item("1", "test1");
		List<Item> items = new ArrayList<>();
		items.add(itemToRemove);
		cartController.setCart(new Cart(items));
		// exercise
		cartController.removeFromCart(itemToRemove);
		// verify
		assertThat(cartController.cartSize()).isEqualTo(0);
		assertThat(cartController.findItemQuantity(itemToRemove)).isEqualTo(0);
		verify(itemsView).itemRemovedFromCart(itemToRemove);
	}

	@Test
	public void testRemoveItemFromCartWhenQuantityIsAboveOne() {
		// setup
		Item itemToRemove = new Item("1", "test1", 2);
		List<Item> items = new ArrayList<>();
		items.add(itemToRemove);
		cartController.setCart(new Cart(items));
		// exercise
		cartController.removeFromCart(itemToRemove);
		// verify
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(itemToRemove)).isEqualTo(1);
		verify(itemsView).updateItemsCart(cartController.cartItems());
	}

	@Test
	public void testPurchaseItemsShouldRemoveItemsFromShop() {
		// setup
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item("1", "test1", 1);
		Item secondExistingItem = new Item("2", "test2", 2);
		items.add(new Item("1", "test1", 1));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items));
		when(itemsRepository.findByProductCode("1")).thenReturn(firstExistingItem);
		when(itemsRepository.findByProductCode("2")).thenReturn(secondExistingItem);
		// exercise
		cartController.completePurchase();
		// verify
		verify(itemsRepository).remove("1");
		verify(itemsRepository).remove("2");
	}

	@Test
	public void testPurchaseItemsShouldModifyItemsQuantityFromShop() {
		// setup
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item("1", "test1", 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item("1", "test1", 1));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items));
		when(itemsRepository.findByProductCode("1")).thenReturn(firstExistingItem);
		when(itemsRepository.findByProductCode("2")).thenReturn(secondExistingItem);
		// exercise
		cartController.completePurchase();
		// verify
		verify(itemsRepository).modifyQuantity(firstExistingItem, 1);
		verify(itemsRepository).modifyQuantity(secondExistingItem, 2);
	}

	@Test
	public void testPurchaseItemsShouldClearCartViewList() {
		// setup
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item("1", "test1", 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item("1", "test1", 1));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items));
		when(itemsRepository.findByProductCode("1")).thenReturn(firstExistingItem);
		when(itemsRepository.findByProductCode("2")).thenReturn(secondExistingItem);
		// exercise
		cartController.completePurchase();
		// verify
		verify(itemsView).showItemsCart(null);
	}

	@Test
	public void testPurchaseItemsShouldUpdateShopViewList() {
		// setup
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item("1", "test1", 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item("1", "test1", 1));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items));
		when(itemsRepository.findByProductCode("1")).thenReturn(firstExistingItem);
		when(itemsRepository.findByProductCode("2")).thenReturn(secondExistingItem);
		// exercise
		cartController.completePurchase();
		// verify
		verify(itemsView).showItemsShop(itemsRepository.findAll());
	}

	@Test
	public void testPurchaseItemsShouldClearCartArrayList() {
		// setup
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item("1", "test1", 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item("1", "test1", 1));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items));
		when(itemsRepository.findByProductCode("1")).thenReturn(firstExistingItem);
		when(itemsRepository.findByProductCode("2")).thenReturn(secondExistingItem);
		// exercise
		cartController.completePurchase();
		// verify
		assertThat(cartController.cartItems()).isEmpty();
	}

	@Test
	public void testPurchaseItemsShouldSaveCartDetails() {
		// setup
		List<Item> items = new ArrayList<>();
		Cart cart = spy(new Cart());
		Item firstExistingItem = new Item("1", "test1", 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item("1", "test1", 1));
		items.add(new Item("2", "test2", 2));
		cart.setItems(items);
		cartController.setCart(cart);
		when(itemsRepository.findByProductCode("1")).thenReturn(firstExistingItem);
		when(itemsRepository.findByProductCode("2")).thenReturn(secondExistingItem);
		// exercise
		cartController.completePurchase();
		// verify
		InOrder inOrder = Mockito.inOrder(itemsRepository, cart);
		inOrder.verify(itemsRepository).saveCart(cart);
		inOrder.verify(cart).setItems(new ArrayList<Item>());

	}
}