package com.online.shop.repository;

import static org.assertj.core.api.Assertions.*;

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
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ItemsMongoRepository;

public class ItemsMongoRepositoryIT {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	private static final String SHOP_DB_NAME = "shop";
	private static final String ITEMS_COLLECTION_NAME = "items";

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
		addTestItemToRepository("1",1);
		addTestItemToRepository("2",1);
		assertThat(itemsRepository.findAll()).containsExactly(new Item("1",1), new Item("2",1));
		
	}

	private void addTestItemToRepository(String productCode, Integer quantity) {
		items.insertOne(new Document().append("productCode", productCode).append("quantity", quantity));		
	}
}
