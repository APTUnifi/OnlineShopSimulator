package com.online.shop.repository.sql;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class ItemsSqlRepository {
	
	private JdbcTemplate jdbcTemplate;
	
	public ItemsSqlRepository(DataSource dataSource) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);

	}
	
	public int count() {
		return (int) jdbcTemplate.queryForObject("SELECT COUNT (*) FROM items", Integer.class);
	}
	
}
