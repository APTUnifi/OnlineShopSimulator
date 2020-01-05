package com.online.shop.view.swing;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.view.ShopView;
import javax.swing.JSeparator;


public class ShopOnlineView extends JFrame  implements ShopView{

	private static final long serialVersionUID = 1L;
	private transient ShopController shopController;
	private transient CartController cartController;
	private final JPanel contentPane;

	private JTextField itemName;
	private JTextField cartNameText;

	private DefaultListModel<Item> itemListShopModel;
	private DefaultListModel<Item> itemListCartModel;
	private DefaultListModel<Cart> listCartModel;
	private DefaultListModel<Item> listItemsCartModel;
	private JList<Item> itemListShop;
	private JList<Item> itemListCart;
	private JList<Item> listItemsCart;
	private JList<Cart> listCart;

	private JButton btnBuy;
	private JButton btnDelete;
	private JButton btnRemove;
	private JButton btnAdd;
	private JButton btnSearch;

	private JSeparator separator; 

	private JLabel lblCartName;
	private JLabel lblYourCart;
	private JLabel lblShop;
	private JLabel lblCartsHistory;
	private JLabel lblErrorMessageLabel;
	private JLabel lblItemsCart;
	private JLabel lblPurchaseHistory;

	DefaultListModel<Cart> getListCartModel(){
		return listCartModel;
	}

	DefaultListModel<Item> getItemListShopModel(){
		return itemListShopModel;
	}
	DefaultListModel<Item> getItemListCartModel(){
		return itemListCartModel;
	}
	public void setShopController(ShopController shopController) {
		this.shopController = shopController;
	}

	public void setCartController(CartController cartController) {
		this.cartController = cartController;
	}

	public void setDefaultListModel(DefaultListModel<Cart> a) {
		listCart.setModel(a);
	}

	public ShopOnlineView() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("ShopOnline");
		setBounds(100, 100, 759, 580);
		contentPane = new JPanel();
		contentPane.setName("MainPanel");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		itemName = new JTextField();
		itemName.setBounds(143, 26, 213, 26);
		itemName.setName("itemName");
		contentPane.add(itemName);
		itemName.setColumns(10);

		btnSearch = new JButton("Search");
		btnSearch.setBounds(0, 26, 131, 29);
		btnSearch.setEnabled(true);
		contentPane.add(btnSearch);

		lblErrorMessageLabel = new JLabel(" ");
		lblErrorMessageLabel.setBounds(162, 279, 441, 16);
		lblErrorMessageLabel.setName("errorMessageLabel");
		lblErrorMessageLabel.setOpaque(true);
		contentPane.add(lblErrorMessageLabel);

		itemListShopModel = new DefaultListModel<>();
		itemListCartModel = new DefaultListModel<>();

		itemListShop = new JList<>();
		itemListShop.setBounds(6, 64, 350, 169);
		itemListShop.setModel(itemListShopModel);
		itemListShop.addListSelectionListener(e -> 
		btnAdd.setEnabled(itemListShop.getSelectedIndex() != -1)
				);

		lblCartName = new JLabel("Choose a Cart Name");
		lblCartName.setBounds(399, 31, 129, 16);
		lblCartName.setName("lblCartName");
		contentPane.add(lblCartName);

		cartNameText = new JTextField("");
		cartNameText.setBounds(540, 26, 213, 26);
		cartNameText.setName("cartNameText");
		contentPane.add(cartNameText);
		cartNameText.setColumns(10);

		itemListShop.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListShop.setName("itemListShop");
		contentPane.add(itemListShop);

		itemListCart = new JList<>(itemListCartModel);
		itemListCart.setBounds(399, 64, 351, 169);
		itemListCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		itemListCart.addListSelectionListener(e -> {
			if(!cartNameText.getText().trim().isEmpty() )
				btnBuy.setEnabled(itemListCart.getSelectedIndex() != -1);
			btnRemove.setEnabled(itemListCart.getSelectedIndex() != -1);	

		});	

		itemListCart.setName("itemListCart");
		contentPane.add(itemListCart);

		btnAdd = new JButton("Add Selected Item to Cart");
		btnAdd.setEnabled(false);
		btnAdd.setBounds(0, 245, 356, 29);
		btnAdd.setName("btnAdd");
		contentPane.add(btnAdd);

		btnAdd.addActionListener(
				e -> { 
					cartController.addToCart(itemListShop.getSelectedValue());
					itemListShop.clearSelection();
				});

		btnRemove = new JButton("Remove Selected Item from Cart");
		btnRemove.setEnabled(false);
		btnRemove.setBounds(399, 245, 247, 29);
		btnRemove.setName("btnRemove");
		contentPane.add(btnRemove);

		btnRemove.addActionListener(
				e -> cartController.removeFromCart(itemListCart.getSelectedValue())
				);

		btnSearch.addActionListener(
				e ->shopController.searchItem(itemName.getText())		
				);

		btnBuy = new JButton("Purchase");
		btnBuy.setBounds(644, 245, 115, 29);
		btnBuy.setName("btnBuy");
		btnBuy.setEnabled(false);
		contentPane.add(btnBuy);


		listItemsCartModel =new DefaultListModel<>();		
		listCartModel =  new DefaultListModel<>();

		listCart = new JList<Cart>(listCartModel);
		listCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCart.setBounds(6, 376, 350, 138);
		listCart.setName("listCart");
		contentPane.add(listCart);
		listCart.addListSelectionListener(e -> {
			btnDelete.setEnabled(listCart.getSelectedIndex() != -1)	;
			if(listCart.getSelectedValue() != null) {
				showItemsCart(listCart.getSelectedValue());
			}
		}
				);

		listItemsCart = new JList<Item>(listItemsCartModel);
		listItemsCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listItemsCart.setName("listItemsCart");
		listItemsCart.setBounds(406, 376, 347, 169);
		contentPane.add(listItemsCart);

		btnDelete = new JButton("Delete Selected Cart");
		btnDelete.setEnabled(false);
		btnDelete.setName("btnDelete");
		btnDelete.setBounds(0, 526, 356, 29);
		contentPane.add(btnDelete);
		btnDelete.addActionListener(
				e -> {
					cartController.removeCart(listCart.getSelectedValue());
					showItemsCart(new Cart());
				});

		lblCartsHistory = new JLabel("Carts");
		lblCartsHistory.setName("lblCartsHistory");
		lblCartsHistory.setBounds(6, 348, 61, 16);
		contentPane.add(lblCartsHistory);

		lblItemsCart = new JLabel("Items Cart");
		lblItemsCart.setName("lblItemsCart");
		lblItemsCart.setBounds(406, 348, 108, 16);
		contentPane.add(lblItemsCart);

		lblShop = new JLabel("Items In Shop");
		lblShop.setName("lblShop");
		lblShop.setBounds(6, 6, 125, 16);
		contentPane.add(lblShop);

		lblYourCart = new JLabel("Your Cart");
		lblYourCart.setName("lblYourCart");
		lblYourCart.setBounds(399, 6, 61, 16);
		contentPane.add(lblYourCart);

		separator = new JSeparator();
		separator.setBounds(6, 307, 668, 12);
		contentPane.add(separator);
		
		lblPurchaseHistory = new JLabel("Purchase History");
		lblPurchaseHistory.setBounds(328, 320, 152, 16);
		lblPurchaseHistory.setName("lblPurchaseHistory");
		contentPane.add(lblPurchaseHistory);

		btnBuy.addActionListener(
				e ->{
					cartController.completePurchase(cartNameText.getText());
				});
	}


	@Override
	public void showItemsShop(List<Item> items) {
		items.stream().forEach(itemListShopModel::addElement);
	}

	@Override
	public void errorLog(String error, List<Item> items) {
		String  names = "";
		for (Item item : items){
			names += item.getName() + " ";
		}	
		final String name = names;
		SwingUtilities.invokeLater(
				()->lblErrorMessageLabel.setText(error +": "+ name)
				);
	}

	@Override
	public void errorLogCart(String error, String cart) {
		SwingUtilities.invokeLater(
				()->lblErrorMessageLabel.setText(error + ": " + cart)
				);
	}
	@Override
	public void errorLogItem(String error, String item) {
		SwingUtilities.invokeLater(
				()->lblErrorMessageLabel.setText(error + ": " + item)
				);
	}

	@Override
	public void showSearchResult(Item item) {
		SwingUtilities.invokeLater(
				()-> {
					if(!itemName.getText().trim().isEmpty()) {
						DefaultListModel<Item> itemFiltered = new DefaultListModel<>();
						itemFiltered.addElement(item);
						itemListShop.setModel(itemFiltered);
						resetErrorLabel();		
					}else {
						itemListShop.setModel(itemListShopModel);
						resetErrorLabel();
					}
				});	
	}

	@Override
	public void itemAddedToCart(Item item) {
		SwingUtilities.invokeLater(
				()-> {
					itemListCartModel.addElement(item);
					resetErrorLabel();
				});
	}

	@Override
	public void itemRemovedFromCart(Item item) {
		itemListCartModel.removeElement(item);
		resetErrorLabel();
	}

	private void resetErrorLabel() {
		lblErrorMessageLabel.setText(" ");
	}

	@Override
	public void updateItemsCart(List<Item> items) {
		DefaultListModel<Item> listCartUpdated = new DefaultListModel<>();
		SwingUtilities.invokeLater(
				()-> {
					for(Item itemsCart : items){
						listCartUpdated.addElement(itemsCart);
					}    
					itemListCart.setModel(listCartUpdated);  
				});
	}

	@Override
	public void updateItemsShop(List<Item> items) {
		DefaultListModel<Item> listShopUpdated = new DefaultListModel<>();
		for(Item itemsShop : items){
			listShopUpdated.addElement(itemsShop);
		}    
		itemListShop.setModel(listShopUpdated);  
	}

	@Override
	public void showHistory(List<Cart> carts) {
		DefaultListModel<Cart> listCartUpdated = new DefaultListModel<>();
		for(Cart cartHistory : carts){
			listCartUpdated.addElement(cartHistory);	
		}   
		listCart.setModel(listCartUpdated); 
		setDefaultListModel(listCartUpdated);
	}

	@Override
	public void removeCart(Cart cart) {
		DefaultListModel<Cart> carts = (DefaultListModel<Cart>) listCart.getModel();
		carts.removeElement(cart);
		listCart.setModel(carts);
		setDefaultListModel(carts);
	}

	@Override
	public void showItemsCart(Cart cart) {
		DefaultListModel<Item> listItems= new DefaultListModel<>();
		for(Item itemsShop : cart.getItems()){
			listItems.addElement(itemsShop);
		}    
		listItemsCart.setModel(listItems);
	}
}