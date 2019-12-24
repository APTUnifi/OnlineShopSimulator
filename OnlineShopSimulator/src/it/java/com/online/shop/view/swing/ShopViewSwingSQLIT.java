package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dimension;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.flywaydb.core.Flyway;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Item;
import com.online.shop.repository.sql.ItemsSqlRepository;

public class ShopViewSwingSQLIT extends AssertJSwingJUnitTestCase {

	private static final String CART_FIXTURE_LABEL_TEST = "cartTest";
	private static final int ITEM_FIXTURE_NEW_QUANTITY = 1;
	private static final int ITEM_FIXTURE_QUANTITY_1 = 10;
	private static final int MODIFIER = 1;
	private static final String ITEM_FIXTURE_NAME_2 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";
	private static final int HEIGHT = 300;
	private static final int WIDTH = 500;
	private static final int FIRST_ITEM = 0;
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final PostgreSQLContainer sqlContainer = new PostgreSQLContainer();
	private ItemsSqlRepository itemsRepository;
	private JdbcTemplate db;

	private ShopController shopController;
	private CartController cartController;
	private ShopViewSwing shopViewSwing;
	private HistoryViewSwing historyView;

	private FrameFixture window;
	
	private ItemsSqlRepository buildRepository() {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(sqlContainer.getJdbcUrl());
		dataSource.setUsername(sqlContainer.getUsername());
		dataSource.setPassword(sqlContainer.getPassword());

		return new ItemsSqlRepository(dataSource);
	}

	private void addTestItemToRepository(Item itemToAdd) {
		db.update("INSERT INTO items (product_code, name, quantity) VALUES (?, ?, ?)", itemToAdd.getProductCode(),
				itemToAdd.getName(), itemToAdd.getQuantity());
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
					shopViewSwing = new ShopViewSwing();
					historyView = new HistoryViewSwing();
					shopController = new ShopController(shopViewSwing,itemsRepository);
					cartController = new CartController(shopViewSwing,itemsRepository,historyView);
					shopViewSwing.setCartController(cartController);
					shopViewSwing.setShopController(shopController);
					historyView.setCartController(cartController);
					return shopViewSwing;
				});
		window = new FrameFixture(robot(),shopViewSwing);
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		window.show(dimension);
	}
	@Test @GUITest
	public void testAllItems() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		addTestItemToRepository(item1);
		addTestItemToRepository(item2);
		GuiActionRunner.execute(
				()-> shopController.allItems()
				);
		assertThat(window.list("itemListShop").contents()).containsExactly(item1.toString(),item2.toString());
	}
	
	@Test @GUITest
	public void testAddButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
				});
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY).toString());
	}

	@Test @GUITest
	public void testAddButtonSuccessModifyQuantity() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				});
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY+MODIFIER).toString());
	}

	@Test @GUITest
	public void testAddButtonError() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		addTestItemToRepository(item1);
		GuiActionRunner.execute(
				()-> {
					shopController.allItems();
					cartController.addToCart(item1);
				});
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1).toString());
		JButtonFixture addButton = window.button(JButtonMatcher.withText("Add"));	
		window.list("itemListShop").clearSelection();
		addButton.requireDisabled();
	}

	@Test @GUITest
	public void testRemoveButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY);
		GuiActionRunner.execute(
				()-> {
					cartController.addToCart(item1);
				});
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("itemListCart").contents()).isEmpty();
	}
	
	@Test @GUITest
	public void testRemoveButtonSuccessModifyQuantity() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY+MODIFIER);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				});
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Add")).click();
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY).toString());

	}

	@Test @GUITest
	public void testBuyButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		addTestItemToRepository(item1);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				});
		window.textBox("cartNameText").enterText(CART_FIXTURE_LABEL_TEST);
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Buy")).click();
		assertThat(window.list("itemListCart").contents()).isEmpty();
		assertThat(window.list("itemListShop").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1-MODIFIER).toString());

	}
	@Test @GUITest
	public void testBuyButtonError() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Item> items = shopViewSwing.getItemListCartModel();
					items.addElement(item1);
				});
		window.textBox("cartNameText").enterText(CART_FIXTURE_LABEL_TEST);
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Buy")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(item1.toString());
	}
}
