package com.online.shop.repository.sql;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.online.shop.model.Item;

public class ItemsSqlRepositoryIT {
	
	@ClassRule
	public static final PostgreSQLContainer sqlContainer = new PostgreSQLContainer();
	
	ItemsSqlRepository repository;

	private static final int STARTER_QUANTITY = 2;
	private static final int QUANTITY_MODIFIER = 1;  
	
	@Before
	public void setup() {
		// setting up SQLDatabase
		Flyway flyway = Flyway.configure().dataSource(
				sqlContainer.getJdbcUrl(), 
				sqlContainer.getUsername(),
				sqlContainer.getPassword()).load();
		
		repository = buildRepository();

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
		repository.getJdbcTemplate().update("INSERT INTO items (product_code, name, quantity) VALUES (?, ?, ?)", 
											itemToAdd.getProductCode(), 
											itemToAdd.getName(),
											itemToAdd.getQuantity());
	}
	
	private List<Item> retrieveAllItems(ItemsSqlRepository repository){
		String sql = "SELECT * FROM items";
		return repository.getJdbcTemplate().query(sql, 
								 (rs, rowNum) -> new Item(
										 		 rs.getString("product_code"),
										 		 rs.getString("name"),
										 		 rs.getInt("quantity")));
	}
	
	private Item retrieveItem(String productCode) {
		String sql = "SELECT * FROM items WHERE product_code = ?";
		return repository.getJdbcTemplate().queryForObject(sql, new Object[] {productCode}, 
				 						  				  (rs, rowNum) -> new Item(
				 								  		   rs.getString("product_code"),
				 								  		   rs.getString("name"),
				 								  		   rs.getInt("quantity")));
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
		addTestItemToRepository(new Item("1","test1",1));
		addTestItemToRepository(new Item("2","test2",1));
	
		assertThat(repository.findAll()).containsExactly(new Item("1", "test1", 1), new Item("2", "test2", 1));
	}
	
	@Test
	public void testFindByProductCode() {
		addTestItemToRepository(new Item("1","test1",1));
		addTestItemToRepository(new Item("2","test2",1));
		
		assertThat(repository.findByProductCode("1")).isEqualTo(new Item("1", "test1", 1));
	}
	
	@Test
	public void testFindByName() {
		addTestItemToRepository(new Item("1","test1",1));
		addTestItemToRepository(new Item("2","test2",1));
		
		assertThat(repository.findByName("test2")).isEqualTo(new Item("2", "test2", 1));
	}
	
	@Test
	public void testStore() {
		Item itemToAdd = new Item("1", "test1", 1);
		
		repository.store(itemToAdd);
		
		assertThat(retrieveAllItems(repository)).containsExactly(itemToAdd);
	}
	
	@Test
	public void testRemove() {
		addTestItemToRepository(new Item("1","test1",1));
		
		repository.remove("1");
		
		assertThat(retrieveAllItems(repository)).isEmpty();		
	}
	
	@Test
	public void testModifyQuantity() {
		Item itemToBeModified = new Item("1", "test1", STARTER_QUANTITY );
		addTestItemToRepository(itemToBeModified);
		
		repository.modifyQuantity(itemToBeModified, QUANTITY_MODIFIER);
		assertThat(retrieveItem("1").getQuantity()).isEqualTo(STARTER_QUANTITY + QUANTITY_MODIFIER);
	}

}
