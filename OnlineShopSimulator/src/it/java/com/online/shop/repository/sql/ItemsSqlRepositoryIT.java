package com.online.shop.repository.sql;

import static org.junit.Assert.assertEquals; 

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

public class ItemsSqlRepositoryIT {
	
	@ClassRule
	public static final PostgreSQLContainer repo = new PostgreSQLContainer();
	
	@Before
	public void setup() {
		// setting up SQLDatabase
		Flyway flyway = Flyway.configure().dataSource(
				repo.getJdbcUrl(), 
				repo.getUsername(),
				repo.getPassword()).load();
		
		// TODO drop method to clean database
		flyway.migrate();
	}
	
	private ItemsSqlRepository buildRepository() {
		
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(repo.getJdbcUrl());
		dataSource.setUsername(repo.getUsername());
		dataSource.setPassword(repo.getPassword());
		
		return new ItemsSqlRepository(dataSource);
	}
	
	@Test
	public void testEmptyRepository() {
		ItemsSqlRepository itemsSqlRepository = buildRepository();
		assertEquals(0L, itemsSqlRepository.count());
	}

}
