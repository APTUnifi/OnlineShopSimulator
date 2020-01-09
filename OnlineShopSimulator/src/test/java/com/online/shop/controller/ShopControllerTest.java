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
	private static final String ITEM_NAME = "test1";

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
		List<Item> items = Arrays.asList(new Item());
		when(shopRepository.findAllItems()).thenReturn(items);
		shopController.allItems();
		verify(shopView).updateItemsShop(items);
	}

	@Test
	public void testNewItemWhenQuantityIsNegative() {
		Item item = new Item(PRODUCT_CODE, ITEM_NAME, -1);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Negative amount: -1");
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testNewItemWhenQuantityIsZero() {
		Item item = new Item(PRODUCT_CODE, ITEM_NAME, 0);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Negative amount: 0");
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testNewItemWhenItemDoesNotAlreadyExists() {
		Item item = new Item(PRODUCT_CODE, ITEM_NAME);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		shopController.newItem(item);
		InOrder inOrder = inOrder(shopRepository, shopView);
		inOrder.verify(shopRepository).storeItem(item);
	}

	@Test
	public void testNewItemWhenItemAlreadyExists() {
		Item itemToAdd = new Item(PRODUCT_CODE, ITEM_NAME, 1);
		Item existingItem = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(existingItem);
		shopController.newItem(itemToAdd);
		InOrder inOrder = inOrder(shopRepository, shopView);
		inOrder.verify(shopRepository).modifyItemQuantity(existingItem, 1);
	}

	@Test
	public void testRemoveItemWhenItemAlreadyExists() {
		Item itemToRemove = new Item(PRODUCT_CODE, ITEM_NAME);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(itemToRemove);
		shopController.removeItem(itemToRemove);
		InOrder inOrder = inOrder(shopRepository, shopView);
		inOrder.verify(shopRepository).removeItem(PRODUCT_CODE);

	}

	@Test
	public void testRemoveItemWhenItemDoesNotAlreadyExists() {
		Item itemToRemove = new Item(PRODUCT_CODE, ITEM_NAME);
		when(shopRepository.findItemByProductCode(PRODUCT_CODE)).thenReturn(null);
		shopController.removeItem(itemToRemove);
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testSearchItemWhenTextBoxIsEmpty() {
		when(shopRepository.findItemByName("")).thenReturn(null);
		shopController.searchItem("");
		verify(shopView).updateItemsShop(shopRepository.findAllItems());
		verifyNoMoreInteractions(ignoreStubs(shopView));
	}

	@Test
	public void testSearchItemWhenItemAlreadyExists() {
		Item itemToSearch = new Item("1", ITEM_NAME);
		when(shopRepository.findItemByName(ITEM_NAME)).thenReturn(itemToSearch);
		shopController.searchItem(itemToSearch.getName());
		verify(shopView).showSearchResult(itemToSearch);
	}

	@Test
	public void testSearchItemWhenItemDoestNotExists() {
		when(shopRepository.findItemByName(ITEM_NAME)).thenReturn(null);
		shopController.searchItem(ITEM_NAME);
		verify(shopView).errorLogItem("Item with name does not exists", ITEM_NAME);
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsLessThanItemQuantity() {
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		shopController.modifyItemQuantity(itemToModify, -1);
		verify(shopRepository).modifyItemQuantity(itemToModify, -1);
	}

	@Test
	public void testModifyQuantityWhenModifierIsEqualToItemQuantity() {
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		shopController.modifyItemQuantity(itemToModify, -2);
		verify(shopRepository).removeItem(itemToModify.getProductCode());
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsZero() {
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		shopController.modifyItemQuantity(itemToModify, 0);
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}

	@Test
	public void testModifyQuantityWhenModifierIsGreaterThanItemQuantity() {
		Item itemToModify = new Item(PRODUCT_CODE, ITEM_NAME, 2);
		shopController.modifyItemQuantity(itemToModify, -3);
		verifyNoMoreInteractions(ignoreStubs(shopRepository));
	}
}