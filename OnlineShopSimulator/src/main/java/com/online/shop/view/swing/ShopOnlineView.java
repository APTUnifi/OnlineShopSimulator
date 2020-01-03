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

@SuppressWarnings("serial")
public class ShopOnlineView extends JFrame  implements ShopView{

	private transient ShopController shopController;
	private transient CartController cartController;
	private JPanel contentPane;

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
		setBounds(100, 100, 676, 594);
		contentPane = new JPanel();
		contentPane.setName("MainPanel");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		itemName = new JTextField();
		itemName.setBounds(96, 6, 409, 26);
		itemName.setName("itemName");
		contentPane.add(itemName);
		itemName.setColumns(10);

		btnSearch = new JButton("Search");
		btnSearch.setBounds(6, 5, 85, 29);
		btnSearch.setEnabled(true);
		contentPane.add(btnSearch);

		lblErrorMessageLabel = new JLabel(" ");
		lblErrorMessageLabel.setBounds(106, 44, 441, 16);
		lblErrorMessageLabel.setName("errorMessageLabel");
		lblErrorMessageLabel.setOpaque(true);
		contentPane.add(lblErrorMessageLabel);

		itemListShopModel = new DefaultListModel<>();
		itemListCartModel = new DefaultListModel<>();

		itemListShop = new JList<>();
		itemListShop.setBounds(6, 91, 281, 169);
		itemListShop.setModel(itemListShopModel);
		itemListShop.addListSelectionListener(e -> 
		btnAdd.setEnabled(itemListShop.getSelectedIndex() != -1)
				);

		lblCartName = new JLabel("Cart Name : ");
		lblCartName.setBounds(316, 274, 78, 16);
		lblCartName.setName("lblCartName");
		contentPane.add(lblCartName);

		cartNameText = new JTextField("");
		cartNameText.setBounds(399, 269, 260, 26);
		cartNameText.setName("cartNameText");
		contentPane.add(cartNameText);
		cartNameText.setColumns(10);

		itemListShop.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListShop.setName("itemListShop");
		contentPane.add(itemListShop);

		itemListCart = new JList<>(itemListCartModel);
		itemListCart.setBounds(399, 91, 260, 169);
		itemListCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		itemListCart.addListSelectionListener(e -> {
			if(!cartNameText.getText().trim().isEmpty() )
				btnBuy.setEnabled(itemListCart.getSelectedIndex() != -1);
			btnRemove.setEnabled(itemListCart.getSelectedIndex() != -1);	

		});	

		itemListCart.setName("itemListCart");
		contentPane.add(itemListCart);

		btnAdd = new JButton("Add");
		btnAdd.setBounds(299, 121, 75, 29);
		btnAdd.setEnabled(false);
		btnAdd.setName("Add");
		contentPane.add(btnAdd);

		btnAdd.addActionListener(
				e -> { 
					cartController.addToCart(itemListShop.getSelectedValue());
					itemListShop.clearSelection();
				});

		btnRemove = new JButton("Remove");
		btnRemove.setBounds(294, 162, 93, 29);
		btnRemove.setName("Remove");
		btnRemove.setEnabled(false);
		contentPane.add(btnRemove);

		btnRemove.addActionListener(
				e -> cartController.removeFromCart(itemListCart.getSelectedValue())
				);

		btnSearch.addActionListener(
				e ->{ new Thread(
						()->
						shopController.searchItem(itemName.getText())
						).start();
				});

		btnBuy = new JButton("Buy");
		btnBuy.setBounds(299, 203, 75, 29);
		btnBuy.setName("Buy");
		btnBuy.setEnabled(false);
		contentPane.add(btnBuy);


		listItemsCartModel =new DefaultListModel<>();		
		listCartModel =  new DefaultListModel<>();

		listCart = new JList<Cart>(listCartModel);
		listCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCart.setBounds(4, 359, 283, 186);
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
		listItemsCart.setBounds(406, 359, 253, 186);
		contentPane.add(listItemsCart);

		btnDelete = new JButton("Delete");
		btnDelete.setName("Delete");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(296, 449, 98, 29);
		contentPane.add(btnDelete);
		btnDelete.addActionListener(
				e -> {
					cartController.removeCart(listCart.getSelectedValue());
					//setDefaultListModel(updateListCarts());
					showItemsCart(new Cart());
				});

		lblCartsHistory = new JLabel("Carts");
		lblCartsHistory.setName("lblCartsHistory");
		lblCartsHistory.setBounds(4, 334, 61, 16);
		contentPane.add(lblCartsHistory);

		lblItemsCart = new JLabel("Items Cart");
		lblItemsCart.setName("lblItemsCart");
		lblItemsCart.setBounds(403, 334, 108, 16);
		contentPane.add(lblItemsCart);

		lblShop = new JLabel("Items Shop");
		lblShop.setName("lblShop");
		lblShop.setBounds(6, 73, 85, 16);
		contentPane.add(lblShop);

		lblYourCart = new JLabel("Your Cart");
		lblYourCart.setName("lblYourCart");
		lblYourCart.setBounds(399, 73, 61, 16);
		contentPane.add(lblYourCart);

		separator = new JSeparator();
		separator.setBounds(6, 307, 668, 12);
		contentPane.add(separator);

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
	public void errorLogCart(String error, Cart cart) {
		SwingUtilities.invokeLater(
				()->lblErrorMessageLabel.setText(error +": " + cart.getLabel() )
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
	public void itemAdded(Item item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void itemQuantityAdded(Item item) {
		// TODO Auto-generated method stub
	}

	@Override
	public void itemRemoved(Item item) {
		// TODO Auto-generated method stub
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