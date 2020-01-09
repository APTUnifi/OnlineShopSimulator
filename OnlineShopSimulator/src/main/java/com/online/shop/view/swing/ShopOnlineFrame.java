package com.online.shop.view.swing;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class ShopOnlineFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	public ShopOnlineFrame(ShopViewPanel shopPanel, HistoryViewPanel historyPanel) {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 400);
		setTitle("ShopOnline");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setName("tabbedPanel");
		contentPane.add(tabbedPane);
		
		shopPanel.getPanel().setName("shopPanel");
		tabbedPane.addTab("Shop", null,shopPanel.getPanel(), null);
		
		historyPanel.getPanel().setName("historyPanel");
		tabbedPane.addTab("History",null, historyPanel.getPanel(),null);
	}

}
