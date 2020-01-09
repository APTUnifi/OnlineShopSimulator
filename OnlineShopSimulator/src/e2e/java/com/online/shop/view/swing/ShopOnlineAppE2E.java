package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.online.shop.model.Item;

@RunWith(GUITestRunner.class)
public class ShopOnlineAppE2E extends AssertJSwingJUnitTestCase {

	private static final int ITEM_FIXTURE_5_QUANTITY = 1;
	private static final int ITEM_FIXTURE_4_QUANTITY = 5;
	private static final int ITEM_FIXTURE_3_QUANTITY = 10;
	private static final String ITEM_FIXTURE_NAME_5 = "Shirt";
	private static final String ITEM_FIXTURE_NAME_4 = "Book";
	private static final String ITEM_FIXTURE_NAME_3 = "Phone";
	private static final String ITEM_FIXTURE_PRODUCTCODE_5 = "003";
	private static final String ITEM_FIXTURE_PRODUCTCODE_4 = "002";
	private static final String ITEM_FIXTURE_PRODUCTCODE_3 = "001";
	private static final String CART_FIXTURE_DATE = LocalDate.now().toString();
	private static final String CART_FIXTURE_LABEL_1 = "cartTest1";
	private static final String CART_FIXTURE_LABEL_2 = "cartTest";


	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.0.5").withExposedPorts(27017);

	private static final String SHOP_DB_NAME = "test-shop";
	private static final String ITEMS_COLLECTION_NAME = "test-items";
	private static final String CARTS_COLLECTION_NAME = "test-carts";
	private static final int FIRST_ITEM = 0;
	private static final int THIRD_ITEM = 2;

	private MongoClient mongoClient;
	private FrameFixture window;
	private List<Document> list;

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getMappedPort(27017);
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		mongoClient.getDatabase(SHOP_DB_NAME).drop();
		list = AddTestItemsToCart(new Item(ITEM_FIXTURE_PRODUCTCODE_3, ITEM_FIXTURE_NAME_3, ITEM_FIXTURE_3_QUANTITY),
				new Item(ITEM_FIXTURE_PRODUCTCODE_4, ITEM_FIXTURE_NAME_4, ITEM_FIXTURE_4_QUANTITY),
				new Item(ITEM_FIXTURE_PRODUCTCODE_5, ITEM_FIXTURE_NAME_5, ITEM_FIXTURE_5_QUANTITY));
		addTestCartToDatabase(CART_FIXTURE_LABEL_1, CART_FIXTURE_DATE, list);
		application("com.online.shop.app.swing.ShopOnlineApp").withArgs("--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(), "--db-name=" + SHOP_DB_NAME,
				"--db-collectionItems=" + ITEMS_COLLECTION_NAME, "--db-collectionCarts=" + CARTS_COLLECTION_NAME)
				.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "ShopOnline".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());

	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	private List<Document> AddTestItemsToCart(Item item1, Item item2, Item item3) {
		list = new ArrayList<>();
		list.add(new Document().append("productCode", item1.getProductCode()).append("name", item1.getName())
				.append("quantity", item1.getQuantity()));
		list.add(new Document().append("productCode", item2.getProductCode()).append("name", item2.getName())
				.append("quantity", item2.getQuantity()));
		list.add(new Document().append("productCode", item3.getProductCode()).append("name", item3.getName())
				.append("quantity", item3.getQuantity()));
		return list;
	}

	private void addTestCartToDatabase(String label, String date, List<Document> list) {
		mongoClient.getDatabase(SHOP_DB_NAME).getCollection(CARTS_COLLECTION_NAME)
				.insertOne(new Document().append("label", label).append("date", date).append("items", list));
	}

	private void removeTestItemFromDatabase(String productCode) {
		mongoClient.getDatabase(SHOP_DB_NAME).getCollection(ITEMS_COLLECTION_NAME)
				.deleteOne(new Document().append("productCode", ITEM_FIXTURE_PRODUCTCODE_3));
	}

	private void removeTestCartFromDatabase(String label, String date) {
		mongoClient.getDatabase(SHOP_DB_NAME).getCollection(CARTS_COLLECTION_NAME)
				.deleteOne(new Document().append("label", label).append("date", date));
	}

	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("itemListShop").contents())
				.anySatisfy(list -> assertThat(list).contains(
						new Item(ITEM_FIXTURE_PRODUCTCODE_3, ITEM_FIXTURE_NAME_3, ITEM_FIXTURE_3_QUANTITY).toString()))
				.anySatisfy(list -> assertThat(list).contains(
						new Item(ITEM_FIXTURE_PRODUCTCODE_4, ITEM_FIXTURE_NAME_4, ITEM_FIXTURE_4_QUANTITY).toString()))
				.anySatisfy(list -> assertThat(list).contains(
						new Item(ITEM_FIXTURE_PRODUCTCODE_5, ITEM_FIXTURE_NAME_5, ITEM_FIXTURE_5_QUANTITY).toString()));
	}

	@Test
	@GUITest
	public void testBuyButtonSuccess() {
		window.list("itemListShop").selectItem(THIRD_ITEM);
		window.button(JButtonMatcher.withName("btnAdd")).click();
		window.textBox("cartNameText").enterText(CART_FIXTURE_LABEL_2);
		window.list("itemListCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnBuy")).click();
		assertThat(window.list("itemListCart").contents()).isEmpty();
		assertThat(window.list("itemListShop").contents())
				.anySatisfy(list -> assertThat(list).contains(
						new Item(ITEM_FIXTURE_PRODUCTCODE_3, ITEM_FIXTURE_NAME_3, ITEM_FIXTURE_3_QUANTITY).toString()))
				.anySatisfy(list -> assertThat(list).contains(
						new Item(ITEM_FIXTURE_PRODUCTCODE_4, ITEM_FIXTURE_NAME_4, ITEM_FIXTURE_4_QUANTITY).toString()));
	}

	@Test
	@GUITest
	public void testBuyButtonError() {
		window.list("itemListShop").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnAdd")).click();
		window.textBox("cartNameText").enterText(CART_FIXTURE_LABEL_2);
		window.list("itemListCart").selectItem(FIRST_ITEM);
		removeTestItemFromDatabase(ITEM_FIXTURE_PRODUCTCODE_3);
		window.button(JButtonMatcher.withName("btnBuy")).click();
		assertThat(window.label("errorMessageLabel").text()).contains("Item/s not found: " + ITEM_FIXTURE_NAME_3);
	}

	@Test
	@GUITest
	public void testRemoveCartSuccess() {
		window.tabbedPane().selectTab("History");
		window.list("listCart").selectItem(FIRST_ITEM);
		window.button(JButtonMatcher.withName("btnDelete")).click();
		assertThat(window.list("listCart").contents()).isEmpty();
		assertThat(window.list("listItemsCart").contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testRemoveCartError() {
		window.tabbedPane().selectTab("History");
		window.list("listCart").selectItem(FIRST_ITEM);
		removeTestCartFromDatabase(CART_FIXTURE_LABEL_1, CART_FIXTURE_DATE);
		window.button(JButtonMatcher.withName("btnDelete")).click();
		assertThat(window.label("errorMessageLabel").text()).contains("Cart not found: " + CART_FIXTURE_LABEL_1);
		assertThat(window.list("listItemsCart").contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testSearchItemSuccess() {
		window.tabbedPane().selectTab("Shop");
		window.textBox("itemName").setText(ITEM_FIXTURE_NAME_3);
		window.button(JButtonMatcher.withText("Search")).click();
		assertThat(window.list("itemListShop").contents()).anySatisfy(list -> assertThat(list).contains(
				new Item(ITEM_FIXTURE_PRODUCTCODE_3, ITEM_FIXTURE_NAME_3, ITEM_FIXTURE_3_QUANTITY).toString()));
	}

	@Test
	@GUITest
	public void testSearchItemError() {
		window.tabbedPane().selectTab("Shop");
		window.textBox("itemName").setText(ITEM_FIXTURE_NAME_3);
		removeTestItemFromDatabase(ITEM_FIXTURE_PRODUCTCODE_3);
		window.button(JButtonMatcher.withText("Search")).click();
		assertThat(window.label("errorMessageLabel").text())
				.contains("Item with name does not exists: " + ITEM_FIXTURE_NAME_3);
		assertThat(window.list("itemListShop").contents())
				.anySatisfy(list -> assertThat(list).contains(
						new Item(ITEM_FIXTURE_PRODUCTCODE_4, ITEM_FIXTURE_NAME_4, ITEM_FIXTURE_4_QUANTITY).toString()))
				.anySatisfy(list -> assertThat(list).contains(
						new Item(ITEM_FIXTURE_PRODUCTCODE_5, ITEM_FIXTURE_NAME_5, ITEM_FIXTURE_5_QUANTITY).toString()));
	}
}