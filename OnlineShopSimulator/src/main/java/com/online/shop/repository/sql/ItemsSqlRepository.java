package com.online.shop.repository.sql;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.ItemsRepository;

public class ItemsSqlRepository implements ItemsRepository {
	
	private JdbcTemplate jdbcTemplate;

	public ItemsSqlRepository(DataSource dataSource) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public List<Item> findAll() {
		String sql = "SELECT * FROM items";
		return jdbcTemplate.query(sql, 
								 (rs, rowNum) -> new Item(
										 		 rs.getString("product_code"),
										 		 rs.getString("name"),
										 		 rs.getInt("quantity")));
	}

	@Override
	public Item findByProductCode(String productCode) {
		String sql = "SELECT * FROM items WHERE product_code = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {productCode}, 
				 						  (rs, rowNum) -> new Item(
				 								  		  rs.getString("product_code"),
				 								  		  rs.getString("name"),
				 								  		  rs.getInt("quantity")));
	}

	@Override
	public Item findByName(String name) {
		String sql = "SELECT * FROM items WHERE name = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {name}, 
				 						  (rs, rowNum) -> new Item(
				 								  		  rs.getString("product_code"),
				 								  		  rs.getString("name"),
				 								  		  rs.getInt("quantity")));
	}

	@Override
	public void store(Item itemToAdd) {
		String sql = "INSERT INTO items (product_code, name, quantity) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, itemToAdd.getProductCode(), itemToAdd.getName(), itemToAdd.getQuantity());
	}

	@Override
	public void remove(String productCode) {
		String sql = "DELETE FROM items WHERE product_code = ?";
		jdbcTemplate.update(sql, productCode);
	}

	@Override
	public void modifyQuantity(Item itemToBeModified, int modifier) {
		String sql = "UPDATE items SET quantity = ? WHERE product_code = ?";
		int newQuantity = itemToBeModified.getQuantity() + modifier;
		jdbcTemplate.update(sql, newQuantity, itemToBeModified.getProductCode());		
	}
	
	public int count() {
		return (int) jdbcTemplate.queryForObject("SELECT COUNT (*) FROM items", Integer.class);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public void storeCart(Cart cartToStore) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCart(String date, String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Cart findCart(String date, String label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cart> findAllCarts() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
