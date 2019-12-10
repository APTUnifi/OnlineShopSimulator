package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Item;
import com.online.shop.repository.mongo.ItemsMongoRepository;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ItemsViewSwingIT extends AssertJSwingJUnitTestCase {

	@SuppressWarnings("rawtypes")
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	private static MongoServer server;	
	private MongoClient mongoClient;
	private FrameFixture window;
	private static InetSocketAddress serverAddress;

	private ShopController shopController;
	private CartController cartController;
	private ItemsMongoRepository itemsRepository;
	private ItemsViewSwing shopViewSwing;
	private HistoryViewSwing historyView;


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
		for(Item item : itemsRepository.findAll()) {
			itemsRepository.remove(item.getProductCode());
		}
		GuiActionRunner.execute(
				()->{
					shopViewSwing = new ItemsViewSwing();
					historyView = new HistoryViewSwing();
					shopController = new ShopController(shopViewSwing,itemsRepository);
					cartController = new CartController(shopViewSwing,itemsRepository,historyView);
					shopViewSwing.setCartController(cartController);
					shopViewSwing.setShopController(shopController);
					historyView.setCartController(cartController);
					return shopViewSwing;
				});
		window = new FrameFixture(robot(),shopViewSwing);
		window.show();
	}		

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testAllItems() {
		Item item1 = new Item("1","Iphone");
		Item item2 = new Item("2","Samsung");
		itemsRepository.store(item1);
		itemsRepository.store(item2);

		GuiActionRunner.execute(
				()-> shopController.allItems()
				);
		assertThat(window.list("itemListShop").contents()).containsExactly(item1.toString(),item2.toString());
	}
	@Test @GUITest
	public void testAddButtonSuccess() {
		Item item1 = new Item("1","Iphone",10);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
				}
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		//verify
		assertThat(window.list("itemListCart").contents()).containsExactly(new Item("1","Iphone",1).toString());
	}

	@Test @GUITest
	public void testAddButtonSuccessModifyQuantity() {
		Item item1 = new Item("1","Iphone",10);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);

				}
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		//verify
		assertThat(window.list("itemListCart").contents()).containsExactly(new Item("1","Iphone",2).toString());
	}

	@Test @GUITest
	public void testAddButtonError() {
		Item item1 = new Item("1","Iphone",1);
		itemsRepository.store(item1);
		GuiActionRunner.execute(
				()-> {
					shopController.allItems();
					cartController.addToCart(item1);

				}
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(new Item("1","Iphone",1).toString());
		JButtonFixture addButton = window.button(JButtonMatcher.withText("Add"));	
		window.list("itemListShop").clearSelection();
		addButton.requireDisabled();
	}

	@Test @GUITest
	public void testRemoveButtonSuccess() {
		Item item1 = new Item("1","Iphone",1);

		GuiActionRunner.execute(
				()-> {
					cartController.addToCart(item1);
				}
				);
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("itemListCart").contents()).isEmpty();

	}
	@Test @GUITest
	public void testRemoveButtonSuccessModifyQuantity() {
		Item item1 = new Item("1","Iphone",10);

		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				}
				);
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(new Item("1","Iphone",1).toString());

	}
	@Test @GUITest
	public void testRemoveButtonError() {

	}
	@Test @GUITest
	public void testBuyButtonSuccess() {
		Item item1 = new Item("1","Iphone",10);
		GuiActionRunner.execute(
				()-> {
					shopController.newItem(item1);
					cartController.addToCart(item1);
				});
		window.textBox("cartNameText").enterText("happy");
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Buy")).click();
		//verify
		assertThat(window.list("itemListCart").contents()).isEmpty();
		assertThat(window.list("itemListShop").contents()).containsExactly(new Item("1","Iphone",9).toString());

	}
	@Test @GUITest
	public void testBuyButtonError() {
		Item item1 = new Item("1","Iphone",2);
		GuiActionRunner.execute(
				()->{ 
					DefaultListModel<Item> items = shopViewSwing.getItemListCartModel();
					items.addElement(item1);
				});
		window.textBox("cartNameText").enterText("happy");
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Buy")).click();
		assertThat(window.list("itemListCart").contents()).containsExactly(item1.toString());
	}
}

