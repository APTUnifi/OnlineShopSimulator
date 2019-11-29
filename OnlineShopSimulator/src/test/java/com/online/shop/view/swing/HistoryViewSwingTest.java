package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.controller.CartController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;

public class HistoryViewSwingTest extends AssertJSwingJUnitTestCase{

	private FrameFixture window;
	private	HistoryViewSwing historyViewSwing;

	@Mock
	private CartController cartController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			historyViewSwing = new HistoryViewSwing();
			historyViewSwing.setCartController(cartController);
			return historyViewSwing;
		});
		window = new FrameFixture(robot(), historyViewSwing);
		window.show();

	}
	@Test @GUITest
	public void testControlsInitialStates() {
		window.button(JButtonMatcher.withText("Remove")).requireDisabled();
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
		window.list("listCart").selectItem(0);
		//verify
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Remove"));
		deleteButton.requireEnabled();
		window.list("listCart").clearSelection();
		deleteButton.requireDisabled();
	}
	@Test
	public void testShowItemsCartShouldShowItemsWhenACartIsSelected() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2));
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = historyViewSwing.getListCartModel();
					listCartModel.addElement(cart);
				}
		);
		window.list("listCart").selectItem(0);
		GuiActionRunner.execute(
				()-> historyViewSwing.showItemsCart(cart)
		);
		
		String[] listContents = window.list("listItemsCart").contents();
		assertThat(listContents).containsExactly(item1.toString(),item2.toString());
	}
	@Test
	public void testRemoveCartShouldRemoveTheCartFromTheListCartAndResetErrorLabel() {
		//setup
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart1 = new Cart(Arrays.asList(item1,item2));
		Cart cart2 = new Cart(Arrays.asList(item2));

		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Cart> listCartModel = historyViewSwing.getListCartModel();
					listCartModel.addElement(cart1);
					listCartModel.addElement(cart2);
				}
		);
		//execute
		GuiActionRunner.execute(
				()-> historyViewSwing.removeCart(cart1)
		);
		//verify
		String[] listContents = window.list("listCart").contents();
		assertThat(listContents).containsExactly(cart2.toString());
	}
	
	@Test
	public void testShowItemsShopShouldAddItemsToTheItemShopList() {
		//setup
		Item item1 = new Item("1","Iphone");
		Item item2 = new Item("3","Samsung");
		Cart cart1 = new Cart(Arrays.asList(item1));
		Cart cart2 = new Cart(Arrays.asList(item2));

		//execute
		GuiActionRunner.execute(() ->
			historyViewSwing.showHistory(Arrays.asList(cart1,cart2))
		);
		//verify
		String[] listContents = window.list("listCart").contents();
		assertThat(listContents).containsExactly(cart1.toString(),cart2.toString());
	}
	@Test
	public void testRemoveButtonShouldDelegateTheCartControllerRemoveCart() {
		//setup
		Item item1 = new Item("1","Iphone");
		Cart cart1 = new Cart(Arrays.asList(item1));
		GuiActionRunner.execute(
				()-> historyViewSwing.getListCartModel().addElement(cart1)
		);
		//execute
		window.list("listCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		//verify
		verify(cartController).removeFromHistory(cart1);
	
	}
	
}
