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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ItemsViewSwing extends JFrame implements ItemsView {	

	private JPanel contentPane;
	private JTextField itemName;
	private ShopController shopController;
	private CartController cartController;
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
	/**
	 * Launch the application.	
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ItemsViewSwing frame = new ItemsViewSwing();
//					frame.setVisible(true);
//					Item item = new Item("1","Iphone");
//					frame.itemListShopModel.addElement(item);
//					frame.itemListCartModel.addElement(item);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public ItemsViewSwing() {
		
		

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ShopOnline");
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setName("");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 36, 0};
		gbl_contentPane.rowHeights = new int[] {0, 30, 30, 30, 0, 0, 0, 0, 0, 30, 30, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		itemName = new JTextField();
		itemName.setName("itemName");
		GridBagConstraints gbc_itemName = new GridBagConstraints();
		gbc_itemName.gridwidth = 8;
		gbc_itemName.insets = new Insets(0, 0, 5, 5);
		gbc_itemName.fill = GridBagConstraints.HORIZONTAL;
		gbc_itemName.gridx = 1;
		gbc_itemName.gridy = 0;
		contentPane.add(itemName, gbc_itemName);
		itemName.setColumns(10);

		btnSearch = new JButton("Search");
		btnSearch.setEnabled(true);
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.insets = new Insets(0, 0, 5, 5);
		gbc_btnSearch.gridx = 0;
		gbc_btnSearch.gridy = 0;
		contentPane.add(btnSearch, gbc_btnSearch);

		lblErrorMessageLabel = new JLabel(" ");
		lblErrorMessageLabel.setName("errorMessageLabel");
		lblErrorMessageLabel.setOpaque(true);
		GridBagConstraints gbc_lblErrorMessageLabel = new GridBagConstraints();
		gbc_lblErrorMessageLabel.gridwidth = 8;
		gbc_lblErrorMessageLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblErrorMessageLabel.gridx = 1;
		gbc_lblErrorMessageLabel.gridy = 1;
		contentPane.add(lblErrorMessageLabel, gbc_lblErrorMessageLabel);

		itemListShopModel = new DefaultListModel<Item>();
		itemListCartModel = new DefaultListModel<Item>();

		itemListShop = new JList<>();
		itemListShop.setModel(itemListShopModel);
		itemListShop.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				btnAdd.setEnabled(itemListShop.getSelectedIndex() != -1);

			}
		});
		itemListShop.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListShop.setName("itemListShop");
		GridBagConstraints gbc_itemListShop = new GridBagConstraints();
		gbc_itemListShop.gridwidth = 4;
		gbc_itemListShop.gridheight = 8;
		gbc_itemListShop.insets = new Insets(0, 0, 5, 5);
		gbc_itemListShop.fill = GridBagConstraints.BOTH;
		gbc_itemListShop.gridx = 0;
		gbc_itemListShop.gridy = 2;
		contentPane.add(itemListShop, gbc_itemListShop);

		itemListCart = new JList<>(itemListCartModel);
		itemListCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemListCart.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				btnRemove.setEnabled(itemListCart.getSelectedIndex() != -1);
				btnBuy.setEnabled(itemListCart.getSelectedIndex() != -1);
			}
		});
		itemListCart.setName("itemListCart");
		GridBagConstraints gbc_itemListCart = new GridBagConstraints();
		gbc_itemListCart.insets = new Insets(0, 0, 5, 0);
		gbc_itemListCart.gridheight = 8;
		gbc_itemListCart.gridwidth = 5;
		gbc_itemListCart.fill = GridBagConstraints.BOTH;
		gbc_itemListCart.gridx = 5;
		gbc_itemListCart.gridy = 2;
		contentPane.add(itemListCart, gbc_itemListCart);

		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		btnAdd.setName("Add");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd.gridx = 4;
		gbc_btnAdd.gridy = 4;
		contentPane.add(btnAdd, gbc_btnAdd);

		btnAdd.addActionListener(
				e -> new Thread(
						()-> cartController.addToCart(itemListShop.getSelectedValue())).start()
			
		);

		btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemove.gridx = 4;
		gbc_btnRemove.gridy = 5;
		contentPane.add(btnRemove, gbc_btnRemove);

		btnRemove.addActionListener(
				e -> cartController.removeFromCart(itemListCart.getSelectedValue())
				);

		btnSearch.addActionListener(
				e -> shopController.searchItem(itemName.getText())
				);
		
		btnHistory = new JButton("History");
		btnHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HistoryViewSwing historyframe = new HistoryViewSwing();
				historyframe.setVisible(true);
			}
		});
		GridBagConstraints gbc_btnHistory = new GridBagConstraints();
		gbc_btnHistory.insets = new Insets(0, 0, 5, 5);
		gbc_btnHistory.gridx = 4;
		gbc_btnHistory.gridy = 8;
		contentPane.add(btnHistory, gbc_btnHistory);
		


		btnBuy = new JButton("Buy");
		btnBuy.setEnabled(false);
		GridBagConstraints gbc_btnBuy = new GridBagConstraints();
		gbc_btnBuy.insets = new Insets(0, 0, 0, 5);
		gbc_btnBuy.gridx = 4;
		gbc_btnBuy.gridy = 10;
		contentPane.add(btnBuy, gbc_btnBuy);
		
		btnBuy.addActionListener(
				e -> cartController.completePurchase()
				);

	}

	@Override
	public void showItemsShop(List<Item> items) {
		items.stream().forEach(itemListShopModel::addElement);

	}
	@Override
	public void showItemsCart(List<Item> items) {
		items.stream().forEach(itemListCartModel::addElement);
	}


	@Override
	public void errorLog(String error, Item item) {
		SwingUtilities.invokeLater(
				()->lblErrorMessageLabel.setText(error + ": " + item));
		
	}

	@Override
	public void showSearchResult(Item item) {
		SwingUtilities.invokeLater(
				()-> {
					if(!itemName.getText().trim().isEmpty()) {
						DefaultListModel<Item> itemFiltered = new DefaultListModel<Item>();
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
	
		DefaultListModel<Item> model = new DefaultListModel<Item>();
	    for(Item p : items){
	         model.addElement(p);
	    }    
	    itemListCart.setModel(model);  
	   
	}


}
