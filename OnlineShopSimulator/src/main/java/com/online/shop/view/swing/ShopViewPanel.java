package com.online.shop.view.swing;

import java.util.List;

import javax.swing.JPanel;

import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Item;
import com.online.shop.view.ShopView;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ShopViewPanel extends JPanel implements ShopView {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<Item> itemListShopModel;
	private DefaultListModel<Item> itemListCartModel;

	private JList<Item> itemListShop;
	private JList<Item> itemListCart;
	
	private JButton btnBuy;
	private JButton btnRemove;
	private JButton btnAdd;
	private JButton btnSearch;
	
	private JLabel lblCartName;
	private JLabel lblYourCart;
	private JLabel lblShop;
	private JLabel lblErrorMessageLabel;
	
	private ShopController shopController;
	private CartController cartController;
	private JTextField itemName;
	private JTextField cartNameText;
	/**
	 * Create the panel.
	 */
	public JPanel getPanel() {
		return this;
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
	
	public ShopViewPanel() {
		setLayout(null);
		itemListShopModel = new DefaultListModel<>();
		itemListCartModel = new DefaultListModel<>();
		
		btnBuy = new JButton("Purchase");
		btnBuy.setName("btnBuy");
		btnBuy.setEnabled(false);
		btnBuy.setBounds(650, 250, 115, 29);
		add(btnBuy);
		
		btnRemove = new JButton("Remove Selected Item from Cart");
		btnRemove.setName("btnRemove");
		btnRemove.setEnabled(false);
		btnRemove.setBounds(405, 250, 247, 29);
		add(btnRemove);
		
		itemListCart = new JList<Item>(itemListCartModel);
		itemListCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListCart.setName("itemListCart");
		itemListCart.setBounds(406, 69, 350, 169);
		add(itemListCart);
		
		itemListCart.addListSelectionListener(e -> {
			if(!cartNameText.getText().trim().isEmpty() )
				btnBuy.setEnabled(itemListCart.getSelectedIndex() != -1);
			btnRemove.setEnabled(itemListCart.getSelectedIndex() != -1);	

		});	
		
		lblCartName = new JLabel("Choose a Cart Name");
		lblCartName.setName("lblCartName");
		lblCartName.setBounds(405, 36, 129, 16);
		add(lblCartName);
		
		lblYourCart = new JLabel("Your Cart");
		lblYourCart.setName("lblYourCart");
		lblYourCart.setBounds(405, 11, 61, 16);
		add(lblYourCart);
		
		itemListShop = new JList<Item>(itemListShopModel);
		itemListShop.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListShop.setName("itemListShop");
		itemListShop.setBounds(12, 69, 350, 169);
		add(itemListShop);
		itemListShop.addListSelectionListener(e -> 
		btnAdd.setEnabled(itemListShop.getSelectedIndex() != -1)
				);
		
		btnAdd = new JButton("Add Selected Item to Cart");
		btnAdd.setName("btnAdd");
		btnAdd.setEnabled(false);
		btnAdd.setBounds(12, 250, 350, 29);
		add(btnAdd);
		
		btnAdd.addActionListener(
				e -> { 
					cartController.addToCart(itemListShop.getSelectedValue());
					itemListShop.clearSelection();
				});
		
		
		btnSearch = new JButton("Search");
		btnSearch.setEnabled(true);
		btnSearch.setBounds(6, 31, 131, 29);
		add(btnSearch);
		
		lblShop = new JLabel("Items In Shop");
		lblShop.setName("lblShop");
		lblShop.setBounds(12, 11, 125, 16);
		add(lblShop);
		btnBuy.addActionListener(
				e ->{
					cartController.completePurchase(cartNameText.getText());
				});
		
		btnRemove.addActionListener(
				e -> cartController.removeFromCart(itemListCart.getSelectedValue())
				);

		btnSearch.addActionListener(
				e ->{
					if(!itemName.getText().trim().isEmpty())
						shopController.searchItem(itemName.getText())	;
					resetErrorLabel();
				}
				);

		
		lblErrorMessageLabel = new JLabel(" ");
		lblErrorMessageLabel.setOpaque(true);
		lblErrorMessageLabel.setName("errorMessageLabel");
		lblErrorMessageLabel.setBounds(168, 284, 441, 16);
		add(lblErrorMessageLabel);
		
		itemName = new JTextField();
		itemName.setBounds(142, 36, 220, 18);
		itemName.setName("itemName");
		add(itemName);
		itemName.setColumns(10);
		
		cartNameText = new JTextField();
		cartNameText.setBounds(536, 35, 220, 18);
		cartNameText.setName("cartNameText");
		add(cartNameText);
		cartNameText.setColumns(10);
		

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
		resetErrorLabel();
	}
	@Override
	public void errorLogCart(String error, String cart) {
		SwingUtilities.invokeLater(
				()->lblErrorMessageLabel.setText(error + ": " + cart)
				);
	}
}
