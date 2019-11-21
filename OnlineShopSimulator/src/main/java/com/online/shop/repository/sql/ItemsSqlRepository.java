package com.online.shop.repository.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemsSqlRepository {
	
	private final String jdbcUrl;
	private final String username;
	private final String password;
	
	public ItemsSqlRepository(String jdbcUrl, String username, String password) {
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;		
	}
	
	public long count() {
		try (Connection c = DriverManager.getConnection(jdbcUrl, username, password)) {
			ResultSet rs = c.createStatement().executeQuery("SELECT COUNT (*) FROM items");
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
