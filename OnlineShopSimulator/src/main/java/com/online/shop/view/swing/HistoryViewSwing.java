package com.online.shop.view.swing;

import java.awt.BorderLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.online.shop.controller.CartController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.view.HistoryView;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class HistoryViewSwing extends JPanel implements HistoryView{

	private final DefaultListModel<Cart> listCartModel;
	private DefaultListModel<Item> listItemsCartModel;
	public JList<Cart> listCart;
	public JList<Item> listItemsCart;
	private JButton btnRemove;
	private JButton btnClose;
	private JButton btnShowHistory;
	private JLabel lblItemsCart;
	private JLabel lblCarts;

	private final JPanel contentPanel;
	private transient CartController cartController;

	DefaultListModel<Cart> getListCartModel(){
		return listCartModel;
	}

	public void setCartController(CartController cartController) {
		this.cartController = cartController;
	}

	public void setDefaultListModel(DefaultListModel<Cart> a) {
		listCart.setModel(a);
	}

	public HistoryViewSwing() {
		contentPanel = new JPanel();

		setBounds(100, 100, 450, 300);
		setName("History");
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.setName("HistoryPanel");

		btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);
		btnRemove.setName("Remove");

		btnRemove.addActionListener(
				e -> {
					cartController.removeCart(listCart.getSelectedValue());
					showItemsCart(null);
					setDefaultListModel(updateListCarts());
					showItemsCart(null);
				});
		btnRemove.setBounds(186, 254, 117, 29);
		contentPanel.add(btnRemove);

		btnClose = new JButton("Close");
		btnClose.setName("Close");
		btnClose.addActionListener(
				e -> closeButtonAction()
				);
		btnClose.setBounds(315, 254, 117, 29);
		contentPanel.add(btnClose);

		listItemsCartModel =new DefaultListModel<>();		
		listCartModel =  new DefaultListModel<>();
		
		listCart = new JList<>(listCartModel);
		listCart.setModel(listCartModel);
		listCart.setName("listCart");
		listCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCart.setBounds(18, 50, 190, 186);
		contentPanel.add(listCart);

		listCart.addListSelectionListener(e -> {
			btnRemove.setEnabled(listCart.getSelectedIndex() != -1)	;
			if(listCart.getSelectedValue() != null) {
				showItemsCart(listCart.getSelectedValue());
			}
		}
				);

		listItemsCart = new JList<Item>(listItemsCartModel);
		listItemsCart.setName("listItemsCart");
		listItemsCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listItemsCart.setBounds(242, 50, 190, 186);
		contentPanel.add(listItemsCart);

		lblCarts = new JLabel("Carts");
		lblCarts.setName("lblCarts");
		lblCarts.setBounds(18, 22, 61, 16);
		contentPanel.add(lblCarts);

		lblItemsCart = new JLabel("Items Cart");
		lblItemsCart.setName("lblItemsCart");
		lblItemsCart.setBounds(242, 22, 108, 16);
		contentPanel.add(lblItemsCart);

		btnShowHistory = new JButton("ShowHistory");
		btnShowHistory.setName("showHistory");
		btnShowHistory.addActionListener(
				e -> setDefaultListModel(updateListCarts())
				);
		
		btnShowHistory.setBounds(57, 254, 117, 29);
		contentPanel.add(btnShowHistory);
	}

	private void closeButtonAction() {
		Window win = SwingUtilities.getWindowAncestor(this);
		if (win != null) {
			win.dispose();
		}
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
		if (cart == null) {
			listItems.removeAllElements();
			listItemsCart.setModel(listItems);
			return;
		}
		for(Item itemsShop : cart.getItems()){
			listItems.addElement(itemsShop);
		}    
		listItemsCart.setModel(listItems);
	}

	@Override
	public DefaultListModel<Cart> updateListCarts() {
		DefaultListModel<Cart> listCartUpdated = new DefaultListModel<>();
		List<Cart> carts = cartController.getListCart();
		if (cartController.getListCart() == null) {
			listCart.setModel(listCartUpdated);
			return listCartUpdated;
		}
		for(Cart cartHistory : carts){
			listCartUpdated.addElement(cartHistory);	
		}   
		listCart.setModel(listCartUpdated); 
		return listCartUpdated;
	}
}
