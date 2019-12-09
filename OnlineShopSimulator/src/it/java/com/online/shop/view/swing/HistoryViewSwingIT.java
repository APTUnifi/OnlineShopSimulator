package com.online.shop.view.swing;


import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;


import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import java.util.Arrays;

import javax.swing.DefaultListModel;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ItemsMongoRepository;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class HistoryViewSwingIT extends AssertJSwingJUnitTestCase {


	@SuppressWarnings("rawtypes")
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	
	private static MongoServer server;	
	private MongoClient mongoClient;
	private FrameFixture window;
	private static InetSocketAddress serverAddress;
	
	private CartController cartController;
	private ItemsMongoRepository itemsRepository;
	private ShopViewSwing shopViewSwing;
	private HistoryViewSwing historyViewSwing;
	

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		itemsRepository = new ItemsMongoRepository(mongoClient);
		for(Cart cart: itemsRepository.findAllCarts()) {
			itemsRepository.removeCart(cart.getLabel(), cart.getDate());
	
		}
		GuiActionRunner.execute(
				()->{
					shopViewSwing = new ShopViewSwing();
					historyViewSwing = new HistoryViewSwing();
					cartController = new CartController(shopViewSwing,itemsRepository,historyViewSwing);
					historyViewSwing.setCartController(cartController);
					return historyViewSwing;
			});
		window = new FrameFixture(robot(),historyViewSwing);
		window.show();
	}		
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testAllCarts() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2),"test");
		itemsRepository.store(item1);
		itemsRepository.store(item2);
		itemsRepository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()
		);
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());
	}
	@Test @GUITest
	public void testAllCartItems() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2),"test");
		itemsRepository.store(item1);
		itemsRepository.store(item2);
		itemsRepository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()
				
		);
		window.list("listCart").selectItem(0);
		GuiActionRunner.execute(
				()-> cartController.allItemsCart(cart)
		);
		assertThat(window.list("listItemsCart").contents()).containsExactly(cart.getItems().get(0).toString(),cart.getItems().get(1).toString());
	}
	@Test @GUITest
	public void testRemoveButtonSuccess() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2),"test");
		Cart cart1 = new Cart(Arrays.asList(item1),"test1");
		itemsRepository.store(item1);
		itemsRepository.store(item2);
		itemsRepository.storeCart(cart);
		itemsRepository.storeCart(cart1);
		GuiActionRunner.execute(
				()-> cartController.allCarts()		
		);
		window.list("listCart").selectItem(1);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());


	}
	@Test @GUITest
	public void testRemoveButtonError() {
		Item item1 = new Item("2","Iphone");
		Item item2 = new Item("1","Sa");
		Cart cart = new Cart(Arrays.asList(item1,item2),"test");
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Cart> carts = historyViewSwing.getListCartModel();
					carts.addElement(cart);
				});
		window.list("listCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());

		
	}	
}
