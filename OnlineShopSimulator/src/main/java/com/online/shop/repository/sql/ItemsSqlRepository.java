package com.online.shop.repository.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
		
		long numItems = 0;
		Connection conn = null;
		ResultSet rs = null;
		
		try {
			conn = DriverManager.getConnection(jdbcUrl, username, password);
			rs = conn.createStatement().executeQuery("SELECT COUNT (*) FROM items");
			rs.next();
			numItems = rs.getInt(1);			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return numItems;
	}
}
