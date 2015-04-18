package sideproj;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sideproj.ImgMerge.MergePolicy;
import sideproj.gui.EasyListCellRenderer;

public class FilterTree {
	
	public static abstract class Node {
		protected Node[] children;
		
		public Node(int numChildren) {
			children = new Node[numChildren];
		}
		
		public Node getChild(int index){
			assert(index < numRequiredChildren());
			return children[index];
		}
		
		public void setChild(int index, Node child){
			assert(index < numRequiredChildren());
			children[index] = child;
		}
		
		public int numRequiredChildren(){
			return children.length;
		}
		
		public int numChildren(){
			int i = 0;
			for(Node child: children)
				if(child != null)
					i++;
			return i;
		}
		
		public int numChildrenRecursive(){
			int i = numChildren();
			for(Node child: children)
				if(child != null)
					i += child.numChildrenRecursive();
			return i;
		}
		
		public boolean allChildrenSet(){
			for(Node child: children){
				if(child == null)
					return false;
			}
			return true;
		}
		
		public boolean allChildrenSetRecursive(){
			if(allChildrenSet()){
				for(Node child: children){
					if(!child.allChildrenSetRecursive())
						return false;
				}
				return true;
			}
			return false;
		}
		
		public List<Node> getNodesRecursive(){
			LinkedList<Node> nodes = new LinkedList<>();
			searchNodesBFS(this, nodes);
			return nodes;
		}
		
		public abstract Img renderedImage();
		public abstract Component getEditor();
	}
	
	// ---
	
	public static class Source extends Node {
		private Img img;
		
		public Source(Img img) {
			super(0);
			this.img = img;
			this.children = new Node[0];
		}

		@Override
		public Img renderedImage() {
			return this.img.copy();
		}

		@Override
		public Component getEditor() {
			return new JLabel("Source");
		}
	}
	
	// ---
	
	public static class FilterNode extends Node {
		
		private ImgFilter filter;
		
		public FilterNode(ImgFilter filter) {
			super(1);
			this.filter = filter;
		}

		@Override
		public Img renderedImage() {
			assert(allChildrenSet());
			Img img = getChild(0).renderedImage();
			this.filter.applyTo(img);
			return img;
		}

		@Override
		public Component getEditor() {
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel(filter.filterName()), BorderLayout.NORTH);
			if(filter.getEditor() != null){
				panel.add(filter.getEditor(), BorderLayout.CENTER);
			}
			return panel;
		}
		
	}
	
	// ---
	
	public static class MergeNode extends Node {
		
		private ImgMerge merge;
		private JComboBox<ImgMerge.MergePolicy> policyCbx = null;
		
		public MergeNode(ImgMerge merge) {
			super(2);
			this.merge = merge;
		}

		@Override
		public Img renderedImage() {
			Img img0 = getChild(0).renderedImage();
			Img img1 = getChild(1).renderedImage();
			merge.merge(img0, img1);
			return img0;
		}

		@Override
		public Component getEditor() {
			if(policyCbx == null){
				policyCbx = new JComboBox<>(ImgMerge.MergePolicy.values());
				policyCbx.setSelectedIndex(merge.getMergePolicy().ordinal());
				policyCbx.setRenderer(new EasyListCellRenderer<MergePolicy>());
				policyCbx.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						MergePolicy policy = (MergePolicy) policyCbx.getSelectedItem();
						if(policy != null)
							merge.setMergePolicy(policy);
					}
				});
			}
			return policyCbx;
		}
		
	}
	
	// ----- ----- ----- ----- -----
	// FILTER TREE
	
	private Node root;
	
	public FilterTree() {
		root = new Node(1) {
			@Override
			public Img renderedImage() {
				return getChild(0).renderedImage();
			}

			@Override
			public Component getEditor() {
				return new JLabel("Root");
			}
		};
	}
	
	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public boolean allChildrenSet() {
		return root.allChildrenSet();
	}

	public boolean allChildrenSetRecursive() {
		return root.allChildrenSetRecursive();
	}

	public Img renderedImage() {
		assert(allChildrenSetRecursive());
		return root.renderedImage();
	}
	
	public List<Node> allNodes(){
		LinkedList<Node> nodes = new LinkedList<Node>();
		searchNodesBFS(getRoot(), nodes);
		return nodes;
	}
	
	public static void searchNodesBFS(Node startNode, List<Node> foundNodes){
		if(foundNodes.contains(startNode)){
			return;
		} else {
			foundNodes.add(startNode);
			for(Node child: startNode.children){
				searchNodesBFS(child, foundNodes);
			}
		}
	}
	
	
}
