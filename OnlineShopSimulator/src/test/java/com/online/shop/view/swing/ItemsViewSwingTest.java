package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import javax.swing.DefaultListModel;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.controller.ShopController;
import com.online.shop.model.Item;

import org.junit.Test;

@RunWith(GUITestRunner.class)
public class ItemsViewSwingTest extends AssertJSwingJUnitTestCase{

	private FrameFixture window;
	private	ItemsViewSwing itemsViewSwing;

	@Mock
	private ShopController shopController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			itemsViewSwing = new ItemsViewSwing();
			itemsViewSwing.setShopController(shopController);
			return itemsViewSwing;
		});
		window = new FrameFixture(robot(), itemsViewSwing);
		window.show();

	}
	@Test @GUITest
	public void testControlsInitialStates() {
		window.textBox("itemName").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.button(JButtonMatcher.withText("Remove")).requireDisabled();
		window.list("itemListShop");
		window.list("itemListCart");
		window.button(JButtonMatcher.withText("Search")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
		window.button(JButtonMatcher.withText("Buy")).requireDisabled();
	}
	@Test
	public void testWhenSearchFieldIsNotEmptyThenSearchButtonShouldBeEnable() {
		window.textBox("itemName").enterText("Iphone");
		window.button(JButtonMatcher.withText("Search")).requireEnabled();
	}

	@Test
	public void testAddButtonShouldBeEnabledWhenAnItemIsSelectedInItemListShop() {
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListShopModel().addElement(new Item("Iphone", 20))
		);
		window.list("itemListShop").selectItem(0);
		JButtonFixture addButton = window.button(JButtonMatcher.withText("Add"));
		addButton.requireEnabled();
		window.list("itemListShop").clearSelection();
		addButton.requireDisabled();
	}
	
	@Test
	public void testBuyButtonShouldBeEnabledWhenThereIsOneOrMoreItemsInItemListCart() {
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListCartModel().addElement(new Item("Iphone", 1))
		);
		window.list("itemListCart").selectItem(0);
		JButtonFixture buyButton = window.button(JButtonMatcher.withText("Remove"));	
		buyButton.requireEnabled();
		window.list("itemListCart").clearSelection();
		buyButton.requireDisabled();

	}
	@Test
	public void testDeleteButtonShouldBeEnabledWhenAnItemIsSelectedInItemListCart() {
		//setup
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListCartModel().addElement(new Item("Iphone", 1))
		);
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListShopModel().addElement(new Item("Iphone", 20))
		);
		//execute
		window.list("itemListCart").selectItem(0);
		//verify
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Remove"));	
		deleteButton.requireEnabled();
		window.list("itemListCart").clearSelection();
		deleteButton.requireDisabled();
	}
	@Test
	public void testShowItemsShouldAddItemsToTheItemShopList() {
		//setup
		Item item1 = new Item("Iphone 9", 20);
		Item item2 = new Item("Samsung 9", 6);
		//execute
		GuiActionRunner.execute(() ->
			itemsViewSwing.showItems(Arrays.asList(item1,item2))
		);
		//verify
		String[] listContents = window.list("itemListShop").contents();
		assertThat(listContents).containsExactly(item1.toString(),item2.toString());
	}
	@Test
	public void testErrorLogShouldShowTheMessageInTheErrorMessageLabel() {
		Item item = new Item("Iphone",20);
		GuiActionRunner.execute(
				() -> itemsViewSwing.errorLog("error Message", item)
		);
		window.label("errorMessageLabel").requireText("error Message: " + item);
	}
	@Test
	public void testItemAddedToCartShouldAddTheItemToTheItemListCartAndResetTheErrorLabel() {
		//setup
		Item item = new Item("Iphone",20);
		//execute
		itemsViewSwing.itemAddedToCart(item);
		//verify
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	@Test
	public void testItemRemovedToCartShouldRemoveTheItemFromTheItemListCartAndResetTheErrorLabel() {
		//setup
		Item item1 = new Item("Iphone",1);
		Item item2 = new Item("Samsung",2);
		Item item3 = new Item("Nokia", 3);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListCartModel = itemsViewSwing.getItemListCartModel();
					itemListCartModel.addElement(item1);
					itemListCartModel.addElement(item2);
					itemListCartModel.addElement(item3);
				}
		);
		//execute
		GuiActionRunner.execute(
				()-> itemsViewSwing.itemRemovedToCart(item1)
		);
		//verify
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item2.toString(),item3.toString());
	}
	@Test
	public void testAddButtonShouldDelegateToTheItemControllerAddElement() {
		//setup
		Item item = new Item("Iphone",1);
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListShopModel().addElement(item)
		);
		//execute
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		//verify
		verify(shopController).addItem(item);
	}

	@Test
	public void testRemoveButtonShouldDelegateToTheItemControllerRemoveElement() {
		//setup
		Item item = new Item("Iphone",1);
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListCartModel().addElement(item)
		);
		//execute
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		//verify
		verify(shopController).removeItemFromCart(item);
	}
	
	@Test
	public void testSearchButtonShouldDelegateToTheItemControllerShowSearchResults() {
		//setup
		Item item1 = new Item("Iphone",1);
		Item item2 = new Item("Samsung",2);
		Item item3 = new Item("Nokia", 3);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = itemsViewSwing.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
					itemListShopModel.addElement(item3);
				}
		);
		window.textBox("nameItem").enterText("Nokia");
		//execute
		window.button(JButtonMatcher.withText("Search")).click();
		//verify
		verify(shopController).searchItem(item1);
			
	}

}
