package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.online.shop.controller.CartController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ShopMongoRepository;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class HistoryViewPanelIT  extends AssertJSwingJUnitTestCase{

	private static final String ITEM_FIXTURE_NAME_2 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";
	private static final String CART_FIXTURE_LABEL_1 = "test";
	private static final int HEIGHT = 400;
	private static final int WIDTH = 800;
	private static final int FIRST_ITEM = 0;
	private static final int SECOND_ITEM = 1;

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	private static MongoServer server;	
	private MongoClient mongoClient;
	private JPanelFixture window;
	private static InetSocketAddress serverAddress;

	private CartController cartController;
	private ShopMongoRepository shopRepository;
	private ShopViewPanel shopViewPanel;
	private HistoryViewPanel historyViewPanel;
	private JFrame frame;

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
		shopRepository = new ShopMongoRepository(mongoClient);
		for(Item item : shopRepository.findAllItems()) {
			shopRepository.removeItem(item.getProductCode());
		}
		for(Cart cart: shopRepository.findAllCarts()) {
			shopRepository.removeCart(cart.getDate(),cart.getLabel());
		}
		GuiActionRunner.execute(
				()->{
					shopViewPanel = new ShopViewPanel();
					historyViewPanel = new HistoryViewPanel();
					Dimension dimension = new Dimension(WIDTH, HEIGHT);
					frame = new JFrame();
					cartController = new CartController(shopViewPanel,shopRepository,historyViewPanel);
					historyViewPanel.setCartController(cartController);
					frame.setPreferredSize(dimension);
					frame.add(historyViewPanel);
					frame.pack();
					frame.setVisible(true);
				});
		window = new JPanelFixture(robot(),historyViewPanel);
	}	

	@Test @GUITest
	public void testAllCarts() {
		Item itemExist1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item itemExist2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cartExist = new Cart(Arrays.asList(itemExist1,itemExist2),CART_FIXTURE_LABEL_1);
		shopRepository.storeItem(itemExist1);
		shopRepository.storeItem(itemExist2);
		shopRepository.storeCart(cartExist);
		GuiActionRunner.execute(
				()-> cartController.allCarts()
				);
		assertThat(window.list("listCart").contents()).containsExactly(cartExist.toString());
	}
	
	@Test @GUITest
	public void testAllItemsCart() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		shopRepository.storeItem(item1);
		shopRepository.storeItem(item2);
		shopRepository.storeCart(cart);
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
	public void testDeleteButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		shopRepository.storeItem(item1);
		shopRepository.storeItem(item2);
		shopRepository.storeCart(cart);
		GuiActionRunner.execute(
				()-> cartController.allCarts()		
				);
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnDelete")).click();
		assertThat(window.list("listCart").contents()).isEmpty();
	}

	@Test @GUITest
	public void testDeleteButtonError() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2);
		Cart cart = new Cart(Arrays.asList(item1,item2),CART_FIXTURE_LABEL_1);
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Cart> carts = historyViewPanel.getListCartModel();
					carts.addElement(cart);
				});
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnDelete")).click();
		assertThat(window.list("listCart").contents()).containsExactly(cart.toString());
		window.label("errorMessageLabel").requireText("Cart not found: " + cart.getLabel());
	}
}
