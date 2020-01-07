package com.online.shop.controller;

import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.ShopRepository;
import com.online.shop.view.HistoryView;
import com.online.shop.view.ShopView;

import static org.assertj.core.api.Assertions.*;

public class CartControllerTest {

	private static final int EXISTING_QUANTITY = 3;
	private static final String CART_LABEL = "test";
	private static final String ITEM_NAME = "test1";
	private static final String ITEM_PRODUCT_CODE = "1";

	@Mock
	ShopView shopView;
	
	@Mock
	HistoryView historyView;

	@Mock
	ShopRepository shopRepository;

	@InjectMocks
	CartController cartController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	@Test
	public void testFindItemQuantityWhenItemIsNotPresent() {
		Item item = new Item(ITEM_PRODUCT_CODE, ITEM_NAME);
		cartController.setCart(new Cart());
		assertThat(cartController.findItemQuantity(item)).isEqualTo(0);
	}

	@Test
	public void testAddItemToCartWhenItemIsNotPresent() {
		Item itemToAdd = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 3);
		cartController.setCart(new Cart());

		cartController.addToCart(itemToAdd);
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(itemToAdd)).isEqualTo(1);
		verify(shopView).itemAddedToCart(itemToAdd);
	}

	@Test
	public void testAddItemToCartWhenItemIsAlreadyPresentWithQuantityBelowMaxQuantity() {
		Item itemToAdd = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, EXISTING_QUANTITY);
		Item existingCartItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME);
		List<Item> items = new ArrayList<>();

		items.add(existingCartItem);
		cartController.setCart(new Cart(items, CART_LABEL));
		cartController.addToCart(itemToAdd);
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(existingCartItem)).isEqualTo(EXISTING_QUANTITY - 1);

		verify(shopView).updateItemsCart(cartController.cartItems());
	}

	@Test
	public void testAddItemToCartWhenItemIsAlreadyPresentWithQuantityAboveMaxQuantity() {
		Item itemToAdd = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, EXISTING_QUANTITY);
		Item existingCartItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, EXISTING_QUANTITY);
		List<Item> items = new ArrayList<>();

		items.add(existingCartItem);
		cartController.setCart(new Cart(items, CART_LABEL));
		cartController.addToCart(itemToAdd);

		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(existingCartItem)).isEqualTo(EXISTING_QUANTITY);
		verify(shopView).errorLogItem("Can not add more this item", itemToAdd.getName());;
	}

	@Test
	public void testRemoveItemFromCartWhenItemQuantityIsEqualToOne() {
		Item itemToRemove = new Item(ITEM_PRODUCT_CODE, ITEM_NAME);
		List<Item> items = new ArrayList<>();
		items.add(itemToRemove);

		cartController.setCart(new Cart(items, CART_LABEL));
		cartController.removeFromCart(itemToRemove);

		assertThat(cartController.cartSize()).isEqualTo(0);
		assertThat(cartController.findItemQuantity(itemToRemove)).isEqualTo(0);
		verify(shopView).itemRemovedFromCart(itemToRemove);
	}

	@Test
	public void testRemoveItemFromCartWhenQuantityIsAboveOne() {
		Item itemToRemove = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, EXISTING_QUANTITY);
		List<Item> items = new ArrayList<>();
		items.add(itemToRemove);

		cartController.setCart(new Cart(items, CART_LABEL));
		cartController.removeFromCart(itemToRemove);
		assertThat(cartController.cartSize()).isEqualTo(1);
		assertThat(cartController.findItemQuantity(itemToRemove)).isEqualTo(EXISTING_QUANTITY - 1);

		verify(shopView).updateItemsCart(cartController.cartItems());
	}

	@Test
	public void testPurchaseItemsShouldRemoveItemsFromShop() {
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME);
		Item secondExistingItem = new Item("2", "test2", 2);
		items.add(new Item(ITEM_PRODUCT_CODE, ITEM_NAME));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items, CART_LABEL));
		when(shopRepository.findItemByProductCode(ITEM_PRODUCT_CODE)).thenReturn(firstExistingItem);
		when(shopRepository.findItemByProductCode("2")).thenReturn(secondExistingItem);
		cartController.completePurchase(CART_LABEL);
		verify(shopRepository).removeItem(ITEM_PRODUCT_CODE);
		verify(shopRepository).removeItem("2");
	}

	@Test
	public void testPurchaseItemsShouldModifyItemsQuantityFromShop() {
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 1));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items, CART_LABEL));
		when(shopRepository.findItemByProductCode(ITEM_PRODUCT_CODE)).thenReturn(firstExistingItem);
		when(shopRepository.findItemByProductCode("2")).thenReturn(secondExistingItem);

		cartController.completePurchase(CART_LABEL);

		verify(shopRepository).modifyItemQuantity(firstExistingItem, -1);
		verify(shopRepository).modifyItemQuantity(secondExistingItem, -2);
	}

	@Test
	public void testPurchaseItemsShouldThrowErrorWhenItemDoesNotExists() {
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 2);
		Item notExistingItem = new Item("2", "test2", 2);
		items.add(new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 1));
		items.add(notExistingItem);
		cartController.setCart(new Cart(items, CART_LABEL));
		when(shopRepository.findItemByProductCode(ITEM_PRODUCT_CODE)).thenReturn(firstExistingItem);
		when(shopRepository.findItemByProductCode("2")).thenReturn(null);
		cartController.completePurchase(CART_LABEL);
		verify(shopView).errorLog("Item/s not found", Arrays.asList(notExistingItem));
	}

	@Test
	public void testPurchaseItemsShouldUpdateShopViewList() {
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item(ITEM_PRODUCT_CODE, ITEM_NAME));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items, CART_LABEL));
		when(shopRepository.findItemByProductCode(ITEM_PRODUCT_CODE)).thenReturn(firstExistingItem);
		when(shopRepository.findItemByProductCode("2")).thenReturn(secondExistingItem);
		cartController.completePurchase(CART_LABEL);
		verify(shopView).updateItemsShop(shopRepository.findAllItems());
	}

	@Test
	public void testPurchaseItemsShouldUpdateCartViewList() {
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item(ITEM_PRODUCT_CODE, ITEM_NAME));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items, CART_LABEL));
		when(shopRepository.findItemByProductCode(ITEM_PRODUCT_CODE)).thenReturn(firstExistingItem);
		when(shopRepository.findItemByProductCode("2")).thenReturn(secondExistingItem);
		cartController.completePurchase(CART_LABEL);
		verify(shopView).updateItemsCart(shopRepository.findAllItems());
	}

	@Test
	public void testPurchaseItemsShouldClearCartArrayList() {
		List<Item> items = new ArrayList<>();
		Item firstExistingItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item(ITEM_PRODUCT_CODE, ITEM_NAME));
		items.add(new Item("2", "test2", 2));
		cartController.setCart(new Cart(items, CART_LABEL));
		when(shopRepository.findItemByProductCode(ITEM_PRODUCT_CODE)).thenReturn(firstExistingItem);
		when(shopRepository.findItemByProductCode("2")).thenReturn(secondExistingItem);
		cartController.completePurchase(CART_LABEL);
		assertThat(cartController.cartItems()).isEmpty();
	}

	@Test
	public void testPurchaseItemsShouldSaveCartDetails() {
		List<Item> items = new ArrayList<>();
		Cart cart = spy(new Cart());
		Item firstExistingItem = new Item(ITEM_PRODUCT_CODE, ITEM_NAME, 2);
		Item secondExistingItem = new Item("2", "test2", 3);
		items.add(new Item(ITEM_PRODUCT_CODE, ITEM_NAME));
		items.add(new Item("2", "test2", 2));
		cart.setItems(items);
		cartController.setCart(cart);
		when(shopRepository.findItemByProductCode(ITEM_PRODUCT_CODE)).thenReturn(firstExistingItem);
		when(shopRepository.findItemByProductCode("2")).thenReturn(secondExistingItem);
		cartController.completePurchase(CART_LABEL);
		InOrder inOrder = inOrder(shopRepository, cart);
		inOrder.verify(shopRepository).storeCart(cart);
		inOrder.verify(cart).setItems(new ArrayList<Item>());
	}

	@Test
	public void testAllCarts() {
		List<Cart> carts = Arrays.asList(new Cart());
		when(shopRepository.findAllCarts()).thenReturn(carts);
		cartController.allCarts();
		verify(historyView).showHistory(carts);
	}

	@Test
	public void testAllItemsCarts() {
		Cart cart = new Cart();
		List<Cart> carts = Arrays.asList(cart);
		when(shopRepository.findAllCarts()).thenReturn(carts);
		cartController.allItemsCart(cart);
		verify(historyView).showItemsCart(cart);
	}

	@Test
	public void testRemoveCartWhenCartExists() {
		List<Item> items = new ArrayList<>();
		Item item = new Item(ITEM_PRODUCT_CODE, ITEM_NAME);
		items.add(item);
		Cart cartToRemove = new Cart(items, CART_LABEL);
		cartController.setCart(cartToRemove);
		when(shopRepository.findCart(LocalDate.now().toString(), CART_LABEL)).thenReturn(cartToRemove);
		cartController.removeCart(cartToRemove);
		InOrder inOrder = inOrder(shopRepository, historyView);
		inOrder.verify(shopRepository).removeCart(LocalDate.now().toString(), CART_LABEL);
		inOrder.verify(historyView).removeCart(cartToRemove);
	}

	@Test
	public void testRemoveCartWhenCartDoesNotExists() {
		List<Item> items = new ArrayList<>();
		Item item = new Item(ITEM_PRODUCT_CODE, ITEM_NAME);
		items.add(item);
		Cart cartToRemove = new Cart(items, CART_LABEL);
		when(shopRepository.findCart(LocalDate.now().toString(), CART_LABEL)).thenReturn(null);
		cartController.removeCart(cartToRemove);
		verify(historyView).errorLogCart("Cart not found" , cartToRemove.getLabel());
		verifyNoMoreInteractions(ignoreStubs(shopView));
	}
	
	@Test
	public void testCompletePurchaseShouldThrowErrorWhenNameCartAlreadyExists() {
		List<Item> items = new ArrayList<>();
		Cart cartToAdd = new Cart(CART_LABEL,LocalDate.now().toString(), items);
		Cart cartExist = new Cart(CART_LABEL, LocalDate.now().toString(),items);
		shopRepository.storeCart(cartExist);
		cartController.setCart(cartToAdd);
		when(shopRepository.findAllCarts()).thenReturn(Arrays.asList(cartExist));
		cartController.completePurchase(CART_LABEL);
		//verify(itemsRepository).storeCart(cartExist);
		verify(shopView).errorLogCart("Cart with this label already exists ", cartExist.getLabel());
		verifyNoMoreInteractions(ignoreStubs(shopView));
	}

}