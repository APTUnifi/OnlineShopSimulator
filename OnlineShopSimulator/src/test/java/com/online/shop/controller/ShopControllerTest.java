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
	public void testNewItemWhenQuantityIsNotPositive() {
		// setup
		Item item = new Item("1", -1);
		when(itemsRepository.findByProductCode("1")).thenReturn(null);
		// exercise + verify
		assertThatThrownBy(() -> shopController.newItem(item)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Negative amount: -1");
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testNewItemWhenItemDoesNotAlreadyExists() {
		// setup
		Item item = new Item("1", 1);
		when(itemsRepository.findByProductCode("1")).thenReturn(null);
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
		Item itemToAdd = new Item("1", 1);
		Item existingItem = new Item("1", 2);
		when(itemsRepository.findByProductCode("1")).thenReturn(existingItem);
		// exercise
		shopController.newItem(itemToAdd);
		// verify
		InOrder inOrder = inOrder(itemsRepository, itemsView);
		inOrder.verify(itemsRepository).increaseQuantity(itemToAdd);
		inOrder.verify(itemsView).itemQuantityAdded(existingItem);
	}

	@Test
	public void testRemoveItemWhenItemAlreadyExists() {
		// setup
		Item itemToRemove = new Item("1", 1);
		when(itemsRepository.findByProductCode("1")).thenReturn(itemToRemove);
		// exercise
		shopController.removeItem(itemToRemove);
		// verify
		InOrder inOrder = inOrder(itemsRepository, itemsView);
		inOrder.verify(itemsRepository).remove(itemToRemove);
		inOrder.verify(itemsView).itemRemoved(itemToRemove);

	}

	@Test
	public void testRemoveItemWhenItemDoesNotAlreadyExists() {
		// setup
		Item itemToRemove = new Item("1", 1);
		when(itemsRepository.findByProductCode("1")).thenReturn(null);
		// exercise
		shopController.removeItem(itemToRemove);
		// verify
		verify(itemsView).errorLog("Item with production code 1 does not exists", itemToRemove);
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}

	@Test
	public void testSearchItemWhenItemAlreadyExists() {
		// setup
		Item itemToSearch = new Item("battery");
		when(itemsRepository.findByName("battery")).thenReturn(itemToSearch);
		// exercise
		shopController.searchItem(itemToSearch);
		// verify
		verify(itemsView).showSearchResult(itemToSearch);
	}

	@Test
	public void testSearchItemWhenItemDoestNotExists() {
		// setup
		Item itemToSearch = new Item("battery");
		when(itemsRepository.findByName("battery")).thenReturn(null);
		// exercise
		shopController.searchItem(itemToSearch);
		// verify
		verify(itemsView).errorLog("Item with name battery doest not exists", itemToSearch);
		verifyNoMoreInteractions(ignoreStubs(itemsRepository));
	}
}
