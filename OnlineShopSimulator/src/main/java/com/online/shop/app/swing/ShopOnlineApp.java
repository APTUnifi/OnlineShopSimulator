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
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ShopMongoRepository;
import com.online.shop.view.swing.HistoryViewPanel;
import com.online.shop.view.swing.ShopOnlineFrame;
import com.online.shop.view.swing.ShopViewPanel;

@Command(mixinStandardHelpOptions = true)
public class ShopOnlineApp implements Callable<Void> {

	public static final String ITEM_FIXTURE_PRODUCTCODE_1 = "001";

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
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

	private void initDatabase(ShopMongoRepository shop) {
		shop.storeItem(new Item(ITEM_FIXTURE_PRODUCTCODE_1, "Phone", 10));
		shop.storeItem(new Item("002", "Book", 5));
		shop.storeItem(new Item("003", "Shirt", 1));
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				ShopMongoRepository shopRepository = new ShopMongoRepository(
						new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, collectionItems,
						collectionCarts);
				initDatabase(shopRepository);
				ShopViewPanel shopViewPanel = new ShopViewPanel();
				HistoryViewPanel historyViewPanel = new HistoryViewPanel();
				ShopOnlineFrame shopViewFrame = new ShopOnlineFrame(shopViewPanel, historyViewPanel);
				ShopController shopController = new ShopController(shopViewPanel, shopRepository);
				CartController cartController = new CartController(shopViewPanel, shopRepository, historyViewPanel);
				shopViewPanel.setShopController(shopController);
				shopViewPanel.setCartController(cartController);
				historyViewPanel.setCartController(cartController);
				shopController.allItems();
				cartController.allCarts();
				shopViewFrame.setVisible(true);
			} catch (Exception e) {
			}
		});
		return null;
	}
}