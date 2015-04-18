package sideproj.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import sideproj.FilterTree;
import sideproj.ImgFilter;
import sideproj.ImgMerge;

@SuppressWarnings("serial")
public class ApplicationWindow extends JFrame {

	private FilterTreePanel treePanel;
	private JToolBar toolbar;
	private LinkedList<ImgFilter> availableFilters = new LinkedList<>();
	
	public ApplicationWindow() {
		this.setTitle("Filter Graph");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		treePanel = new FilterTreePanel();
		toolbar = new JToolBar();
		setupToolbar();
		this.getContentPane().add(toolbar, BorderLayout.NORTH);
		this.getContentPane().add(treePanel, BorderLayout.CENTER);
	}

	private void setupToolbar() {
		toolbar.add(new AbstractAction("New Merger") {
			@Override
			public void actionPerformed(ActionEvent e) {
				treePanel.addNode(new FilterTree.MergeNode(new ImgMerge()));
			}
		});
		toolbar.add(new AbstractAction("New Filter") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				execFilterNodeDialog();
			}
		});
	}
	
	public FilterTreePanel getTreePanel() {
		return treePanel;
	}
	
	private void execFilterNodeDialog(){
		JDialog dialog = new JDialog(this, true);
		dialog.setTitle("Select Filter");
		JComboBox<ImgFilter> cbx = new JComboBox<>(availableFilters.toArray(new ImgFilter[availableFilters.size()]));
		cbx.setRenderer(new EasyListCellRenderer<ImgFilter>(new EasyListCellRenderer.CellTextRetriever<ImgFilter>() {
			@Override
			public String getCellText(ImgFilter value) {
				return value.filterName();
			}
		}));
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(cbx,BorderLayout.CENTER);
		JButton okBtn = new JButton("Ok");
		dialog.getContentPane().add(okBtn, BorderLayout.SOUTH);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImgFilter filter = (ImgFilter) cbx.getSelectedItem();
				if(filter != null)
					treePanel.addNode(new FilterTree.FilterNode(filter.copy()));
				dialog.dispose();
			}
		});
		dialog.pack();
		dialog.setVisible(true);
	}
	
	public void registerFilter(ImgFilter filter){
		this.availableFilters.add(filter);
	}
}
