package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
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

	ItemsSqlRepository repository;
	JdbcTemplate db;

	private CartController cartController;
	private ItemsViewSwing itemsViewSwing;
	private HistoryViewSwing historyViewSwing;

	private FrameFixture window;


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

		repository = buildRepository();
		db = repository.getJdbcTemplate();

		flyway.clean();
		flyway.migrate();


		GuiActionRunner.execute(
				()->{
					itemsViewSwing = new ItemsViewSwing();
					historyViewSwing = new HistoryViewSwing();
					cartController = new CartController(itemsViewSwing,repository,historyViewSwing);
					historyViewSwing.setCartController(cartController);
					return historyViewSwing;
				});
		window = new FrameFixture(robot(),historyViewSwing);
		window.show();		
	}


	@Test @GUITest
	public void testAllCarts() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2),"test");
		repository.store(item1);
		repository.store(item2);
		repository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()
				);
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());
	}
	@Test @GUITest
	public void testAllCartItems() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2),"test");
		repository.store(item1);
		repository.store(item2);
		repository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()

				);
		window.list("listCart").selectItem(0);
		GuiActionRunner.execute(
				()-> cartController.allItemsCart(cart)
				);
		assertThat(window.list("listItemsCart").contents()).containsExactly(cart.getItems().get(0).toString(),cart.getItems().get(1).toString());
	}
	@Test @GUITest
	public void testRemoveButtonSuccess() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2),"test");
		Cart cart1 = new Cart(Arrays.asList(item1),"test1");
		repository.store(item1);
		repository.store(item2);
		repository.storeCart(cart);
		repository.storeCart(cart1);
		GuiActionRunner.execute(
				()-> cartController.allCarts()		
				);
		window.list("listCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("listCart").contents()).containsExactly(cart1.toString());


	}
	@Test @GUITest
	public void testRemoveButtonError() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2),"test");
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Cart> carts = historyViewSwing.getListCartModel();
					carts.addElement(cart);
				});
		window.list("listCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());


	}	
}







