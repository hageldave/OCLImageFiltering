package sideproj.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sideproj.FilterTree;
import sideproj.FilterTree.Node;

@SuppressWarnings("serial")
public class FilterTreePanel extends JPanel {
	
	public class NodeWidget extends JPanel {
		private Node node;
		
		public NodeWidget(Node node) {
			this.node = node;
			this.setLayout(new BorderLayout());
			JLabel dragArea = new JLabel("<+>", JLabel.CENTER);
			JLabel outConnector = new JLabel("}", JLabel.RIGHT);
			this.add(dragArea, BorderLayout.NORTH);
			this.add(node.getEditor(), BorderLayout.CENTER);
			this.add(outConnector, BorderLayout.EAST);
			
			new MouseDragAdapter(dragArea) {
				@Override
				public void mouseDragAction(MouseEvent e, Point delta) {
					Rectangle bounds = new Rectangle( NodeWidget.this.getBounds() );
					bounds.translate(delta.x, delta.y);
					NodeWidget.this.setBounds(bounds);
				}
			};
			
			new MouseDragAdapter(outConnector) {
				@Override
				public void mouseDragAction(MouseEvent e, Point delta) {
					FilterTreePanel.this.attemptConnection(NodeWidget.this, outConnector.getLocationOnScreen(), e.getLocationOnScreen());
				}
			};
		}
		
		public Node getNode() {
			return node;
		}
		
		@Override
		public String toString() {
			return super.toString() + " " + node.getClass().getSimpleName();
		}
	}
	
	
	// ---
	
	LinkedList<NodeWidget> elements = new LinkedList<FilterTreePanel.NodeWidget>();
	
	public FilterTreePanel() {
		this.setLayout(null);
		this.setMinimumSize(new Dimension(500, 500));
		this.setPreferredSize(getMinimumSize());
	}
	
	public void addTree(FilterTree tree){
		for(Node node: tree.allNodes()){
			addNode(node);
		}
	}
	
	public void addNode(Node node){
		NodeWidget widget = new NodeWidget(node);
		widget.setBounds(new Rectangle(new Point(0, 0), widget.getPreferredSize()));
		elements.add(widget);
		this.add(widget);
		this.repaint();
	}
	
	private void attemptConnection(NodeWidget sender, Point senderLocation, Point plugPoint){
		Component component = getComponentAt(screenPointToComponentPoint(plugPoint, this));
		if(component != null && component != this && component != sender && component instanceof NodeWidget){
			NodeWidget widget = (NodeWidget) component;
			System.out.println(widget.getComponentAt(screenPointToComponentPoint(plugPoint, widget)));
		}
		
		// todo: visualize connection
	}
	
	private static Point screenPointToComponentPoint(Point p, Component comp){
		Point pC = new Point(p);
		pC.translate(-comp.getLocationOnScreen().x, -comp.getLocationOnScreen().y);
		return pC;
	}
	
}
