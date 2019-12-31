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
	ShopRepository itemsRepository;

	@Mock
	ShopView itemsView;

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
		when(itemsRepository.findAllItems()).thenReturn(items);
		// exercise
		shopController.allItems();
		// verify
		verify(itemsView).updateItemsShop(items);
	}

	
	@Test
	public void testNewItemWhenQuantityIsNegative() {
		// setup
		Item item = new Item(PRODUCT_CODE, ITEM_NAME, -1);
		when(itemsRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise + verify
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Negative amount: -1");
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testNewItemWhenQuantityIsZero() {
		// setup
		Item item = new Item(PRODUCT_CODE, ITEM_NAME, 0);
		when(itemsRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise + verify
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Negative amount: 0");
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testNewItemWhenItemDoesNotAlreadyExists() {
		// setup
		Item item = new Item(PRODUCT_CODE, ITEM_NAME);
		when(itemsRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise
		shopController.newItem(item);
		// verify
		InOrder inOrder = inOrder(itemsRepository, itemsView);
		inOrder.verify(itemsRepository).storeItem(item);
	}

	@Test
	public void testNewItemWhenItemAlreadyExists() {
		// setup
		Item itemToAdd = new Item(PRODUCT_CODE, ITEM_NAME, 1);
		Item existingItem = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		when(itemsRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(existingItem);
		// exercise
		shopController.newItem(itemToAdd);
		// verify
		InOrder inOrder = inOrder(itemsRepository, itemsView);
		inOrder.verify(itemsRepository).modifyItemQuantity(existingItem, 1);
	}

	@Test
	public void testRemoveItemWhenItemAlreadyExists() {
		// setup
		Item itemToRemove = new Item(PRODUCT_CODE, ITEM_NAME);
		when(itemsRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(itemToRemove);
		// exercise
		shopController.removeItem(itemToRemove);
		// verify
		InOrder inOrder = inOrder(itemsRepository, itemsView);
		inOrder.verify(itemsRepository).removeItem(PRODUCT_CODE);

	}

	@Test
	public void testRemoveItemWhenItemDoesNotAlreadyExists() {
		// setup
		Item itemToRemove = new Item(PRODUCT_CODE, ITEM_NAME);
		when(itemsRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise
		shopController.removeItem(itemToRemove);
		// verify
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testSearchItemWhenItemAlreadyExists() {
		// setup
		Item itemToSearch = new Item("1", ITEM_NAME);
		when(itemsRepository.findItemByName(ITEM_NAME)).thenReturn(itemToSearch);
		// exercise
		shopController.searchItem(itemToSearch.getName());
		// verify
		verify(itemsView).showSearchResult(itemToSearch);
	}

	@Test
	public void testSearchItemWhenItemDoestNotExists() {
		// setup
		when(itemsRepository.findItemByName(ITEM_NAME)).thenReturn(null);
		// exercise
		shopController.searchItem(ITEM_NAME);
		// verify
		verify(itemsView).errorLog("Item with name battery doest not exists", null);
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsLessThanItemQuantity() {
		// setup
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -1);
		// verify
		verify(itemsRepository).modifyItemQuantity(itemToModify, -1);
	}

	@Test
	public void testModifyQuantityWhenModifierIsEqualToItemQuantity() {
		// Setup
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -2);
		// verify
		verify(itemsRepository).removeItem(itemToModify.getProductCode());
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsZero() {
		// Setup
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, 0);
		// verify
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsGreaterThanItemQuantity() {
		// setup
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -3);
		// verify
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}
}