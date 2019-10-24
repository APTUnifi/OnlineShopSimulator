package com.online.shop.controller;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;
import com.online.shop.view.ItemsView;

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

}
