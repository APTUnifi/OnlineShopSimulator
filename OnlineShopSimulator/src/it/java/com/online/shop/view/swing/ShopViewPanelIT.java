package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.time.LocalDate;

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
import com.online.shop.controller.ShopController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ShopMongoRepository;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ShopViewPanelIT extends AssertJSwingJUnitTestCase {

	private static final String CART_FIXTURE_LABEL_TEST = "cartTest";
	private static final String ITEM_FIXTURE_NAME_2 = "test1";
	private static final String ITEM_FIXTURE_PRODUCTCODE_2 = "2";
	private static final String ITEM_FIXTURE_NAME_1 = "test";
	private static final String ITEM_FIXTURE_PRODUCTCODE_1 = "1";
	private static final String ITEM_SEARCHED = "test";
	private static final int ITEM_FIXTURE_NEW_QUANTITY = 1;
	private static final int ITEM_FIXTURE_QUANTITY_1 = 10;
	private static final int MODIFIER = 1;
	private static final int HEIGHT = 400;
	private static final int WIDTH = 800;
	private static final int FIRST_ITEM = 0;
	

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	private static MongoServer server;	
	private MongoClient mongoClient;
	private JPanelFixture window;
	private static InetSocketAddress serverAddress;

	private ShopController shopController;
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
					shopController = new ShopController(shopViewPanel,shopRepository);
					cartController = new CartController(shopViewPanel,shopRepository,historyViewPanel);
					shopViewPanel.setCartController(cartController);
					shopViewPanel.setShopController(shopController);
					frame.setPreferredSize(dimension);
					frame.add(shopViewPanel);
					frame.pack();
					frame.setVisible(true);
				});
		window = new JPanelFixture(robot(),shopViewPanel);
	}	

	@Test @GUITest
	public void testSearchButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2,ITEM_FIXTURE_QUANTITY_1);
		shopRepository.storeItem(item1);
		shopRepository.storeItem(item2);
		GuiActionRunner.execute(
				()-> shopController.allItems()
				);
		window.textBox("itemName").setText(ITEM_SEARCHED);
		window.button(JButtonMatcher.withText("Search")).click();
		assertThat(window.list("itemListShop").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1).toString());
	}
	
	@Test @GUITest
	public void testSearchButtonError() {
			Item item2 = new Item(ITEM_FIXTURE_PRODUCTCODE_2,ITEM_FIXTURE_NAME_2,ITEM_FIXTURE_QUANTITY_1);
			shopRepository.storeItem(item2);
			GuiActionRunner.execute(
					()-> shopController.allItems()
					);
			window.textBox("itemName").setText(ITEM_SEARCHED);
			window.button(JButtonMatcher.withText("Search")).click();
			window.label("errorMessageLabel").requireText("Item with name does not exists: " + ITEM_SEARCHED);
			assertThat(window.list("itemListShop").contents()).containsExactly(
				item2.toString());
	}	

	@Test @GUITest
	public void testAddButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		GuiActionRunner.execute(
				()->shopController.newItem(item1)
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withName("btnAdd")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY).toString());
	}

	@Test @GUITest
	public void testAddButtonSuccessModifyQuantity() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				});
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnAdd")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY+MODIFIER).toString());
	}
	
	@Test @GUITest
	public void testAddButtonError_ModifyQuantity() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY);
		shopRepository.storeItem(item1);
		GuiActionRunner.execute(
				()-> shopController.allItems()
				);
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnAdd")).click();
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnAdd")).click();
		window.label("errorMessageLabel").requireText("Can not add more this item: " + item1.getName());
		assertThat(window.list("itemListCart").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY).toString());
	}

	@Test @GUITest
	public void testRemoveButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY);
		GuiActionRunner.execute(
				()->cartController.addToCart(item1)
				);
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withName("btnRemove")).click();
		assertThat(window.list("itemListCart").contents()).isEmpty();
	}

	@Test @GUITest
	public void testRemoveButtonSuccessModifyQuantity() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY+MODIFIER);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				});
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnAdd")).click();
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnRemove")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_NEW_QUANTITY).toString());
	}

	@Test @GUITest
	public void testBuyButtonSuccess() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				});
		window.textBox("cartNameText").enterText(CART_FIXTURE_LABEL_TEST);
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnBuy")).click();
		assertThat(window.list("itemListCart").contents()).isEmpty();
		assertThat(window.list("itemListShop").contents()).containsExactly(
				new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1-MODIFIER).toString());
	}

	@Test @GUITest
	public void testBuyButtonError() {
		Item item1 = new Item(ITEM_FIXTURE_PRODUCTCODE_1,ITEM_FIXTURE_NAME_1,ITEM_FIXTURE_QUANTITY_1);
		Cart cart = new Cart(CART_FIXTURE_LABEL_TEST, LocalDate.now().toString());
		shopRepository.storeCart(cart);
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Item> items = shopViewPanel.getItemListCartModel();
					items.addElement(item1);
					cartController.allCarts();
				});
		window.textBox("cartNameText").enterText(CART_FIXTURE_LABEL_TEST);
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnBuy")).click();
		window.label("errorMessageLabel").requireText("Cart with this label already exists : " + cart.getLabel());
		assertThat(window.list("itemListCart").contents()).containsExactly(item1.toString());	
	}

}
