package com.online.shop.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.repository.mongo.ItemsMongoRepository;
import com.online.shop.view.swing.ShopViewTotal;

@Command(mixinStandardHelpOptions = true)
public class ShopSwingApp implements Callable<Void> {

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
		CommandLine.call(new ShopSwingApp(), args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(()->{
			try {
				ItemsMongoRepository itemsRepository = new ItemsMongoRepository(
						new MongoClient(new ServerAddress(mongoHost,mongoPort)),
						databaseName, collectionItems, collectionCarts);
				ShopViewTotal shopView = new ShopViewTotal();
				ShopController shopController = new ShopController(shopView, itemsRepository);
				CartController cartController = new CartController(shopView, itemsRepository,shopView);
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
