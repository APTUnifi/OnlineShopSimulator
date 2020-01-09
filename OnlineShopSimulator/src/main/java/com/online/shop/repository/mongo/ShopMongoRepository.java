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

	private static final String ITEMS = "items";
	private static final String DATE = "date";
	private static final String LABEL = "label";
	private static final String QUANTITY = "quantity";
	private static final String NAME = "name";
	private static final String PRODUCT_CODE = "productCode";
	private MongoCollection<Document> collectionItems;
	private MongoCollection<Document> collectionCarts;

	public ShopMongoRepository(MongoClient client, String databaseName, String itemsCollection,
			String cartsCollection) {
		collectionItems = client.getDatabase(databaseName).getCollection(itemsCollection);
		collectionCarts = client.getDatabase(databaseName).getCollection(cartsCollection);
	}

	private Item fromDocumentToItem(Document d) {
		return new Item("" + d.get(PRODUCT_CODE), "" + d.get(NAME), (int) d.get(QUANTITY));
	}

	@SuppressWarnings("unchecked")
	private Cart fromDocumentToCart(Document d) {
		List<Item> items = new ArrayList<>();
		List<Document> documents = (List<Document>) (d.get(ITEMS));

		for (Document item : documents) {
			items.add(fromDocumentToItem(item));
		}
		return new Cart("" + d.get(LABEL), "" + d.get(DATE), items);

	}

	@Override
	public List<Item> findAllItems() {
		return StreamSupport.stream(collectionItems.find().spliterator(), false).map(this::fromDocumentToItem)
				.collect(Collectors.toList());
	}

	@Override
	public Item findItemByProductCode(String productCode) {
		Document d = collectionItems.find(Filters.eq(PRODUCT_CODE, productCode)).first();
		if (d != null)
			return fromDocumentToItem(d);
		return null;
	}

	@Override
	public Item findItemByName(String name) {
		Document d = collectionItems.find(Filters.eq(NAME, name)).first();
		if (d != null)
			return fromDocumentToItem(d);
		return null;
	}

	@Override
	public void storeItem(Item itemToAdd) {
		collectionItems.insertOne(new Document().append(PRODUCT_CODE, itemToAdd.getProductCode())
				.append(NAME, itemToAdd.getName()).append(QUANTITY, itemToAdd.getQuantity()));
	}

	@Override
	public void removeItem(String productCode) {
		collectionItems.deleteOne(Filters.eq(PRODUCT_CODE, productCode));
	}

	@Override
	public void modifyItemQuantity(Item itemToBeModified, int modifier) {
		int newQuantity = itemToBeModified.getQuantity() + modifier;
		collectionItems.updateOne(Filters.eq(PRODUCT_CODE, itemToBeModified.getProductCode()),
				Updates.set(QUANTITY, newQuantity));
	}

	@Override
	public void storeCart(Cart cartToStore) {
		List<Document> list = new ArrayList<>();
		for (Item item : cartToStore.getItems()) {
			list.add(new Document().append(PRODUCT_CODE, item.getProductCode()).append(NAME, item.getName())
					.append(QUANTITY, item.getQuantity()));
		}
		collectionCarts.insertOne(new Document().append(LABEL, cartToStore.getLabel())
				.append(DATE, cartToStore.getDate()).append(ITEMS, list));

	}

	@Override
	public Cart findCart(String date, String label) {
		Document d = collectionCarts.find(Filters.and(Filters.eq(DATE, date), Filters.eq(LABEL, label))).first();
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
		collectionCarts.deleteOne(Filters.and(Filters.eq(DATE, date), Filters.eq(LABEL, label)));
	}
}