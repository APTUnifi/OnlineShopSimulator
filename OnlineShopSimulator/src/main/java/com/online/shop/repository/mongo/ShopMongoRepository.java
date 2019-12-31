package com.online.shop.repository.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.ShopRepository;

public class ShopMongoRepository implements ShopRepository {

	private static final String SHOP_DB_NAME = "shop";
	private static final String ITEMS_COLLECTION_NAME = "items";
	private static final String CARTS_COLLECTION_NAME = "carts";
	private MongoCollection<Document> collectionItems;
	private MongoCollection<Document> collectionCarts;

	public ShopMongoRepository(MongoClient client) {
		collectionItems = client.getDatabase(SHOP_DB_NAME).getCollection(ITEMS_COLLECTION_NAME);
		collectionCarts = client.getDatabase(SHOP_DB_NAME).getCollection(CARTS_COLLECTION_NAME);

	}

	public ShopMongoRepository(MongoClient client, String databaseName, String itemsCollection,
			String cartsCollection) {
		collectionItems = client.getDatabase(databaseName).getCollection(itemsCollection);
		collectionCarts = client.getDatabase(databaseName).getCollection(cartsCollection);
	}

	private Item fromDocumentToItem(Document d) {
		return new Item("" + d.get("productCode"), "" + d.get("name"), (int) d.get("quantity"));
	}

	@SuppressWarnings("unchecked")
	private Cart fromDocumentToCart(Document d) {
		List<Item> items = new ArrayList<>();
		List<Document> documents = (List<Document>) (d.get("items"));

		for (Document item : documents) {
			items.add(fromDocumentToItem(item));
		}
		return new Cart("" + d.get("label"), "" + d.get("date"), items);

	}

	@Override
	public List<Item> findAllItems() {
		return StreamSupport.stream(collectionItems.find().spliterator(), false).map(this::fromDocumentToItem)
				.collect(Collectors.toList());
	}

	@Override
	public Item findItemByProductCode(String productCode) {
		Document d = collectionItems.find(Filters.eq("productCode", productCode)).first();
		if (d != null)
			return fromDocumentToItem(d);
		return null;
	}

	@Override
	public Item findItemByName(String name) {
		Document d = collectionItems.find(Filters.eq("name", name)).first();
		if (d != null)
			return fromDocumentToItem(d);
		return null;
	}

	@Override
	public void storeItem(Item itemToAdd) {
		collectionItems.insertOne(new Document().append("productCode", itemToAdd.getProductCode())
				.append("name", itemToAdd.getName()).append("quantity", itemToAdd.getQuantity()));
	}

	@Override
	public void removeItem(String productCode) {
		collectionItems.deleteOne(Filters.eq("productCode", productCode));
	}

	@Override
	public void modifyItemQuantity(Item itemToBeModified, int modifier) {
		int newQuantity = itemToBeModified.getQuantity() + modifier;
		collectionItems.updateOne(Filters.eq("productCode", itemToBeModified.getProductCode()),
				Updates.set("quantity", newQuantity));
	}

	@Override
	public void storeCart(Cart cartToStore) {
		List<Document> list = new ArrayList<>();
		for (Item item : cartToStore.getItems()) {
			list.add(new Document().append("productCode", item.getProductCode()).append("name", item.getName())
					.append("quantity", item.getQuantity()));
		}
		collectionCarts.insertOne(new Document().append("label", cartToStore.getLabel())
				.append("date", cartToStore.getDate()).append("items", list));

	}

	@Override
	public Cart findCart(String date, String label) {
		Document d = collectionCarts.find(Filters.and(Filters.eq("date", date), Filters.eq("label", label))).first();
		if (d != null)
			return fromDocumentToCart(d);
		return null;
	}

	@Override
	public List<Cart> findAllCarts() {
		return StreamSupport.stream(collectionCarts.find().spliterator(), false).map(this::fromDocumentToCart)
				.collect(Collectors.toList());
	}

	@Override
	public void removeCart(String date, String label) {
		collectionCarts.deleteOne(Filters.and(Filters.eq("date", date), Filters.eq("label", label)));
	}
}