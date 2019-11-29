package com.online.shop.repository;

import static org.assertj.core.api.Assertions.*;

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
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ItemsMongoRepository;

public class ItemsMongoRepositoryIT {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	private static final String SHOP_DB_NAME = "shop";
	private static final String ITEMS_COLLECTION_NAME = "items";
	
	private static final int STARTER_QUANTITY = 2;
	private static final int QUANTITY_MODIFIER = 1;

	private MongoClient client;
	private ItemsMongoRepository itemsRepository;
	private MongoCollection<Document> items;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		itemsRepository = new ItemsMongoRepository(client);
		MongoDatabase db = client.getDatabase(SHOP_DB_NAME);
		db.drop(); // clean db
		items = db.getCollection(ITEMS_COLLECTION_NAME);
	}
	
	@After
	public void close() {
		client.close();
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
		assertThat(itemsRepository.findByProductCode("1")).isEqualTo(new Item("1","test1"));
	}
	
	@Test
	public void testFindByName() {
		addTestItemToRepository("1", "test1");
		addTestItemToRepository("2", "test2");
		assertThat(itemsRepository.findByName("test2")).isEqualTo(new Item("2","test2"));
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
		addTestItemToRepository(itemToBeModified.getProductCode(),
								itemToBeModified.getName(),
								itemToBeModified.getQuantity());
		itemsRepository.modifyQuantity(itemToBeModified, QUANTITY_MODIFIER);
		assertThat(retrieveItem("1").getQuantity()).isEqualTo(STARTER_QUANTITY + QUANTITY_MODIFIER);
	}

	private Item retrieveItem(String productCode) {
		Document d = items.find(Filters.eq("productCode", productCode)).first();
		if (d != null)
			return new Item(""+d.get("productCode"), ""+d.get("name"), (int)d.get("quantity"));
		return null;
	}

	private List<Item> retrieveAllItems() {
		return StreamSupport.stream(items.find().spliterator(), false)
				.map(d -> new Item(""+d.get("productCode"), ""+d.get("name"), (int)d.get("quantity")))
				.collect(Collectors.toList());
	}
	
	private void addTestItemToRepository(String productCode, String name) {
		items.insertOne(new Document().append("productCode", productCode).append("name", name).append("quantity", 1));		
	}

	private void addTestItemToRepository(String productCode, String name, int quantity) {
		items.insertOne(new Document().append("productCode", productCode).append("name", name).append("quantity", quantity));		
	}
}
