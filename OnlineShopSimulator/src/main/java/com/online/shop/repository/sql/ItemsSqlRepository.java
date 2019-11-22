package com.online.shop.repository.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemsSqlRepository {
	
	private final String jdbcUrl;
	private final String username;
	private final String password;
	
	private Logger logger;
	
	public ItemsSqlRepository(String jdbcUrl, String username, String password) {
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;		
		
		logger = Logger.getAnonymousLogger();
	}
	
	public long count() {	
		
		long numItems = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DriverManager.getConnection(jdbcUrl, username, password);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COUNT (*) FROM items");
			rs.next();
			numItems = rs.getInt(1);
			
		} catch (SQLException e) {
			
			logger.log(Level.SEVERE,"SQLException", e);
			
		} finally {
			
			try {
				
				if (rs != null) {
					rs.close();
				}
				
				stmt.close();
				conn.close();
				
			} catch (NullPointerException e) {
				
				logger.log(Level.SEVERE,"NullPointerException", e);
				
			} catch (SQLException e) {
				
				logger.log(Level.SEVERE,"SQLException", e);
				
			}
			
		}
		
		return numItems;
	}
}
