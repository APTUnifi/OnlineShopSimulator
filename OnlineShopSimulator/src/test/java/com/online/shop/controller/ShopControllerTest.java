package com.online.shop.controller;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.model.Item;
import com.online.shop.repository.ShopRepository;
import com.online.shop.view.ShopView;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ShopControllerTest {

	private static final String PRODUCT_CODE = "1";
	private static final String ITEM_NAME = "battery";

	@Mock
	ShopRepository shopRepository;

	@Mock
	ShopView shopView;

	@InjectMocks
	ShopController shopController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAllItems() {
		// setup
		List<Item> items = Arrays.asList(new Item());
		when(shopRepository.findAllItems()).thenReturn(items);
		// exercise
		shopController.allItems();
		// verify
		verify(shopView).updateItemsShop(items);
	}


	@Test
	public void testNewItemWhenQuantityIsNegative() {
		// setup
		Item item = new Item(PRODUCT_CODE, ITEM_NAME, -1);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise + verify
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Negative amount: -1");
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testNewItemWhenQuantityIsZero() {
		// setup
		Item item = new Item(PRODUCT_CODE, ITEM_NAME, 0);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise + verify
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Negative amount: 0");
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testNewItemWhenItemDoesNotAlreadyExists() {
		// setup
		Item item = new Item(PRODUCT_CODE, ITEM_NAME);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise
		shopController.newItem(item);
		// verify
		InOrder inOrder = inOrder(shopRepository, shopView);
		inOrder.verify(shopRepository).storeItem(item);
	}

	@Test
	public void testNewItemWhenItemAlreadyExists() {
		// setup
		Item itemToAdd = new Item(PRODUCT_CODE, ITEM_NAME, 1);
		Item existingItem = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(existingItem);
		// exercise
		shopController.newItem(itemToAdd);
		// verify
		InOrder inOrder = inOrder(shopRepository, shopView);
		inOrder.verify(shopRepository).modifyItemQuantity(existingItem, 1);
	}

	@Test
	public void testRemoveItemWhenItemAlreadyExists() {
		// setup
		Item itemToRemove = new Item(PRODUCT_CODE, ITEM_NAME);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(itemToRemove);
		// exercise
		shopController.removeItem(itemToRemove);
		// verify
		InOrder inOrder = inOrder(shopRepository, shopView);
		inOrder.verify(shopRepository).removeItem(PRODUCT_CODE);

	}

	@Test
	public void testRemoveItemWhenItemDoesNotAlreadyExists() {
		// setup
		Item itemToRemove = new Item(PRODUCT_CODE, ITEM_NAME);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise
		shopController.removeItem(itemToRemove);
		// verify
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testSearchItemWhenItemAlreadyExists() {
		// setup
		Item itemToSearch = new Item("1", ITEM_NAME);
		when(shopRepository.findItemByName(ITEM_NAME)).thenReturn(itemToSearch);
		// exercise
		shopController.searchItem(itemToSearch.getName());
		// verify
		verify(shopView).showSearchResult(itemToSearch);
	}


	@Test
	public void testSearchItemWhenItemDoestNotExists() {
		// setup
		when(shopRepository.findItemByName(ITEM_NAME)).thenReturn(null);
		// exercise
		shopController.searchItem(ITEM_NAME);
		// verify
		verify(shopView).errorLogItem("Item with name does not exists" , ITEM_NAME);
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsLessThanItemQuantity() {
		// setup
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -1);
		// verify
		verify(shopRepository).modifyItemQuantity(itemToModify, -1);
	}

	@Test
	public void testModifyQuantityWhenModifierIsEqualToItemQuantity() {
		// Setup
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -2);
		// verify
		verify(shopRepository).removeItem(itemToModify.getProductCode());
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsZero() {
		// Setup
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, 0);
		// verify
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsGreaterThanItemQuantity() {
		// setup
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -3);
		// verify
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}
}