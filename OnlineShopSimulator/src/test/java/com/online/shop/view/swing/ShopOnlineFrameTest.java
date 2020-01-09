package com.online.shop.view.swing;


import java.awt.Dimension;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

public class ShopOnlineFrameTest extends AssertJSwingJUnitTestCase {
	
	private static final int HEIGHT = 400;
	private static final int WIDTH = 800;

	private ShopOnlineFrame shopOnlineFrame;
	private FrameFixture window;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			ShopViewPanel shopViewPanel = new ShopViewPanel();
			HistoryViewPanel historyViewPanel = new HistoryViewPanel();
			shopOnlineFrame = new ShopOnlineFrame(shopViewPanel,historyViewPanel);
			window = new FrameFixture(robot(), shopOnlineFrame.getFrame());

		});
		Dimension dimension = new Dimension(WIDTH, HEIGHT);
		window.show(dimension);
	}

	@Test @GUITest
	public void testInitalFrameContent() {

		window.tabbedPane("tabbedPanel").requireTabTitles("Shop",
				"History");
		window.tabbedPane("tabbedPanel").selectTab(0);
		window.panel("shopPanel");
		window.tabbedPane("tabbedPanel").selectTab(1);
		window.panel("historyPanel");
	}

}
