package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;

public class HistoryViewPanelTest extends AssertJSwingJUnitTestCase {

	private static final String CART_FIXTURE_LABEL_2 = "test2";
	private static final String CART_FIXTURE_LABEL_1 = "cartTest";
	private static final String ITEM_FIXTURE_NAME_2 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";
	private static final int HEIGHT = 400;
	private static final int WIDTH = 800;
	private static final int FIRST_ITEM = 0;

	private HistoryViewPanel historyViewPanel;
	private JPanelFixture window;
	private JFrame frame;

	@Mock
	private ShopController shopController;

	@Mock
	private CartController cartController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			historyViewPanel = new HistoryViewPanel();
			frame = new JFrame();
			historyViewPanel.setCartController(cartController);
			frame.add(historyViewPanel);
			Dimension dimension = new Dimension(WIDTH, HEIGHT);
			frame.setPreferredSize(dimension);
			frame.pack();
			frame.setVisible(true);
		});


		window = new JPanelFixture(robot(), historyViewPanel);
	}
	@Test @GUITest
	public void testControlsInitialStates() {

		window.button(JButtonMatcher.withName("btnDelete")).requireDisabled();
		window.list("listItemsCart");
		window.list("listCart");
		window.label("errorMessageLabel").requireText(" ");
		window.label("lblItemsCart").requireText("Items Cart");
		window.label("lblCartsHistory").requireText("Carts");
		window.label("lblPurchaseHistory").requireText("Purchase History");
	}
	@Test
	public void testShowItemsCartShouldShowItemsWhenACartIsSelected() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = historyViewPanel.getListCartModel();
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
					DefaultListModel<Cart> listCartModel = historyViewPanel.getListCartModel();
					listCartModel.addElement(cart1);
					listCartModel.addElement(cart2);
				});
		GuiActionRunner.execute(
				()-> historyViewPanel.removeCart(cart1)
				);
		String[] listContents = window.list("listCart").contents();
		window.label("errorMessageLabel").requireText(" ");
		assertThat(listContents).containsExactly(cart2.toString());
	}
	@Test
	public void testShowHistoryShouldShowCartToTheListCart() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart1 = new Cart(Arrays.asList(item1),CART_FIXTURE_LABEL_1);
		Cart cart2 = new Cart(Arrays.asList(item2),CART_FIXTURE_LABEL_2);
		GuiActionRunner.execute(
				() -> historyViewPanel.showHistory(Arrays.asList(cart1,cart2))
				);
		String[] listContents = window.list("listCart").contents();
		assertThat(listContents).containsExactly(cart1.toString(),cart2.toString());
	}

	@Test
	public void testDeleteButtonShouldDelegateTheCartControllerRemoveCart() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Cart cart1 = new Cart(Arrays.asList(item1),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()-> historyViewPanel.getListCartModel().addElement(cart1)
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnDelete")).click();
		verify(cartController).removeCart(cart1);
	}

	@Test
	public void testDeleteButtonShouldBeEnabledOnlyForTheFirstList() {
		Cart cart = new Cart();
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = historyViewPanel.getListCartModel();
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
	@Test
	public void testErrorLogCartShouldShowTheMessageInTheErrorMessageLabel() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Cart cart1 = new Cart(Arrays.asList(item1),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				() -> historyViewPanel.errorLogCart("error Message", cart1.getLabel())
				);		
		window.label("errorMessageLabel").requireText("error Message: " + cart1.getLabel());
		assertThat(JLabelMatcher.withText("errorMessageLabel").andShowing());
	}
}
