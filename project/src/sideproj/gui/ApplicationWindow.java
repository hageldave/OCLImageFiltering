package sideproj.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import sideproj.FilterTree;
import sideproj.ImgMerge;

@SuppressWarnings("serial")
public class ApplicationWindow extends JFrame {

	private FilterTreePanel treePanel;
	private JToolBar toolbar;
	
	public ApplicationWindow() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		treePanel = new FilterTreePanel();
		toolbar = new JToolBar();
		setupToolbar();
		this.getContentPane().add(toolbar, BorderLayout.PAGE_END);
		this.getContentPane().add(treePanel, BorderLayout.CENTER);
	}

	private void setupToolbar() {
		toolbar.add(new AbstractAction("New Merger") {
			@Override
			public void actionPerformed(ActionEvent e) {
				treePanel.addNode(new FilterTree.MergeNode(new ImgMerge()));
			}
		});
	}
	
	public FilterTreePanel getTreePanel() {
		return treePanel;
	}
}
