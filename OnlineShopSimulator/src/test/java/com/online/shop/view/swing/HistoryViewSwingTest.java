package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.controller.CartController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;

public class HistoryViewSwingTest extends AssertJSwingJUnitTestCase{

	private static final String CART_FIXTURE_LABEL_2 = "test2";
	private static final String ITEM_FIXTURE_NAME_2 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";
	private static final int HEIGHT = 300;
	private static final int WIDTH = 500;
	private static final int FIRST_ITEM = 0;
	private static final String CART_FIXTURE_LABEL_1 = "cartTest";

	private DialogFixture window;
	private	HistoryViewSwing historyViewSwing;
	private JDialog historyDialog;

	@Mock
	private CartController cartController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			historyDialog = new JDialog();
			historyViewSwing = new HistoryViewSwing();
			historyViewSwing.setCartController(cartController);
			historyViewSwing.setSize(WIDTH,HEIGHT);
			historyDialog.getContentPane().add(historyViewSwing);
			historyDialog.setSize(WIDTH, HEIGHT);
			historyDialog.pack();
			historyDialog.setLocationRelativeTo(null);
			return historyDialog;
		});
		window = new DialogFixture(robot(), historyDialog);
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		window.show(dimension);

	}
	@Test @GUITest
	public void testControlsInitialStates() {
		window.button(JButtonMatcher.withText("Remove")).requireDisabled();
		window.button(JButtonMatcher.withText("Close")).requireEnabled();
		window.label("lblItemsCart").requireText("Items Cart");
		window.label("lblCarts").requireText("Carts");
		window.list("listCart");
		window.list("listItemsCart");

	}
	@Test
	public void testRemoveButtonShouldBeEnabledOnlyForTheFirstList() {
		Cart cart = new Cart();
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = historyViewSwing.getListCartModel();
					listCartModel.addElement(cart);
				}
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Remove"));
		deleteButton.requireEnabled();
		assertThat(deleteButton).matches(p -> p.isEnabled());
		window.list("listCart").clearSelection();
		deleteButton.requireDisabled();
	}
	@Test
	public void testShowItemsCartShouldShowItemsWhenACartIsSelected() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = historyViewSwing.getListCartModel();
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
					DefaultListModel<Cart> listCartModel = historyViewSwing.getListCartModel();
					listCartModel.addElement(cart1);
					listCartModel.addElement(cart2);
				});
		GuiActionRunner.execute(
				()-> historyViewSwing.removeCart(cart1)
				);
		String[] listContents = window.list("listCart").contents();
		assertThat(listContents).containsExactly(cart2.toString());
	}

	@Test
	public void testShowItemsShopShouldAddItemsToTheItemShopList() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart1 = new Cart(Arrays.asList(item1),CART_FIXTURE_LABEL_1);
		Cart cart2 = new Cart(Arrays.asList(item2),CART_FIXTURE_LABEL_2);
		GuiActionRunner.execute(
				() -> historyViewSwing.showHistory(Arrays.asList(cart1,cart2))
				);
		String[] listContents = window.list("listCart").contents();
		assertThat(listContents).containsExactly(cart1.toString(),cart2.toString());
	}

	@Test
	public void testRemoveButtonShouldDelegateTheCartControllerRemoveCart() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Cart cart1 = new Cart(Arrays.asList(item1),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()-> historyViewSwing.getListCartModel().addElement(cart1)
				);
		window.list("listCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		verify(cartController).removeCart(cart1);
	}

	@Test
	public void testCloseButtonShouldCloseTheDialogWindow() {
		window.button(JButtonMatcher.withName("Close")).click();
		assertThat(historyDialog.isShowing()).isFalse();
	}

}
