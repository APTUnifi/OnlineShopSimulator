package com.online.shop.view.swing;


import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dimension;
import java.net.InetSocketAddress;


import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;

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


	private static final int HEIGHT = 300;
	private static final int WIDTH = 500;
	private static final int SECOND_ITEM = 1;
	private static final int FIRST_ITEM = 0;
	private static final String CART_FIXTURE_LABEL_1 = "test";
	private static final String ITEM_FIXTURE_NAME_2 = "test2";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";

	@SuppressWarnings("rawtypes")
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	private static MongoServer server;	
	private MongoClient mongoClient;
	private DialogFixture window;
	private static InetSocketAddress serverAddress;

	private CartController cartController;
	private ItemsMongoRepository itemsRepository;
	private ShopViewSwing itemViewSwing;
	private HistoryViewSwing historyViewSwing;
	private JDialog historyDialog;

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
			itemsRepository.removeCart(cart.getDate(),cart.getLabel());

		}
		GuiActionRunner.execute(
				()->{
					historyDialog = new JDialog();
					historyViewSwing = new HistoryViewSwing();
					cartController = new CartController(itemViewSwing,itemsRepository,historyViewSwing);
					historyDialog.getContentPane().add(historyViewSwing);
					historyDialog.setName("History");
					historyViewSwing.setCartController(cartController);
					historyDialog.pack();
					historyDialog.setLocationRelativeTo(null);
					return historyDialog;
				});
		window = new DialogFixture(robot(),historyDialog);
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		window.show(dimension);
	}		

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testAllCarts() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
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
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		itemsRepository.store(item1);
		itemsRepository.store(item2);
		itemsRepository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		GuiActionRunner.execute(
				()-> cartController.allItemsCart(cart)
				);
		assertThat(window.list("listItemsCart").contents()).containsExactly(cart.getItems().get(FIRST_ITEM).toString(),
				cart.getItems().get(SECOND_ITEM).toString());
	}
	
	@Test @GUITest
	public void testRemoveButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		itemsRepository.store(item1);
		itemsRepository.store(item2);
		itemsRepository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()		
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("listCart").contents()).isEmpty();
	}
	
	@Test @GUITest
	public void testRemoveButtonError() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Cart> carts = historyViewSwing.getListCartModel();
					carts.addElement(cart);
				});
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());
	}	
}
