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
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

@SuppressWarnings("serial")
public class HistoryViewSwing extends JFrame implements HistoryView {

	private JPanel contentPane;
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
		contentPane = new JPanel();
		contentPane.setName("history");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gblcontentPane = new GridBagLayout();
		gblcontentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 36, 0};
		gblcontentPane.rowHeights = new int[] {0, 30, 30, 30, 0, 0, 0, 0, 0, 30, 30, 0};
		gblcontentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gblcontentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gblcontentPane);

		listItemsCartModel = new DefaultListModel<>();
		listCartModel = new DefaultListModel<>();
		
		ListSelectionListener btnAddEnabler = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				btnRemove.setEnabled(listCart.getSelectedIndex() != -1);

				}
		};

		listCart = new JList<>();
		listCart.addListSelectionListener(btnAddEnabler);
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
		contentPane.add(listCart, gbcitemListShop);

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
		contentPane.add(listItemsCart, gbcitemListCart);



		btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);
		GridBagConstraints gbcbtnRemove = new GridBagConstraints();
		gbcbtnRemove.insets = new Insets(0, 0, 5, 5);
		gbcbtnRemove.gridx = 4;
		gbcbtnRemove.gridy = 5;
		contentPane.add(btnRemove, gbcbtnRemove);

		btnRemove.addActionListener(
				e -> cartController.removeFromHistory(listCart.getSelectedValue())
		);
	}
		
	@Override
	public void showHistory(List<Cart> carts) {
		carts.stream().forEach(listCartModel::addElement);
	}

	@Override
	public void removeCart(Cart cart) {
		listCartModel.removeElement(cart);		
	}
	@Override
	public void showItemsCart(Cart cart) {
		cart.getItems().stream().forEach(listItemsCartModel::addElement);
	}
}
