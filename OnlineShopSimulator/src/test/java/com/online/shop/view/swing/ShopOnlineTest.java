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
public class ShopOnlineTest extends AssertJSwingJUnitTestCase{

	private static final String CART_FIXTURE_LABEL_2 = "test2";
	private static final String CART_FIXTURE_LABEL_1 = "cartTest";
	private static final int ITEM_FIXTURE_QUANTITY_1 = 10;
	private static final int ITEM_FIXTURE_QUANTITY_2 = 19;
	private static final String ITEM_FIXTURE_NAME_2 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";
	private static final int HEIGHT = 600;
	private static final int WIDTH = 760;
	private static final int FIRST_ITEM = 0;
	private FrameFixture window;
	private ShopOnlineView shopOnlineView;

	@Mock
	private ShopController shopController;

	@Mock
	private CartController cartController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			shopOnlineView = new ShopOnlineView();
			shopOnlineView.setShopController(shopController);
			shopOnlineView.setCartController(cartController);
			return shopOnlineView;
		});
		window = new FrameFixture(robot(), shopOnlineView);
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		window.show(dimension);
	}
	
	@Test @GUITest
	public void testControlsInitialStates() {
		window.textBox("itemName").requireEnabled();
		window.textBox("cartNameText").requireEnabled();
		window.button(JButtonMatcher.withName("btnAdd")).requireDisabled();
		window.button(JButtonMatcher.withName("btnRemove")).requireDisabled();
		window.button(JButtonMatcher.withName("btnAdd")).requireDisabled();
		window.button(JButtonMatcher.withText("Search")).requireEnabled();
		window.button(JButtonMatcher.withName("btnDelete")).requireDisabled();
		window.list("itemListShop");
		window.list("itemListCart");
		window.list("listItemsCart");
		window.list("listCart");
		window.label("lblCartName").requireText("Choose a Cart Name");
		window.label("lblYourCart").requireText("Your Cart");
		window.label("lblShop").requireText("Items In Shop");
		window.label("errorMessageLabel").requireText(" ");
		window.label("lblItemsCart").requireText("Items Cart");
		window.label("lblCartsHistory").requireText("Carts");
		window.label("lblPurchaseHistory").requireText("Purchase History");
	}

	@Test
	public void testAddButtonShouldBeEnabledWhenAnItemIsSelectedInItemListShop() {
		GuiActionRunner.execute(
				()-> shopOnlineView.getItemListShopModel().addElement(
						new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1))
				);
		window.list("itemListShop").selectItem(FIRST_ITEM);
		JButtonFixture addButton = window.button(JButtonMatcher.withName("btnAdd"));
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
					DefaultListModel<Item> itemListShopModel = shopOnlineView.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
				});
		window.textBox("itemName").setText(ITEM_FIXTURE_NAME_1);
		GuiActionRunner.execute(() ->
		shopOnlineView.showSearchResult(item1)
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
					DefaultListModel<Item> itemListShopModel = shopOnlineView.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
				});
		window.textBox("itemName").setText("");
		GuiActionRunner.execute(
				() -> shopOnlineView.showSearchResult(item1)
				);
		String[] listContents = window.list("itemListShop").contents();
		assertThat(listContents).containsExactly(item1.toString(),item2.toString());
	}

	@Test
	public void testBuyButtonShouldBeEnabledWhenThereIsOneOrMoreItemsInItemListCartAndNameInCartNameText() {
		GuiActionRunner.execute(
				()-> shopOnlineView.getItemListCartModel().addElement(
						new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1))
				);
		window.textBox("cartNameText").enterText("testCart");
		window.list("itemListCart").selectItem(0);
		JButtonFixture buyButton = window.button(JButtonMatcher.withName("btnBuy"));
		buyButton.requireEnabled();
		assertThat(buyButton).matches(p -> p.isEnabled());
		window.list("itemListCart").clearSelection();
		buyButton.requireDisabled();
	}

	@Test
	public void testRemoveButtonShouldBeEnabledWhenAnItemIsSelectedInItemListCart() {
		GuiActionRunner.execute(
				()->{ 
					shopOnlineView.getItemListCartModel().addElement(
							new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1));
					shopOnlineView.getItemListShopModel().addElement(
							new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1, ITEM_FIXTURE_QUANTITY_2));
				});
		window.list("itemListCart").selectItem(FIRST_ITEM);
		JButtonFixture removeButton = window.button(JButtonMatcher.withName("btnRemove"));
		removeButton.requireEnabled();
		assertThat(removeButton).matches(p -> p.isEnabled());
		window.list("itemListCart").clearSelection();
		removeButton.requireDisabled();
	}

	@Test
	public void testShowItemsShopShouldAddItemsToTheItemShopList() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(() ->
		shopOnlineView.showItemsShop(Arrays.asList(item1,item2))
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
				() -> shopOnlineView.errorLog("error Message", items)
				);		
		window.label("errorMessageLabel").requireText("error Message: " + item.getName() + " "+ item1.getName() + " ");
		assertThat(JLabelMatcher.withText("errorMessageLabel").andShowing());
	}
	@Test
	public void testErrorLogItemShouldShowTheMessageInTheErrorMessageLabel() {
		Item item = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(
				() -> shopOnlineView.errorLogItem("error Message", item.getName())
				);		
		window.label("errorMessageLabel").requireText("error Message: " + item.getName());
		assertThat(JLabelMatcher.withText("errorMessageLabel").andShowing());
	}

	@Test
	public void testItemAddedToCartShouldAddTheItemToTheItemListCartAndResetTheErrorLabel() {
		Item item = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		GuiActionRunner.execute(
				()-> shopOnlineView.itemAddedToCart(item)
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
					shopOnlineView.getItemListCartModel().addElement(item2);
					shopOnlineView.getItemListShopModel().addElement(item1);
				});
		GuiActionRunner.execute(
				()-> shopOnlineView.updateItemsCart(Arrays.asList(item2))
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
					DefaultListModel<Item> itemListCartModel = shopOnlineView.getItemListCartModel();
					itemListCartModel.addElement(item1);
					itemListCartModel.addElement(item2);
				});
		GuiActionRunner.execute(
				()-> shopOnlineView.itemRemovedFromCart(item1)
				);
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testAddButtonShouldDelegateToTheCartControllerAddElement() {
		Item item = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		GuiActionRunner.execute(
				()-> shopOnlineView.getItemListShopModel().addElement(item)
				);
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnAdd")).click();
		verify(cartController).addToCart(item);
	}

	@Test
	public void testRemoveButtonShouldDelegateToTheCartControllerRemoveElement() {
		Item item = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		GuiActionRunner.execute(
				()-> shopOnlineView.getItemListCartModel().addElement(item)
				);
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnRemove")).click();
		verify(cartController).removeFromCart(item);
	}

	@Test
	public void testSearchButtonShouldDelegateToTheShopControllerShowSearchResults() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = shopOnlineView.getItemListShopModel();
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
					DefaultListModel<Item> itemListCartModel = shopOnlineView.getItemListCartModel();
					itemListCartModel.addElement(item1);
					itemListCartModel.addElement(item2);
				});
		window.textBox("cartNameText").enterText("testCart");
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnBuy")).click();
		verify(cartController).completePurchase(window.textBox("cartNameText").text());
	}

	@Test
	public void testShowItemsCartShouldShowItemsWhenACartIsSelected() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = shopOnlineView.getListCartModel();
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
					DefaultListModel<Cart> listCartModel = shopOnlineView.getListCartModel();
					listCartModel.addElement(cart1);
					listCartModel.addElement(cart2);
				});
		GuiActionRunner.execute(
				()-> shopOnlineView.removeCart(cart1)
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
				() -> shopOnlineView.showHistory(Arrays.asList(cart1,cart2))
				);
		String[] listContents = window.list("listCart").contents();
		assertThat(listContents).containsExactly(cart1.toString(),cart2.toString());
	}

	@Test
	public void testRemoveButtonShouldDelegateTheCartControllerRemoveCart() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Cart cart1 = new Cart(Arrays.asList(item1),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()-> shopOnlineView.getListCartModel().addElement(cart1)
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnDelete")).click();
		verify(cartController).removeCart(cart1);
	}

	@Test
	public void testRemoveButtonShouldBeEnabledOnlyForTheFirstList() {
		Cart cart = new Cart();
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = shopOnlineView.getListCartModel();
					listCartModel.addElement(cart);
				}
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withName("btnDelete"));
		deleteButton.requireEnabled();
		assertThat(deleteButton).matches(p -> p.isEnabled());
		window.list("listCart").clearSelection();
		deleteButton.requireDisabled();
	}

}