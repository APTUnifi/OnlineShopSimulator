package com.online.shop.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;

public class ItemsMongoRepository implements ItemsRepository {
	
	private static final String SHOP_DB_NAME = "shop";
	private static final String ITEMS_COLLECTION_NAME = "items";
	private MongoCollection<Document> items;
	
	public ItemsMongoRepository(MongoClient client) {
		items = client.getDatabase(SHOP_DB_NAME).getCollection(ITEMS_COLLECTION_NAME);
	}
	
	private Item fromDocumentToItem(Document d) {
		return new Item(""+d.get("productCode"), (int)d.get("quantity"));
	}

	@Override
	public List<Item> findAll() {
		return StreamSupport.stream(items.find().spliterator(), false)
							.map(this::fromDocumentToItem)
							.collect(Collectors.toList());
	}

}