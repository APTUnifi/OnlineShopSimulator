package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.online.shop.controller.CartController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ItemsMongoRepository;
import com.online.shop.repository.sql.ItemsSqlRepository;

public class HistoryViewSwingSQLIT extends AssertJSwingJUnitTestCase {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final PostgreSQLContainer sqlContainer = new PostgreSQLContainer();

	private static final int HEIGHT = 300;
	private static final int WIDTH = 500;
	private static final int SECOND_ITEM = 1;
	private static final int FIRST_ITEM = 0;
	private static final String CART_FIXTURE_LABEL_1 = "cartTest";
	private static final String ITEM_FIXTURE_NAME_2 = "test2";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";

	ItemsSqlRepository itemsRepository;
	JdbcTemplate db;

	private CartController cartController;
	private ShopViewSwing itemsViewSwing;
	private HistoryViewSwing historyViewSwing;

	private DialogFixture window;
	private JDialog historyDialog;

	private ItemsSqlRepository buildRepository() {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(sqlContainer.getJdbcUrl());
		dataSource.setUsername(sqlContainer.getUsername());
		dataSource.setPassword(sqlContainer.getPassword());

		return new ItemsSqlRepository(dataSource);
	}

	@Override
	protected void onSetUp() {
		Flyway flyway = Flyway.configure()
				.dataSource(sqlContainer.getJdbcUrl(), sqlContainer.getUsername(), sqlContainer.getPassword()).load();
		itemsRepository = buildRepository();
		db = itemsRepository.getJdbcTemplate();
		flyway.clean();
		flyway.migrate();
		GuiActionRunner.execute(
				()->{
					historyDialog = new JDialog();
					historyViewSwing = new HistoryViewSwing();
					historyDialog.setName("History");
					historyDialog.getContentPane().add(historyViewSwing);
					cartController = new CartController(itemsViewSwing,itemsRepository,historyViewSwing);
					historyViewSwing.setCartController(cartController);
					historyDialog.pack();
					historyDialog.setLocationRelativeTo(null);
				});
		window = new DialogFixture(robot(),historyDialog);
		Dimension d = new Dimension(WIDTH, HEIGHT);

		window.show(d);		
	}
	
	@Test @GUITest
	public void testAllCarts() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		itemsRepository.store(item1);
		itemsRepository.store(item2);
		itemsRepository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()
				);
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());
	}
	
	@Test @GUITest
	public void testAllCartItems() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		itemsRepository.store(item1);
		itemsRepository.store(item2);
		itemsRepository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		GuiActionRunner.execute(
				()-> cartController.allItemsCart(cart)
				);
		assertThat(window.list("listItemsCart").contents()).containsExactly(cart.getItems().get(FIRST_ITEM).toString(),
				cart.getItems().get(SECOND_ITEM).toString());
	}
	
	@Test @GUITest
	public void testRemoveButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		itemsRepository.store(item1);
		itemsRepository.store(item2);
		itemsRepository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()		
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("listCart").contents()).isEmpty();
	}
	
	@Test @GUITest
	public void testRemoveButtonError() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Cart> carts = historyViewSwing.getListCartModel();
					carts.addElement(cart);
				});
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());
	}	
}
