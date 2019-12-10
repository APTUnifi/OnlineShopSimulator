package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

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

public class ItemsViewSwingSQLIT extends AssertJSwingJUnitTestCase {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final PostgreSQLContainer sqlContainer = new PostgreSQLContainer();
	private ItemsSqlRepository repository;
	private JdbcTemplate db;

	private ShopController shopController;
	private CartController cartController;
	private ItemsViewSwing itemsViewSwing;
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

		repository = buildRepository();
		db = repository.getJdbcTemplate();

		flyway.clean();
		flyway.migrate();

		GuiActionRunner.execute(
				()->{
					itemsViewSwing = new ItemsViewSwing();
					historyView = new HistoryViewSwing();
					shopController = new ShopController(itemsViewSwing,repository);
					cartController = new CartController(itemsViewSwing,repository,historyView);
					itemsViewSwing.setCartController(cartController);
					itemsViewSwing.setShopController(shopController);
					historyView.setCartController(cartController);
					return itemsViewSwing;
				});
		window = new FrameFixture(robot(),itemsViewSwing);
		window.show();		
	}
	@Test @GUITest
	public void testAllItems() {
		Item item1 = new Item("1","Iphone");
		Item item2 = new Item("2","Samsung");
		repository.store(item1);
		repository.store(item2);

		GuiActionRunner.execute(
				()-> shopController.allItems()
				);
		assertThat(window.list("itemListShop").contents()).containsExactly(item1.toString(),item2.toString());
	}
	@Test @GUITest
	public void testAddButtonSuccess() {
		Item item1 = new Item("1","Iphone",10);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
				}
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		//verify
		assertThat(window.list("itemListCart").contents()).containsExactly(new Item("1","Iphone",1).toString());
	}

	@Test @GUITest
	public void testAddButtonSuccessModifyQuantity() {
		Item item1 = new Item("1","Iphone",10);
		Item item2 = new Item("1","Iphone");

		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);

					cartController.addToCart(item1);
				}
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		//verify
		assertThat(window.list("itemListCart").contents()).containsExactly(new Item("1","Iphone",2).toString());
	}

	@Test @GUITest
	public void testAddButtonError() {
		Item item1 = new Item("1","Iphone",1);
		repository.store(item1);
		GuiActionRunner.execute(
				()-> {
					shopController.allItems();
					cartController.addToCart(item1);

				}
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(new Item("1","Iphone",1).toString());
		JButtonFixture addButton = window.button(JButtonMatcher.withText("Add"));	
		window.list("itemListShop").clearSelection();
		addButton.requireDisabled();
	}

	@Test @GUITest
	public void testRemoveButtonSuccess() {
		Item item1 = new Item("1","Iphone",1);
		GuiActionRunner.execute(
				()-> {
					cartController.addToCart(item1);
				}
				);
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("itemListCart").contents()).isEmpty();

	}
	@Test @GUITest
	public void testRemoveButtonSuccessModifyQuantity() {
		Item item1 = new Item("1","Iphone",10);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				}
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(new Item("1","Iphone",1).toString());

	}
	@Test @GUITest
	public void testBuyButtonSuccess() {
		Item item1 = new Item("1","Iphone",10);
		Item item2 = new Item("1","Iphone",1);
		addTestItemToRepository(item1);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item2);
				});
		window.textBox("cartNameText").enterText("happy");
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Buy")).click();
		//verify
		assertThat(window.list("itemListCart").contents()).isEmpty();
		assertThat(window.list("itemListShop").contents()).containsExactly(new Item("1","Iphone",9).toString());


	}
	@Test @GUITest
	public void testBuyButtonError() {
		Item item1 = new Item("1","Iphone",2);
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Item> items = itemsViewSwing.getItemListCartModel();
					items.addElement(item1);
				});
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Buy")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(item1.toString());
	}

}
