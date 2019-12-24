package com.online.shop.repository.mongo;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ItemsMongoRepository;

public class ItemsMongoRepositoryIT {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	private static final String SHOP_DB_NAME = "shop";
	private static final String ITEMS_COLLECTION_NAME = "items";
	private static final String CARTS_COLLECTION_NAME = "carts";

	private static final int STARTER_QUANTITY = 2;
	private static final int QUANTITY_MODIFIER = 1;

	private MongoClient client;
	private ItemsMongoRepository itemsRepository;
	private MongoCollection<Document> collectionItems;
	private MongoCollection<Document> collectionCarts;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		itemsRepository = new ItemsMongoRepository(client);
		MongoDatabase db = client.getDatabase(SHOP_DB_NAME);
		db.drop(); // clean db
		collectionItems = db.getCollection(ITEMS_COLLECTION_NAME);
		collectionCarts = db.getCollection(CARTS_COLLECTION_NAME);
	}

	@After
	public void close() {
		client.close();
	}

	private Item retrieveItem(String productCode) {
		Document d = collectionItems.find(Filters.eq("productCode", productCode)).first();
		if (d != null)
			return new Item("" + d.get("productCode"), "" + d.get("name"), (int) d.get("quantity"));
		return null;
	}

	private List<Item> retrieveAllItems() {
		return StreamSupport.stream(collectionItems.find().spliterator(), false)
				.map(d -> new Item("" + d.get("productCode"), "" + d.get("name"), (int) d.get("quantity")))

				.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private List<Cart> retrieveAllCarts() {

		return StreamSupport.stream(collectionCarts.find().spliterator(), false)
				.map(d -> new Cart("" + d.get("label"), "" + d.get("date"), (List<Item>) d.get("items")))
				.collect(Collectors.toList());
	}

	private void addTestItemToRepository(String productCode, String name) {
		collectionItems.insertOne(
				new Document().append("productCode", productCode).append("name", name).append("quantity", 1));
	}

	private void addTestItemToRepository(String productCode, String name, int quantity) {
		collectionItems.insertOne(
				new Document().append("productCode", productCode).append("name", name).append("quantity", quantity));
	}

	private void addTestCartToRepository(String label, String date, List<Item> items) {
		List<Document> list = new ArrayList<>();
		for (Item item : items) {
			list.add(new Document().append("productCode", item.getProductCode()).append("name", item.getName())
					.append("quantity", item.getQuantity()));
		}
		collectionCarts.insertOne(new Document().append("label", label).append("date", date).append("items", list));
	}

	@Test
	public void testFindAll() {
		addTestItemToRepository("1", "test1");
		addTestItemToRepository("2", "test2");
		assertThat(itemsRepository.findAll()).containsExactly(new Item("1", "test1"), new Item("2", "test2"));
	}

	@Test
	public void testFindByProductCode() {
		addTestItemToRepository("1", "test1");
		addTestItemToRepository("2", "test2");
		assertThat(itemsRepository.findByProductCode("1")).isEqualTo(new Item("1", "test1"));
	}

	@Test
	public void testFindByName() {
		addTestItemToRepository("1", "test1");
		addTestItemToRepository("2", "test2");
		assertThat(itemsRepository.findByName("test2")).isEqualTo(new Item("2", "test2"));
	}

	@Test
	public void testStore() {
		Item itemToAdd = new Item("1", "test1");
		itemsRepository.store(itemToAdd);
		assertThat(retrieveAllItems()).containsExactly(itemToAdd);
	}

	@Test
	public void testRemove() {
		addTestItemToRepository("1", "test1");
		itemsRepository.remove("1");
		assertThat(retrieveAllItems()).isEmpty();
	}

	@Test
	public void testModifyQuantityWhenModifierIsPositive() {
		Item itemToBeModified = new Item("1", "test1", STARTER_QUANTITY);
		addTestItemToRepository(itemToBeModified.getProductCode(), itemToBeModified.getName(),
				itemToBeModified.getQuantity());
		itemsRepository.modifyQuantity(itemToBeModified, QUANTITY_MODIFIER);
		assertThat(retrieveItem("1").getQuantity()).isEqualTo(STARTER_QUANTITY + QUANTITY_MODIFIER);
	}

	@Test
	public void testFindAllCarts() {
		addTestCartToRepository("testCart", LocalDate.now().toString(), Arrays.asList(new Item("1", "test1")));
		assertThat(itemsRepository.findAllCarts())
		.containsExactly(new Cart(Arrays.asList(new Item("1", "test1")), "testCart"));

	}

	@Test
	public void testFindCart() {
		addTestCartToRepository("testCart1", LocalDate.now().toString(), Arrays.asList(new Item("1", "test1")));
		addTestCartToRepository("testCart2", LocalDate.now().toString(), Arrays.asList(new Item("1", "test1")));
		assertThat(itemsRepository.findCart( LocalDate.now().toString(),"testCart2"))
		.isEqualTo(new Cart(Arrays.asList(new Item("1", "test1")), "testCart2"));

	}

	@Test
	public void testRemoveCart() {
		addTestCartToRepository("testCart", LocalDate.now().toString(), Arrays.asList(new Item("1", "test1")));

		itemsRepository.removeCart(LocalDate.now().toString(),"testCart");

		assertThat(retrieveAllCarts()).isEmpty();
	}

	@Test
	public void testStoreNewCartShouldAddCartToRepository() {
		Item itemToBeModified = new Item("1", "test1", STARTER_QUANTITY);
		addTestItemToRepository(itemToBeModified.getProductCode(), itemToBeModified.getName(),
				itemToBeModified.getQuantity());
		Cart cartToStore = new Cart("testCart", LocalDate.now().toString(), Arrays.asList(itemToBeModified));
		itemsRepository.storeCart(cartToStore);
		assertThat(retrieveAllCarts()).containsExactly(new Cart("testCart", LocalDate.now().toString(), Arrays.asList(new Item("1", "test1"))));
	}
}

