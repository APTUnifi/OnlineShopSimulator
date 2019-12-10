  
package com.online.shop.view.swing;


import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.online.shop.controller.CartController;
import com.online.shop.controller.ShopController;
import com.online.shop.model.Item;
import com.online.shop.view.ItemsView;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


@SuppressWarnings("serial")
public class ItemsViewSwing extends JFrame implements ItemsView {	

	private JPanel contentPane;
	private JTextField itemName;
	private transient ShopController shopController;
	private transient CartController cartController;
	private JButton btnRemove;
	private JButton btnAdd;
	private JButton btnSearch;
	private JList<Item> itemListShop;
	private JList<Item> itemListCart;

	private DefaultListModel<Item> itemListShopModel;
	private DefaultListModel<Item> itemListCartModel;
	private JLabel lblErrorMessageLabel;
	private JButton btnBuy;
	private JButton btnHistory;
	private JLabel lblCartName;
	private JTextField cartNameText;


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

	public ItemsViewSwing() {



		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("ShopOnline");
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setName("");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gblcontentPane = new GridBagLayout();
		gblcontentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 36, 0};
		gblcontentPane.rowHeights = new int[] {0, 30, 30, 30, 0, 0, 0, 0, 0, 30, 30, 0};
		gblcontentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gblcontentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gblcontentPane);

		itemName = new JTextField();
		itemName.setName("itemName");
		GridBagConstraints gbcitemName = new GridBagConstraints();
		gbcitemName.gridwidth = 8;
		gbcitemName.insets = new Insets(0, 0, 5, 5);
		gbcitemName.fill = GridBagConstraints.HORIZONTAL;
		gbcitemName.gridx = 1;
		gbcitemName.gridy = 0;
		contentPane.add(itemName, gbcitemName);
		itemName.setColumns(10);

		btnSearch = new JButton("Search");
		btnSearch.setEnabled(true);
		GridBagConstraints gbcbtnSearch = new GridBagConstraints();
		gbcbtnSearch.insets = new Insets(0, 0, 5, 5);
		gbcbtnSearch.gridx = 0;
		gbcbtnSearch.gridy = 0;
		contentPane.add(btnSearch, gbcbtnSearch);

		lblErrorMessageLabel = new JLabel(" ");
		lblErrorMessageLabel.setName("errorMessageLabel");
		lblErrorMessageLabel.setOpaque(true);
		GridBagConstraints gbclblErrorMessageLabel = new GridBagConstraints();
		gbclblErrorMessageLabel.gridwidth = 8;
		gbclblErrorMessageLabel.insets = new Insets(0, 0, 5, 5);
		gbclblErrorMessageLabel.gridx = 1;
		gbclblErrorMessageLabel.gridy = 1;
		contentPane.add(lblErrorMessageLabel, gbclblErrorMessageLabel);

		itemListShopModel = new DefaultListModel<>();
		itemListCartModel = new DefaultListModel<>();

		itemListShop = new JList<>();
		itemListShop.setModel(itemListShopModel);
		itemListShop.addListSelectionListener(e -> 
			btnAdd.setEnabled(itemListShop.getSelectedIndex() != -1)
				);

		lblCartName = new JLabel("Cart Name : ");
		lblCartName.setName("lblCartName");
		GridBagConstraints gbc_lblCartName = new GridBagConstraints();
		gbc_lblCartName.insets = new Insets(0, 0, 5, 5);
		gbc_lblCartName.gridx = 4;
		gbc_lblCartName.gridy = 2;
		contentPane.add(lblCartName, gbc_lblCartName);

		cartNameText = new JTextField("");
		cartNameText.setName("cartNameText");
		GridBagConstraints gbc_cartNameText = new GridBagConstraints();
		gbc_cartNameText.gridwidth = 5;
		gbc_cartNameText.insets = new Insets(0, 0, 5, 5);
		gbc_cartNameText.fill = GridBagConstraints.HORIZONTAL;
		gbc_cartNameText.gridx = 5;
		gbc_cartNameText.gridy = 2;
		contentPane.add(cartNameText, gbc_cartNameText);
		cartNameText.setColumns(10);

		itemListShop.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListShop.setName("itemListShop");
		GridBagConstraints gbcitemListShop = new GridBagConstraints();
		gbcitemListShop.gridwidth = 4;
		gbcitemListShop.gridheight = 7;
		gbcitemListShop.insets = new Insets(0, 0, 5, 5);
		gbcitemListShop.fill = GridBagConstraints.BOTH;
		gbcitemListShop.gridx = 0;
		gbcitemListShop.gridy = 3;
		contentPane.add(itemListShop, gbcitemListShop);

		itemListCart = new JList<>(itemListCartModel);
		itemListCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		itemListCart.addListSelectionListener(e -> {
			if(!cartNameText.getText().trim().isEmpty())
				btnBuy.setEnabled(itemListCart.getSelectedIndex() != -1);
			btnRemove.setEnabled(itemListCart.getSelectedIndex() != -1);	
			
		});	
		

		itemListCart.setName("itemListCart");
		GridBagConstraints gbcitemListCart = new GridBagConstraints();
		gbcitemListCart.insets = new Insets(0, 0, 5, 0);
		gbcitemListCart.gridheight = 7;
		gbcitemListCart.gridwidth = 5;
		gbcitemListCart.fill = GridBagConstraints.BOTH;
		gbcitemListCart.gridx = 5;
		gbcitemListCart.gridy = 3;
		contentPane.add(itemListCart, gbcitemListCart);

		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		btnAdd.setName("Add");
		GridBagConstraints gbcbtnAdd = new GridBagConstraints();
		gbcbtnAdd.insets = new Insets(0, 0, 5, 5);
		gbcbtnAdd.gridx = 4;
		gbcbtnAdd.gridy = 4;
		contentPane.add(btnAdd, gbcbtnAdd);

		btnAdd.addActionListener(
				e -> new Thread(
						()-> cartController.addToCart(itemListShop.getSelectedValue())).start()

				);

		btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);
		GridBagConstraints gbcbtnRemove = new GridBagConstraints();
		gbcbtnRemove.insets = new Insets(0, 0, 5, 5);
		gbcbtnRemove.gridx = 4;
		gbcbtnRemove.gridy = 5;
		contentPane.add(btnRemove, gbcbtnRemove);

		btnRemove.addActionListener(
				e -> cartController.removeFromCart(itemListCart.getSelectedValue())
				);

		btnSearch.addActionListener(
				e -> shopController.searchItem(itemName.getText())
				);

		btnHistory = new JButton("History");
		btnHistory.addActionListener(e -> {
			HistoryViewSwing historyframe = new HistoryViewSwing();
			historyframe.setVisible(true);
		}
				);

		GridBagConstraints gbcbtnHistory = new GridBagConstraints();
		gbcbtnHistory.insets = new Insets(0, 0, 5, 5);
		gbcbtnHistory.gridx = 4;
		gbcbtnHistory.gridy = 8;
		contentPane.add(btnHistory, gbcbtnHistory);

		btnBuy = new JButton("Buy");
		btnBuy.setEnabled(false);
		GridBagConstraints gbcbtnBuy = new GridBagConstraints();
		gbcbtnBuy.insets = new Insets(0, 0, 0, 5);
		gbcbtnBuy.gridx = 4;
		gbcbtnBuy.gridy = 10;
		contentPane.add(btnBuy, gbcbtnBuy);

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
	
}