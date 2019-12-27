package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.DefaultListModel;

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

public class HistoryDialogSwingTest extends AssertJSwingJUnitTestCase{

	private static final String CART_FIXTURE_LABEL_2 = "test2";
	private static final String ITEM_FIXTURE_NAME_2 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";
	private static final int HEIGHT = 800;
	private static final int WIDTH = 800;
	private static final int FIRST_ITEM = 0;
	private static final String CART_FIXTURE_LABEL_1 = "cartTest";

	private DialogFixture window;
	private	HistoryDialogSwing historyDialogSwing;

	@Mock
	private CartController cartController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			historyDialogSwing = new HistoryDialogSwing();
			historyDialogSwing.setCartController(cartController);
			historyDialogSwing.setSize(WIDTH,HEIGHT);
			historyDialogSwing.pack();
			historyDialogSwing.setLocationRelativeTo(null);
			return historyDialogSwing;
		});
		window = new DialogFixture(robot(), historyDialogSwing);
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		window.show(dimension);

	}
	
	@Test @GUITest
	public void testControlsInitialStates() {
		window.list("listCart");
		window.list("listItemsCart");
		window.label("lblItemsCart").requireText("Items Cart");
		window.label("lblCarts").requireText("Carts");
		window.button(JButtonMatcher.withText("ShowHistory")).requireEnabled();
		window.button(JButtonMatcher.withText("Remove")).requireDisabled();
		window.button(JButtonMatcher.withText("Close")).requireEnabled();
	}
}
