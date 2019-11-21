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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

@SuppressWarnings("serial")
public class HistoryViewSwing extends JFrame implements HistoryView {

	private JPanel contentPane;
	private DefaultListModel<Cart> listCartModel;
	private DefaultListModel<Item> listItemsCartModel;
	private JList<Cart> listCart;
	private JList<Item> listItemsCart;
	
	private CartController cartController;

	private JButton btnRemove;

	DefaultListModel<Cart> getListCartModel(){
		return listCartModel;
	}
	
	public void setCartController(CartController cartController) {
		this.cartController = cartController;
	}
	public HistoryViewSwing() {

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

		listItemsCartModel = new DefaultListModel<Item>();
		listCartModel = new DefaultListModel<Cart>();

		listCart = new JList<>();
		listCart.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				btnRemove.setEnabled(listCart.getSelectedIndex() != -1);
			}
		});
		listCart.setModel(listCartModel);

		listCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCart.setName("listCart");	
		GridBagConstraints gbc_itemListShop = new GridBagConstraints();
		gbc_itemListShop.gridwidth = 4;
		gbc_itemListShop.gridheight = 8;
		gbc_itemListShop.insets = new Insets(0, 0, 5, 5);
		gbc_itemListShop.fill = GridBagConstraints.BOTH;
		gbc_itemListShop.gridx = 0;
		gbc_itemListShop.gridy = 2;
		contentPane.add(listCart, gbc_itemListShop);

		listItemsCart = new JList<>(listItemsCartModel);
		listItemsCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listItemsCart.setName("listItemsCart");
		GridBagConstraints gbc_itemListCart = new GridBagConstraints();
		gbc_itemListCart.insets = new Insets(0, 0, 5, 0);
		gbc_itemListCart.gridheight = 8;
		gbc_itemListCart.gridwidth = 5;
		gbc_itemListCart.fill = GridBagConstraints.BOTH;
		gbc_itemListCart.gridx = 5;
		gbc_itemListCart.gridy = 2;
		contentPane.add(listItemsCart, gbc_itemListCart);



		btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemove.gridx = 4;
		gbc_btnRemove.gridy = 5;
		contentPane.add(btnRemove, gbc_btnRemove);

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
