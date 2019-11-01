package com.online.shop.controller;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;
import com.online.shop.view.ItemsView;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ShopControllerTest {

	private static final String PRODUCT_CODE = "1";

	private static final String ITEM_NAME = "battery";

	@Mock
	ItemsRepository itemsRepository;

	@Mock
	ItemsView itemsView;

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
		when(itemsRepository.findAll()).thenReturn(items);
		// exercise
		shopController.allItems();
		// verify
		verify(itemsView).showItems(items);
	}

	@Test 
	public void testNewItemWhenQuantityIsNegative() {
		// setup
		Item item = new Item(PRODUCT_CODE, -1);
		when(itemsRepository.findByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise + verify
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Negative amount: -1");
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testNewItemWhenQuantityIsZero() {
		// setup
		Item item = new Item(PRODUCT_CODE, 0);
		when(itemsRepository.findByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise + verify
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Negative amount: 0");
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testNewItemWhenItemDoesNotAlreadyExists() {
		// setup
		Item item = new Item(PRODUCT_CODE, 1);
		when(itemsRepository.findByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise
		shopController.newItem(item);
		// verify
		InOrder inOrder = inOrder(itemsRepository, itemsView);
		inOrder.verify(itemsRepository).store(item);
		inOrder.verify(itemsView).itemAdded(item);
	}

	@Test
	public void testNewItemWhenItemAlreadyExists() {
		// setup
		Item itemToAdd = new Item(PRODUCT_CODE, 1);
		Item existingItem = new Item(PRODUCT_CODE, 2);
		when(itemsRepository.findByProductCode(PRODUCT_CODE)).thenReturn(existingItem);
		// exercise
		shopController.newItem(itemToAdd);
		// verify
		InOrder inOrder = inOrder(itemsRepository, itemsView);
		inOrder.verify(itemsRepository).modifyQuantity(existingItem, 1);
		inOrder.verify(itemsView).itemQuantityAdded(existingItem);
	}

	@Test
	public void testRemoveItemWhenItemAlreadyExists() {
		// setup
		Item itemToRemove = new Item(PRODUCT_CODE, 1);
		when(itemsRepository.findByProductCode(PRODUCT_CODE)).thenReturn(itemToRemove);
		// exercise
		shopController.removeItem(itemToRemove);
		// verify
		InOrder inOrder = inOrder(itemsRepository, itemsView);
		inOrder.verify(itemsRepository).remove(PRODUCT_CODE);
		inOrder.verify(itemsView).itemRemoved(itemToRemove);

	}

	@Test
	public void testRemoveItemWhenItemDoesNotAlreadyExists() {
		// setup
		Item itemToRemove = new Item(PRODUCT_CODE, 1);
		when(itemsRepository.findByProductCode(PRODUCT_CODE)).thenReturn(null);
		// exercise
		shopController.removeItem(itemToRemove);
		// verify
		verify(itemsView).errorLog("Item with product code 1 does not exists", itemToRemove);
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testSearchItemWhenItemAlreadyExists() {
		// setup
		Item itemToSearch = new Item(ITEM_NAME);
		when(itemsRepository.findByName(ITEM_NAME)).thenReturn(itemToSearch);
		// exercise
		shopController.searchItem(itemToSearch);
		// verify
		verify(itemsView).showSearchResult(itemToSearch);
	}

	@Test
	public void testSearchItemWhenItemDoestNotExists() {
		// setup
		Item itemToSearch = new Item(ITEM_NAME);
		when(itemsRepository.findByName(ITEM_NAME)).thenReturn(null);
		// exercise
		shopController.searchItem(itemToSearch);
		// verify
		verify(itemsView).errorLog("Item with name battery doest not exists", itemToSearch);
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsLessThanItemQuantity() {
		// setup
		Item itemToModify = new Item(PRODUCT_CODE, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -1);
		// verify
		verify(itemsRepository).modifyQuantity(itemToModify, -1);
	}

	@Test
	public void testModifyQuantityWhenModifierIsEqualToItemQuantity() {
		// Setup
		Item itemToModify = new Item(PRODUCT_CODE, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -2);
		// verify
		verify(itemsRepository).remove(itemToModify.getProductCode());
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsGreaterThanItemQuantity() {
		// setup
		Item itemToModify = new Item(PRODUCT_CODE, 2);
		// exercise
		shopController.modifyItemQuantity(itemToModify, -3);
		// verify
		verify(itemsView).errorLog("Item has quantity 2, can't remove more items", itemToModify);
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}
}
