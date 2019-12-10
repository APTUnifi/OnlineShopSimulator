
package com.online.shop.repository.sql;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;

public class ItemsSqlRepository implements ItemsRepository {

	private JdbcTemplate db;

	public ItemsSqlRepository(DataSource dataSource) {
		db = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Item> findAll() {
		String sql = "SELECT * FROM items";
		return db.query(sql,
				(rs, rowNum) -> new Item(rs.getString("product_code"), rs.getString("name"), rs.getInt("quantity")));
	}

	@Override
	public Item findByProductCode(String productCode) {
		String sql = "SELECT * FROM items WHERE product_code = ?";
		
		Item result = null;
		
		try {
			result = db.queryForObject(sql, new Object[] { productCode },
					(rs, rowNum) -> new Item(rs.getString("product_code"), rs.getString("name"), rs.getInt("quantity")));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} 
		
		return result;

	}

	@Override
	public Item findByName(String name) {
		String sql = "SELECT * FROM items WHERE name = ?";
		return db.queryForObject(sql, new Object[] { name },
				(rs, rowNum) -> new Item(rs.getString("product_code"), rs.getString("name"), rs.getInt("quantity")));
	}

	@Override
	public void store(Item itemToAdd) {
		String sql = "INSERT INTO items (product_code, name, quantity) VALUES (?, ?, ?)";
		db.update(sql, itemToAdd.getProductCode(), itemToAdd.getName(), itemToAdd.getQuantity());
	}

	@Override
	public void remove(String productCode) {
		String sql = "DELETE FROM items WHERE product_code = ?";
		db.update(sql, productCode);
	}

	@Override
	public void modifyQuantity(Item itemToBeModified, int modifier) {
		String sql = "UPDATE items SET quantity = ? WHERE product_code = ?";
		int newQuantity = itemToBeModified.getQuantity() + modifier;
		db.update(sql, newQuantity, itemToBeModified.getProductCode());
	}

	@Override
	public void storeCart(Cart cartToStore) {
		String storeCart = "INSERT INTO carts (label, date) VALUES (?, ?)";
		db.update(storeCart, cartToStore.getLabel(), cartToStore.getDate());
		String selectCartId = "SELECT cart_id FROM carts WHERE label = '" + cartToStore.getLabel() + "' AND date = '"
				+ cartToStore.getDate() + "'";

		int cartId = (int) db.queryForObject(selectCartId, Integer.class);

		for (Item item : cartToStore.getItems()) {
			String insertItemIntoCart = "INSERT INTO items_in_cart (cart_id, product_code, quantity_in_cart) VALUES (?, ?, ?)";
			db.update(insertItemIntoCart, cartId, item.getProductCode(), item.getQuantity());

			String selectQuantityToBeModified = "SELECT quantity FROM items WHERE product_code = '" + item.getProductCode() + "'";
			int quantityToBeModified = (int) db.queryForObject(selectQuantityToBeModified, Integer.class);
			int newQuantity = quantityToBeModified - item.getQuantity();
			String updateQuantity = "UPDATE items SET quantity = ? WHERE product_code = ?";
			db.update(updateQuantity, newQuantity, item.getProductCode());
		}
	}

	@Override
	public Cart findCart(String date,String label) {
		Cart requestedCart = new Cart(label, date);

		String selectCartId = "SELECT cart_id FROM carts WHERE label = '" + label + "' AND date = '" + date +"'";
		int cartId = (int) db.queryForObject(selectCartId, Integer.class);

		List<Item> items = new ArrayList<>();

		String selectItemsInCart = "SELECT * FROM items_in_cart IC, items I WHERE IC.cart_id = '" + cartId +"'";

		items = db.query(selectItemsInCart, (rs, rowNum) -> new Item(rs.getString("product_code"),
				rs.getString("name"), rs.getInt("quantity_in_cart")));

		requestedCart.setItems(items);

		return requestedCart;
	}

	@Override
	public List<Cart> findAllCarts() {
		List<Cart> allCarts = new ArrayList<>();

		String selectAllCarts = "SELECT * FROM carts";
		allCarts = db.query(selectAllCarts,
				(rs, rowNum) -> new Cart(rs.getString("label"), rs.getString("date")));

		for (Cart cart : allCarts) {
			List<Item> items = new ArrayList<>();

			String selectItemsInCart = "SELECT * FROM items_in_cart IC, items I WHERE IC.cart_id IN (SELECT cart_id FROM carts WHERE label = '"
					+ cart.getLabel() + "' AND date = '" + cart.getDate() + "') AND I.product_code = IC.product_code";

			items = db.query(selectItemsInCart, (rs, rowNum) -> new Item(rs.getString("product_code"),
					rs.getString("name"), rs.getInt("quantity_in_cart")));

			cart.setItems(items);
		}
		return allCarts;
	}

	@Override
	public void removeCart( String date,String label) {
		String selectCartId = "SELECT cart_id FROM carts WHERE label = '" + label + "' AND date = '"
				+ date + "'";

		int cartID = (int) db.queryForObject(selectCartId, Integer.class);

		String deleteCartFromCarts = "DELETE FROM carts WHERE cart_id = ?";
		db.update(deleteCartFromCarts, cartID);

		String deleteItemsFromItemsInCart = "DELETE FROM items_in_cart WHERE cart_id = ?";
		db.update(deleteItemsFromItemsInCart, cartID);
	}

	public int countItems() {
		return (int) db.queryForObject("SELECT COUNT (*) FROM items", Integer.class);
	}

	public int countCarts() {
		return (int) db.queryForObject("SELECT COUNT (*) FROM carts", Integer.class);
	}

	public JdbcTemplate getJdbcTemplate() {
		return db;
	}
}

