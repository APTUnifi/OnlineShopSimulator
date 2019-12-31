package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;

import org.junit.Test;

@RunWith(GUITestRunner.class)
public class ShopViewTotalTest extends AssertJSwingJUnitTestCase{

	private static final String CART_FIXTURE_LABEL_2 = "test2";
	private static final String CART_FIXTURE_LABEL_1 = "cartTest";
	private static final int ITEM_FIXTURE_QUANTITY_1 = 10;
	private static final int ITEM_FIXTURE_QUANTITY_2 = 19;
	private static final String ITEM_FIXTURE_NAME_2 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";
	private static final int HEIGHT = 600;
	private static final int WIDTH = 676;
	private static final int FIRST_ITEM = 0;
	private FrameFixture window;
	private	ShopViewTotal shopViewTotal;

	@Mock
	private ShopController shopController;

	@Mock
	private CartController cartController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			shopViewTotal = new ShopViewTotal();
			shopViewTotal.setShopController(shopController);
			shopViewTotal.setCartController(cartController);
			return shopViewTotal;
		});
		window = new FrameFixture(robot(), shopViewTotal);
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		window.show(dimension);

	}
	@Test @GUITest
	public void testControlsInitialStates() {
		window.textBox("itemName").requireEnabled();
		window.textBox("cartNameText").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.button(JButtonMatcher.withText("Remove")).requireDisabled();
		window.button(JButtonMatcher.withText("Buy")).requireDisabled();
		window.button(JButtonMatcher.withText("Search")).requireEnabled();
		window.button(JButtonMatcher.withText("Delete")).requireDisabled();
		window.list("itemListShop");
		window.list("itemListCart");
		window.list("listItemsCart");
		window.list("listCart");
		window.label("lblCartName").requireText("Cart Name : ");
		window.label("lblYourCart").requireText("Your Cart");
		window.label("lblShop").requireText("Items Shop");
		window.label("errorMessageLabel").requireText(" ");
		window.label("lblItemsCart").requireText("Items Cart");
		window.label("lblCartsHistory").requireText("Carts");
		
	}

	@Test
	public void testAddButtonShouldBeEnabledWhenAnItemIsSelectedInItemListShop() {
		GuiActionRunner.execute(
				()-> shopViewTotal.getItemListShopModel().addElement(
						new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1))
				);
		window.list("itemListShop").selectItem(FIRST_ITEM);
		JButtonFixture addButton = window.button(JButtonMatcher.withText("Add"));
		addButton.requireEnabled();
		assertThat(addButton).matches(p -> p.isEnabled());
		window.list("itemListShop").clearSelection();
		addButton.requireDisabled();
	}

	@Test
	public void testSearchButtonShouldShowFilteredShopListIfThereIsTheSearchedItem() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = shopViewTotal.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
				});
		window.textBox("itemName").setText(ITEM_FIXTURE_NAME_1);
		GuiActionRunner.execute(() ->
		shopViewTotal.showSearchResult(item1)
				);
		String[] listContents = window.list("itemListShop").contents();
		assertThat(listContents).containsExactly(item1.toString());
	}

	@Test
	public void testSearchButtonShouldShowShopListIfTheSearchBoxIsEmpty() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = shopViewTotal.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
				});
		window.textBox("itemName").setText("");
		GuiActionRunner.execute(
				() -> shopViewTotal.showSearchResult(item1)
				);
		String[] listContents = window.list("itemListShop").contents();
		assertThat(listContents).containsExactly(item1.toString(),item2.toString());
	}
	
	@Test
	public void testBuyButtonShouldBeEnabledWhenThereIsOneOrMoreItemsInItemListCartAndNameInCartNameText() {
		GuiActionRunner.execute(
				()-> shopViewTotal.getItemListCartModel().addElement(
						new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1))
				);
		window.textBox("cartNameText").enterText("testCart");
		window.list("itemListCart").selectItem(0);
		JButtonFixture buyButton = window.button(JButtonMatcher.withText("Remove"));	
		buyButton.requireEnabled();
		assertThat(buyButton).matches(p -> p.isEnabled());
		window.list("itemListCart").clearSelection();
		buyButton.requireDisabled();
	}


	@Test
	public void testDeleteButtonShouldBeEnabledWhenAnItemIsSelectedInItemListCart() {
		GuiActionRunner.execute(
				()->{ 
					shopViewTotal.getItemListCartModel().addElement(
							new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1));
					shopViewTotal.getItemListShopModel().addElement(
							new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1, ITEM_FIXTURE_QUANTITY_2));
				});
		window.list("itemListCart").selectItem(FIRST_ITEM);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Remove"));	
		deleteButton.requireEnabled();
		assertThat(deleteButton).matches(p -> p.isEnabled());
		window.list("itemListCart").clearSelection();
		deleteButton.requireDisabled();
	}
	
	@Test
	public void testShowItemsShopShouldAddItemsToTheItemShopList() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(() ->
		shopViewTotal.showItemsShop(Arrays.asList(item1,item2))
				);
		String[] listContents = window.list("itemListShop").contents();
		assertThat(listContents).containsExactly(item1.toString(),item2.toString());
	}

	@Test
	public void testErrorLogShouldShowTheMessageInTheErrorMessageLabel() {
		List<Item> items = new ArrayList<Item>();
		Item item = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		items.add(item);
		items.add(item1);
		GuiActionRunner.execute(
				() -> shopViewTotal.errorLog("error Message", items)
				);		
		window.label("errorMessageLabel").requireText("error Message: " + item.getName() + " "+ item1.getName() + " ");
		assertThat(JLabelMatcher.withText("errorMessageLabel").andShowing());
	}
	
	@Test
	public void testItemAddedToCartShouldAddTheItemToTheItemListCartAndResetTheErrorLabel() {
		Item item = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		GuiActionRunner.execute(
				()-> shopViewTotal.itemAddedToCart(item)
				);
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testUpdateItemsCartShouldUpdateTheItemsCartList() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2,ITEM_FIXTURE_QUANTITY_2);
		GuiActionRunner.execute(
				()-> {
					shopViewTotal.getItemListCartModel().addElement(item2);
					shopViewTotal.getItemListShopModel().addElement(item1);
				});
		GuiActionRunner.execute(
				()-> shopViewTotal.updateItemsCart(Arrays.asList(item2))
				);
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item2.toString());

	}


	@Test
	public void testItemRemovedToCartShouldRemoveTheItemFromTheItemListCartAndResetErrorLabel() {
		//setup
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListCartModel = shopViewTotal.getItemListCartModel();
					itemListCartModel.addElement(item1);
					itemListCartModel.addElement(item2);
				});
		GuiActionRunner.execute(
				()-> shopViewTotal.itemRemovedFromCart(item1)
				);
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testAddButtonShouldDelegateToTheCartControllerAddElement() {
		Item item = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		GuiActionRunner.execute(
				()-> shopViewTotal.getItemListShopModel().addElement(item)
				);
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Add")).click();
		verify(cartController).addToCart(item);
	}

	@Test
	public void testRemoveButtonShouldDelegateToTheCartControllerRemoveElement() {
		Item item = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		GuiActionRunner.execute(
				()-> shopViewTotal.getItemListCartModel().addElement(item)
				);
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Remove")).click();
		verify(cartController).removeFromCart(item);
	}

	@Test
	public void testSearchButtonShouldDelegateToTheShopControllerShowSearchResults() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = shopViewTotal.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
				});
		window.textBox("itemName").enterText(ITEM_FIXTURE_NAME_2);
		window.button(JButtonMatcher.withText("Search")).click();
		verify(shopController).searchItem(window.textBox("itemName").text());	
	}
	
	@Test
	public void testBuyButtonShouldDelegateToTheCartControllerCompletePurchase() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListCartModel = shopViewTotal.getItemListCartModel();
					itemListCartModel.addElement(item1);
					itemListCartModel.addElement(item2);
				});
		window.textBox("cartNameText").enterText("testCart");
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Buy")).click();
		verify(cartController).completePurchase(window.textBox("cartNameText").text());
	}
	
	@Test
	public void testShowItemsCartShouldShowItemsWhenACartIsSelected() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = shopViewTotal.getListCartModel();
					listCartModel.addElement(cart);
				});
		window.list("listCart").selectItem(FIRST_ITEM);
		String[] listContents = window.list("listItemsCart").contents();
		assertThat(listContents).containsExactly(item1.toString(),item2.toString());
	}

	@Test
	public void testRemoveCartShouldRemoveTheCartFromTheListCartAndResetErrorLabel() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart1 = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		Cart cart2 = new Cart(Arrays.asList(item2),CART_FIXTURE_LABEL_2);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = shopViewTotal.getListCartModel();
					listCartModel.addElement(cart1);
					listCartModel.addElement(cart2);
				});
		GuiActionRunner.execute(
				()-> shopViewTotal.removeCart(cart1)
				);
		String[] listContents = window.list("listCart").contents();
		assertThat(listContents).containsExactly(cart2.toString());
	}
	@Test
	public void testShowHistoryShouldShowCartToTheListCart() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart1 = new Cart(Arrays.asList(item1),CART_FIXTURE_LABEL_1);
		Cart cart2 = new Cart(Arrays.asList(item2),CART_FIXTURE_LABEL_2);
		GuiActionRunner.execute(
				() -> shopViewTotal.showHistory(Arrays.asList(cart1,cart2))
				);
		String[] listContents = window.list("listCart").contents();
		assertThat(listContents).containsExactly(cart1.toString(),cart2.toString());
	}

	@Test
	public void testRemoveButtonShouldDelegateTheCartControllerRemoveCart() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Cart cart1 = new Cart(Arrays.asList(item1),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()-> shopViewTotal.getListCartModel().addElement(cart1)
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Delete")).click();
		verify(cartController).removeCart(cart1);
	}
	
	@Test
	public void testRemoveButtonShouldBeEnabledOnlyForTheFirstList() {
		Cart cart = new Cart();
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = shopViewTotal.getListCartModel();
					listCartModel.addElement(cart);
				}
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete"));
		deleteButton.requireEnabled();
		assertThat(deleteButton).matches(p -> p.isEnabled());
		window.list("listCart").clearSelection();
		deleteButton.requireDisabled();
	}

}