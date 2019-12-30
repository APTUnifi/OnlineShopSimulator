package com.online.shop.view.swing;


import java.awt.Window;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import com.online.shop.controller.CartController;
import com.online.shop.model.Cart;
import com.online.shop.model.Item;
import com.online.shop.view.HistoryView;

@SuppressWarnings("serial")
public class HistoryDialogSwing extends JDialog implements HistoryView {

	private DefaultListModel<Cart> listCartModel;
	private DefaultListModel<Item> listItemsCartModel;
	private JList<Item> listItemsCart;
	private JList<Cart> listCart;
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

	public HistoryDialogSwing() {
		setName("History");
		setTitle("History");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		 

		setModalityType(DEFAULT_MODALITY_TYPE);

		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 450, 278);
		contentPanel.setSize(500, 300);
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		
		listItemsCartModel =new DefaultListModel<>();		
		listCartModel =  new DefaultListModel<>();
		listCart = new JList<Cart>(listCartModel);
		listCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listCart.setBounds(16, 34, 190, 186);
		listCart.setName("listCart");
		contentPanel.add(listCart);
		listCart.addListSelectionListener(e -> {
			btnRemove.setEnabled(listCart.getSelectedIndex() != -1)	;
			if(listCart.getSelectedValue() != null) {
				showItemsCart(listCart.getSelectedValue());
			}
		}
				);
		listItemsCart = new JList<Item>(listItemsCartModel);
		listItemsCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listItemsCart.setName("listItemsCart");
		listItemsCart.setBounds(242, 34, 190, 186);
		contentPanel.add(listItemsCart);
		
		lblCarts = new JLabel("Carts");
		lblCarts.setName("lblCarts");
		lblCarts.setBounds(16, 6, 61, 16);
		contentPanel.add(lblCarts);
		
		lblItemsCart = new JLabel("Items Cart");
		lblItemsCart.setName("lblItemsCart");
		lblItemsCart.setBounds(242, 6, 108, 16);
		contentPanel.add(lblItemsCart);
		
		btnShowHistory = new JButton("ShowHistory");
		btnShowHistory.setName("showHistory");
		btnShowHistory.setBounds(57, 232, 117, 29);
		contentPanel.add(btnShowHistory);
		btnShowHistory.addActionListener(
				e ->{ setDefaultListModel(updateListCarts());
				cartController.allCarts();
				}
				);		
	
		btnRemove = new JButton("Remove");
		btnRemove.setName("Remove");
		btnRemove.setEnabled(false);
		btnRemove.setBounds(186, 232, 117, 29);
		contentPanel.add(btnRemove);
		btnRemove.addActionListener(
				e -> {
					cartController.removeCart(listCart.getSelectedValue());
					setDefaultListModel(updateListCarts());
					showItemsCart(new Cart());
				});
		
		btnClose = new JButton("Close");
		btnClose.setName("Close");
		btnClose.setBounds(315, 232, 117, 29);
		contentPanel.add(btnClose);
		

		btnClose.addActionListener(
				e -> closeButtonAction()
				);


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
		for(Item itemsShop : cart.getItems()){
			listItems.addElement(itemsShop);
		}    
		listItemsCart.setModel(listItems);
	}

	
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