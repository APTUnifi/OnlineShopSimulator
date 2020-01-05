package com.online.shop.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ShopMongoRepository;
import com.online.shop.view.swing.ShopOnlineView;

@Command(mixinStandardHelpOptions = true)
public class ShopOnlineApp implements Callable<Void> {
	
	@Option(names = "0", description = "The item into the database")
	private Item item;

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option (names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "test-shop";

	@Option(names = { "--db-collectionItems" }, description = "Collection items")
	private String collectionItems = "test-items";

	@Option(names = { "--db-collectionCarts" }, description = "Collection carts")
	private String collectionCarts = "test-carts";

	public static void main(String[] args) { 
		new CommandLine(new ShopOnlineApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(()->{
			try {
				ShopMongoRepository shopRepository = new ShopMongoRepository(
						new MongoClient(new ServerAddress(mongoHost,mongoPort)),
						databaseName, collectionItems, collectionCarts);
				ShopOnlineView shopView = new ShopOnlineView();
				ShopController shopController = new ShopController(shopView, shopRepository);
				CartController cartController = new CartController(shopView, shopRepository);
				shopView.setShopController(shopController);
				shopView.setCartController(cartController);
				shopView.setVisible(true);
				shopController.allItems();
				cartController.allCarts();
			}catch(Exception e ) {}
		}	
				);
		return null;
	}
}