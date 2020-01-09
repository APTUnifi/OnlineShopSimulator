package com.online.shop.view.swing;

import java.util.List;

import javax.swing.JPanel;

import com.online.shop.controller.CartController;
import com.online.shop.model.Cart;
import com.online.shop.view.HistoryView;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import com.online.shop.model.Item;

public class HistoryViewPanel extends JPanel implements HistoryView {

	private DefaultListModel<Cart> listCartModel;
	private DefaultListModel<Item> listItemsCartModel;

	private JLabel lblPurchaseHistory;
	private JLabel lblItemsCart;
	private JLabel lblCartsHistory;
	private JLabel lblErrorMessageLabel;
	private JList<Item> listItemsCart;
	private JList<Cart> listCart;
	private JButton btnDelete;

	private transient CartController cartController;

	private static final long serialVersionUID = 1L;

	public JPanel getPanel() {
		return this;
	}

	DefaultListModel<Cart> getListCartModel() {
		return listCartModel;
	}

	public void setCartController(CartController cartController) {
		this.cartController = cartController;
	}

	public void setDefaultListModel(DefaultListModel<Cart> a) {
		listCart.setModel(a);
	}

	public HistoryViewPanel() {
		setSize(775, 320);
		setLayout(null);
		listItemsCartModel = new DefaultListModel<>();
		listCartModel = new DefaultListModel<>();

		listCart = new JList<>(listCartModel);
		listCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCart.setName("listCart");
		listCart.setBounds(18, 62, 350, 169);
		add(listCart);

		listCart.addListSelectionListener(e -> {
			btnDelete.setEnabled(listCart.getSelectedIndex() != -1);
			if (listCart.getSelectedValue() != null) {
				showItemsCart(listCart.getSelectedValue());
			}
		});

		lblCartsHistory = new JLabel("Carts");
		lblCartsHistory.setName("lblCartsHistory");
		lblCartsHistory.setBounds(18, 34, 61, 16);
		add(lblCartsHistory);

		btnDelete = new JButton("Delete Selected Cart");
		btnDelete.setName("btnDelete");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(18, 243, 350, 29);
		add(btnDelete);
		btnDelete.addActionListener(e -> {
			cartController.removeCart(listCart.getSelectedValue());
			showItemsCart(new Cart());
		});

		listItemsCart = new JList<>(listItemsCartModel);
		listItemsCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listItemsCart.setName("listItemsCart");
		listItemsCart.setBounds(407, 62, 347, 210);
		add(listItemsCart);

		lblItemsCart = new JLabel("Items Cart");
		lblItemsCart.setName("lblItemsCart");
		lblItemsCart.setBounds(407, 34, 108, 16);
		add(lblItemsCart);

		lblPurchaseHistory = new JLabel("Purchase History");
		lblPurchaseHistory.setName("lblPurchaseHistory");
		lblPurchaseHistory.setBounds(334, 6, 152, 16);
		add(lblPurchaseHistory);

		lblErrorMessageLabel = new JLabel(" ");
		lblErrorMessageLabel.setOpaque(true);
		lblErrorMessageLabel.setName("errorMessageLabel");
		lblErrorMessageLabel.setBounds(168, 284, 441, 16);
		add(lblErrorMessageLabel);
	}

	@Override
	public void showHistory(List<Cart> carts) {
		DefaultListModel<Cart> listCartUpdated = new DefaultListModel<>();
		for (Cart cartHistory : carts) {
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
		resetErrorLabel();
	}

	@Override
	public void showItemsCart(Cart cart) {
		DefaultListModel<Item> listItems = new DefaultListModel<>();
		for (Item itemsShop : cart.getItems()) {
			listItems.addElement(itemsShop);
		}
		listItemsCart.setModel(listItems);
	}

	@Override
	public void errorLogCart(String error, String cart) {
		SwingUtilities.invokeLater(() -> lblErrorMessageLabel.setText(error + ": " + cart));
	}

	private void resetErrorLabel() {
		lblErrorMessageLabel.setText(" ");
	}
	
}
