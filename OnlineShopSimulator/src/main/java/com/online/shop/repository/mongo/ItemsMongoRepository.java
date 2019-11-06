package com.online.shop.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
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
		return new Item(""+d.get("productCode"), ""+d.get("name"), (int)d.get("quantity"));
	}

	@Override
	public List<Item> findAll() {
		return StreamSupport.stream(items.find().spliterator(), false)
							.map(this::fromDocumentToItem)
							.collect(Collectors.toList());
	}
	
	@Override
	public Item findByProductCode(String productCode) {
		Document d = items.find(Filters.eq("productCode", productCode)).first();
		if (d != null)
			return fromDocumentToItem(d);
		return null;
	}
	
	@Override	
	public Item findByName(String name) {
		Document d = items.find(Filters.eq("name", name)).first();
		if (d != null)
			return fromDocumentToItem(d);
		return null;
	}
	
	@Override
	public void store(Item itemToAdd) {
		items.insertOne(new Document()
						.append("productCode", itemToAdd.getProductCode())
						.append("name", itemToAdd.getName())
						.append("quantity", itemToAdd.getQuantity()));
	}
	
	@Override
	public void remove(String productCode) {
		items.deleteOne(Filters.eq("productCode", productCode));
	}

	@Override
	public void modifyQuantity(Item itemToBeModified, int modifier) {
		int newQuantity = itemToBeModified.getQuantity() + modifier;
		items.updateOne(Filters.eq("productCode", itemToBeModified.getProductCode()), Updates.set("quantity", newQuantity));	
	}
}