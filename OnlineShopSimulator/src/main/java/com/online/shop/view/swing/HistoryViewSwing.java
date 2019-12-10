package com.online.shop.view.swing;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.online.shop.controller.CartController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.view.HistoryView;
import javax.swing.JList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class HistoryViewSwing extends JFrame implements HistoryView {

	private JPanel contentPanel;
	private DefaultListModel<Cart> listCartModel;
	private DefaultListModel<Item> listItemsCartModel;
	private JList<Cart> listCart;
	private JList<Item> listItemsCart;

	private transient CartController cartController;

	private JButton btnRemove;

	DefaultListModel<Cart> getListCartModel(){
		return listCartModel;
	}

	public void setCartController(CartController cartController) {
		this.cartController = cartController;
	}
	public HistoryViewSwing() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("History");
		setBounds(100, 100, 450, 300);
		contentPanel = new JPanel();
		contentPanel.setName("history");
		contentPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
		setContentPane(contentPanel);
		GridBagLayout gblcontentPanel = new GridBagLayout();
		gblcontentPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 36, 0};
		gblcontentPanel.rowHeights = new int[] {0, 30, 30, 30, 0, 0, 0, 0, 0, 30, 30, 0};
		gblcontentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gblcontentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gblcontentPanel);

		listItemsCartModel = new DefaultListModel<>();
		listCartModel = new DefaultListModel<>();

		listCart = new JList<>();
		listCart.addListSelectionListener(e -> {
			btnRemove.setEnabled(listCart.getSelectedIndex() != -1)	;
			if(listCart.getSelectedValue() != null) {
				showItemsCart(listCart.getSelectedValue());
			}
		}
				);

		listCart.setModel(listCartModel);
		listCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCart.setName("listCart");	
		GridBagConstraints gbcitemListShop = new GridBagConstraints();
		gbcitemListShop.gridwidth = 4;
		gbcitemListShop.gridheight = 8;
		gbcitemListShop.insets = new Insets(0, 0, 5, 5);
		gbcitemListShop.fill = GridBagConstraints.BOTH;
		gbcitemListShop.gridx = 0;
		gbcitemListShop.gridy = 2;
		contentPanel.add(listCart, gbcitemListShop);

		listItemsCart = new JList<>(listItemsCartModel);
		listItemsCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listItemsCart.setName("listItemsCart");
		GridBagConstraints gbcitemListCart = new GridBagConstraints();
		gbcitemListCart.insets = new Insets(0, 0, 5, 0);
		gbcitemListCart.gridheight = 8;
		gbcitemListCart.gridwidth = 5;
		gbcitemListCart.fill = GridBagConstraints.BOTH;
		gbcitemListCart.gridx = 5;
		gbcitemListCart.gridy = 2;
		contentPanel.add(listItemsCart, gbcitemListCart);

		btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);
		GridBagConstraints gbcbtnRemove = new GridBagConstraints();
		gbcbtnRemove.insets = new Insets(0, 0, 5, 5);
		gbcbtnRemove.gridx = 4;
		gbcbtnRemove.gridy = 5;
		contentPanel.add(btnRemove, gbcbtnRemove);

		btnRemove.addActionListener(
				e -> cartController.removeCart(listCart.getSelectedValue())
				);
	}

	@Override
	public void showHistory(List<Cart> carts) {
		carts.stream().forEach(listCartModel::addElement);
	}

	@Override
	public void removeCart(Cart cart) {
		SwingUtilities.invokeLater(
				()-> listCartModel.removeElement(cart)			
				);	
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
