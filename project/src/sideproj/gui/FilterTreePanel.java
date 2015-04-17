package sideproj.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javafx.util.Pair;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sideproj.FilterTree;
import sideproj.FilterTree.Node;
import sideproj.Img;
import util.ImageFrame;
import util.ImageFrame.ImagePanel;

@SuppressWarnings("serial")
public class FilterTreePanel extends JPanel {
	
	public class NodeWidget extends JPanel {
		private Node node;
		private JLabel[] inConnectors;
		private JPanel inPanel;
		private JLabel outConnector;
		private ImageFrame.ImagePanel resultImg;
		
		public NodeWidget(Node node) {
			this.node = node;
			this.setLayout(new BorderLayout());
			JLabel dragArea = new JLabel("<+>", JLabel.CENTER);
			outConnector = new JLabel("}", JLabel.RIGHT);
			this.add(dragArea, BorderLayout.NORTH);
			this.add(node.getEditor(), BorderLayout.CENTER);
			this.add(outConnector, BorderLayout.EAST);
			resultImg = new ImageFrame.ImagePanel();
			resultImg.setPreferredSize(new Dimension(150, 100));
			this.add(resultImg, BorderLayout.SOUTH);
			updatePreview();
			
			new MouseDragAdapter(dragArea) {
				@Override
				public void mouseDragAction(MouseEvent e, Point delta) {
					Rectangle bounds = new Rectangle( NodeWidget.this.getBounds() );
					bounds.translate(delta.x, delta.y);
					NodeWidget.this.setBounds(bounds);
					FilterTreePanel.this.repaint();
				}
			};
			
			new MouseDragAdapter(outConnector) {
				@Override
				public void mouseReleaseAction(MouseEvent e, Point delta) {
					FilterTreePanel.this.attemptConnection(NodeWidget.this, getOutConnectorLocationOnScreen(), e.getLocationOnScreen());
				}
				
				@Override
				public void mouseDragAction(MouseEvent e, Point delta) {
					FilterTreePanel.this.onConnectionDrag(getOutConnectorLocationOnScreen(), e.getLocationOnScreen());
				}
			};
			
			this.resultImg.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(SwingUtilities.isRightMouseButton(e))
						NodeWidget.this.updatePreview();
				}
			});
			
			this.inConnectors = new JLabel[node.numRequiredChildren()];
			this.inPanel = new JPanel();
			inPanel.setLayout(new BoxLayout(inPanel, BoxLayout.Y_AXIS));
			for(int i = 0; i < inConnectors.length; i++){
				inConnectors[i] = new JLabel("}", JLabel.LEFT);
				inPanel.add(inConnectors[i]);
				final int index = i;
				inConnectors[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(SwingUtilities.isRightMouseButton(e)){
							NodeWidget.this.getNode().setChild(index, null);
							FilterTreePanel.this.repaint();
						}
					}
				}); 
			}
			this.add(inPanel, BorderLayout.WEST);
			
		}
		
		public Node getNode() {
			return node;
		}
		
		@Override
		public String toString() {
			return super.toString() + " " + node.getClass().getSimpleName();
		}
		
		public int getInputConnectorIndex(Point screenpoint){
			Component c = this.inPanel.getComponentAt(screenPointToComponentPoint(screenpoint, this.inPanel));
			for (int i = 0; i < this.inConnectors.length; i++) {
				if(inConnectors[i] == c)
					return i;
			}
			return -1;
		}
		
		public Point getOutConnectorLocationOnScreen(){
			Point p = new Point(this.outConnector.getLocationOnScreen());
			p.translate(this.outConnector.getWidth(), this.outConnector.getHeight()/2);
			return p;
		}
		
		public Point getInConnectorLocationOnscreen(int index){
			if(index >= 0 && index < this.inConnectors.length){
				Point p = new Point(this.inConnectors[index].getLocationOnScreen());
				p.translate(0, this.inConnectors[index].getHeight()/2);
				return p;
			}
			return null;
		}
		
		private void updatePreview(){
			if(this.node.allChildrenSetRecursive()){
				Img img = this.node.renderedImage();
				this.resultImg.setImg(img.toBufferedImage());
			}
		}
	}
	
	
	// ---
	
	private LinkedList<NodeWidget> elements = new LinkedList<FilterTreePanel.NodeWidget>();
	private Pair<Point, Point> connectionLine = null;
	
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
		this.validate();
	}
	
	private void attemptConnection(NodeWidget sender, Point senderLocation, Point plugPoint){
		Component component = getComponentAt(screenPointToComponentPoint(plugPoint, this));
		if(component != null && component != this && component != sender && component instanceof NodeWidget){
			NodeWidget widget = (NodeWidget) component;
			int index = widget.getInputConnectorIndex(plugPoint);
			if(index >= 0 && !sender.getNode().getNodesRecursive().contains(widget.getNode())){	
				widget.getNode().setChild(index, sender.getNode());
				System.out.println("connection made" + index);
			}
		}
		this.connectionLine = null;
		repaint();
	}
	
	private void onConnectionDrag(Point sender, Point cursor) {
		this.connectionLine = new Pair<Point, Point>(screenPointToComponentPoint(sender, this), screenPointToComponentPoint(cursor, this));
		this.repaint();
	}
	
	private static Point screenPointToComponentPoint(Point p, Component comp){
		Point pC = new Point(p);
		pC.translate(-comp.getLocationOnScreen().x, -comp.getLocationOnScreen().y);
		return pC;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// paint connections
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.blue);
		for(NodeWidget widget: this.elements){
			for(int i = 0; i < widget.getNode().numRequiredChildren(); i++){
				NodeWidget senderwidget = getNodeWidgetForNode(widget.getNode().getChild(i));
				if(senderwidget != null){
					Point p1 = screenPointToComponentPoint(senderwidget.getOutConnectorLocationOnScreen(), this);
					Point p2 = screenPointToComponentPoint(widget.getInConnectorLocationOnscreen(i), this);
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}
		}
		// paint drag line if in progress
		g.setColor(Color.red);
		if(connectionLine != null)
			g.drawLine(connectionLine.getKey().x,connectionLine.getKey().y, connectionLine.getValue().x, connectionLine.getValue().y);
	}
	
	public NodeWidget getNodeWidgetForNode(Node node){
		for(NodeWidget widget: this.elements){
			if(widget.getNode()==node)
				return widget;
		}
		return null;
	}
	
}
