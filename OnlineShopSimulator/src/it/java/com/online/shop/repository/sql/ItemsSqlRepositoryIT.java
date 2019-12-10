package com.online.shop.repository.sql;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.online.shop.model.Cart;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.online.shop.model.Item;

public class ItemsSqlRepositoryIT {

	@ClassRule
	public static final PostgreSQLContainer sqlContainer = new PostgreSQLContainer();

	ItemsSqlRepository repository;
	private JdbcTemplate db;

	private static final int STARTER_QUANTITY = 2;
	private static final int QUANTITY_MODIFIER = 1;

	@Before
	public void setup() {
		// setting up SQLDatabase
		Flyway flyway = Flyway.configure()
				.dataSource(sqlContainer.getJdbcUrl(), sqlContainer.getUsername(), sqlContainer.getPassword()).load();

		repository = buildRepository();
		db = repository.getJdbcTemplate();

		flyway.clean();
		flyway.migrate();
	}

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

	private void addTestCartToRepository(Cart cartToAdd) {

		// insert cart inside table carts
		db.update("INSERT INTO carts (label, date) VALUES (?, ?)", cartToAdd.getLabel(), cartToAdd.getDate());

		String selectCartId = "SELECT cart_id FROM carts WHERE label = '" + cartToAdd.getLabel() + "' AND date = '"
				+ cartToAdd.getDate() + "'";

		int cartID = (int) db.queryForObject(selectCartId, Integer.class);

		// insert each item in list into the table items_in_cart
		for (Item item : cartToAdd.getItems()) {
			db.update("INSERT INTO items_in_cart (cart_id, product_code, quantity_in_cart) VALUES (?, ?, ?)", cartID,
					item.getProductCode(), item.getQuantity());
		}
	}

	private List<Item> retrieveAllItems() {
		String sql = "SELECT * FROM items";
		return db.query(sql,
				(rs, rowNum) -> new Item(rs.getString("product_code"), rs.getString("name"), rs.getInt("quantity")));
	}

	private List<Cart> retrieveAllCarts() {
		String selectAllCarts = "SELECT * FROM carts";
		return db.query(selectAllCarts,
				(rs, rowNum) -> new Cart(rs.getString("label"), rs.getString("date")));
	}

	private Item retrieveItem(String productCode) {
		String sql = "SELECT * FROM items WHERE product_code = ?";
		return db.queryForObject(sql, new Object[] { productCode },
				(rs, rowNum) -> new Item(rs.getString("product_code"), rs.getString("name"), rs.getInt("quantity")));
	}

	private List<Item> retrieveAllItemsInCart(String label, String date) {
		String selectItemsInCart = "SELECT * FROM items_in_cart IC, items I WHERE IC.cart_id IN (SELECT cart_id FROM carts WHERE label = '"
				+ label + "' AND date = '" + date + "') AND I.product_code = IC.product_code";
		return db.query(selectItemsInCart, (rs, rowNum) -> new Item(rs.getString("product_code"),
				rs.getString("name"), rs.getInt("quantity_in_cart")));

	}

	@Test
	public void testCountItemsWhenTableItemsIsEmpy() {
		assertThat(repository.countItems()).isEqualTo(0);
	}

	@Test
	public void testCountCartsWhenTableCartsIsEmpy() {
		assertThat(repository.countCarts()).isEqualTo(0);
	}

	@Test
	public void testFindAll() {
		addTestItemToRepository(new Item("1", "test1", 1));
		addTestItemToRepository(new Item("2", "test2", 1));

		assertThat(repository.findAll()).containsExactly(new Item("1", "test1", 1), new Item("2", "test2", 1));
	}

	@Test
	public void testFindByProductCode() {
		addTestItemToRepository(new Item("1", "test1", 1));
		addTestItemToRepository(new Item("2", "test2", 1));

		assertThat(repository.findByProductCode("1")).isEqualTo(new Item("1", "test1", 1));
	}

	@Test
	public void testFindByName() {
		addTestItemToRepository(new Item("1", "test1", 1));
		addTestItemToRepository(new Item("2", "test2", 1));

		assertThat(repository.findByName("test2")).isEqualTo(new Item("2", "test2", 1));
	}

	@Test
	public void testStore() {
		Item itemToAdd = new Item("1", "test1", 1);

		repository.store(itemToAdd);

		assertThat(retrieveAllItems()).containsExactly(itemToAdd);
	}

	@Test
	public void testRemove() {
		addTestItemToRepository(new Item("1", "test1", 1));

		repository.remove("1");

		assertThat(retrieveAllItems()).isEmpty();
	}

	@Test
	public void testModifyQuantity() {
		Item itemToBeModified = new Item("1", "test1", STARTER_QUANTITY);
		addTestItemToRepository(itemToBeModified);

		repository.modifyQuantity(itemToBeModified, QUANTITY_MODIFIER);
		assertThat(retrieveItem("1").getQuantity()).isEqualTo(STARTER_QUANTITY + QUANTITY_MODIFIER);
	}

	@Test
	public void testFindAllCarts() {
		Item itemToAdd = new Item("1", "test", 1);
		addTestItemToRepository(itemToAdd);
		Cart cartToAdd = new Cart(Arrays.asList(itemToAdd), "testCart");
		addTestCartToRepository(cartToAdd);

		assertThat(repository.findAllCarts()).containsExactly(new Cart(Arrays.asList(new Item("1","test", 1)), "testCart"));
	}

	@Test
	public void testRemoveCartFromTableCarts() {
		Item itemToAdd = new Item("1", "test", 1);
		addTestItemToRepository(itemToAdd);
		Cart cartToRemove = new Cart(Arrays.asList(itemToAdd), "testCart");
		addTestCartToRepository(cartToRemove);

		repository.removeCart(LocalDate.now().toString(),"testCart");

		assertThat(retrieveAllCarts()).isEmpty();
	}

	@Test
	public void testRemoveCartIfAlsoAllItemsInItAreDropped() {
		Item itemToAdd = new Item("1", "test", 1);
		addTestItemToRepository(itemToAdd);
		Cart cartToRemove = new Cart(Arrays.asList(itemToAdd), "testCart");
		addTestCartToRepository(cartToRemove);

		repository.removeCart(cartToRemove.getDate(),cartToRemove.getLabel());

		assertThat(retrieveAllItemsInCart("testCart", LocalDate.now().toString())).isEmpty();
	}

	@Test
	public void testFindCart() {
		Item itemToAdd = new Item("1", "test", 1);
		addTestItemToRepository(itemToAdd);
		Cart cartToFind = new Cart(Arrays.asList(itemToAdd), "testCart");
		addTestCartToRepository(cartToFind);

		assertThat(repository.findCart(cartToFind.getDate(),cartToFind.getLabel())).isEqualTo(new Cart(Arrays.asList(new Item("1", "test", 1)), "testCart"));
	}

	@Test
	public void testStoreCartSavingNewCartInCartsTable() {


		Cart cartToStore = new Cart();

		repository.storeCart(cartToStore);

		assertThat(retrieveAllCarts()).containsExactly(new Cart());
	}

	@Test
	public void testStoreCartAlsoSavingItemsInItemsInCartTable() {
		Item itemToAdd = new Item("1", "test", 1);
		addTestItemToRepository(itemToAdd);
		Cart cartToStore = new Cart(Arrays.asList(itemToAdd), "testCart");

		repository.storeCart(cartToStore);

		assertThat(retrieveAllItemsInCart(cartToStore.getLabel(), cartToStore.getDate())).containsExactly(new Item("1", "test", 1));
	}

	@Test
	public void testStoreCartAlsoUpdateQuantityValueOfItemsLeftInShop() {
		Item itemToAdd = new Item("1", "test", 1);
		addTestItemToRepository(itemToAdd);
		Cart cartToStore = new Cart(Arrays.asList(itemToAdd), "testCart");

		repository.storeCart(cartToStore);

		assertThat(retrieveItem("1").getQuantity()).isEqualTo(0);
	}
}