package sideproj;

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
		
		public abstract Img renderedImage();
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
		
	}
	
	// ---
	
	public static class MergeNode extends Node {
		
		private ImgMerge merge;
		
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
	
	
}
