package com.online.shop.repository.sql;

import static org.junit.Assert.assertEquals;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

public class ItemsSqlRepositoryIT {
	
	@ClassRule
	public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();
	
	@Before
	public void setup() {
		// setting up sql database
		Flyway flyway = new Flyway();
		flyway.setDataSource(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(),
				postgreSQLContainer.getPassword());
		// TODO drop method to clean database
		flyway.migrate();
	}
	
	private ItemsSqlRepository buildRepository() {
		return new ItemsSqlRepository(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(),
				postgreSQLContainer.getPassword());
	}
	
	@Test
	public void testEmptyRepository() {
		ItemsSqlRepository itemsSqlRepository = buildRepository();
		assertEquals(0L, itemsSqlRepository.count());
	}

}
