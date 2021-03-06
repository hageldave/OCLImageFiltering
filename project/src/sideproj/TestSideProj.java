package sideproj;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import static sideproj.FilterTree.*;
import sideproj.filters.ImgFilter_Bitmask;
import sideproj.filters.ImgFilter_Grayscale;
import sideproj.gui.ApplicationWindow;
import sideproj.gui.FilterTreePanel;
import util.BufferedImageFactory;
import util.ImageFrame;

public class TestSideProj {
	
	public static void main(String[] args) {
		BufferedImage b_img = BufferedImageFactory.getINT_ARGB(new ImageIcon("res/baywp.jpg").getImage());
		Img img = new Img(b_img);
		Img img2 = img.copy();
		
		ImgFilter_Bitmask.getInstance(0xff0000).applyTo(img);
		Img tmp = img.copy();
		ImgFilter_Grayscale.getInstance().applyTo(img);
		ImgMerge.getInstance().merge(img, tmp);
		
//		FilterTree tree = new FilterTree();
//		Node sourceImg = new Source(img2);
//		Node nodeGrayscale = new FilterNode(ImgFilter_Grayscale.getInstance());
//		Node nodeBitmask = new FilterNode(ImgFilter_Bitmask.getInstance(0xff0000));
//		Node nodeMerge = new MergeNode(ImgMerge.getInstance());
//		
//		{ // build tree
//			tree.setRoot(nodeMerge);
//			nodeMerge.setChild(0, nodeBitmask);
//			nodeMerge.setChild(1, nodeGrayscale);
//			nodeGrayscale.setChild(0, nodeBitmask);
//			nodeBitmask.setChild(0, sourceImg);
//		}
		
		FilterTree initialTree = new FilterTree();
		initialTree.getRoot().setChild(0, new Source(img2));

		ApplicationWindow window = new ApplicationWindow();
		window.registerFilter(new ImgFilter_Bitmask());
		window.registerFilter(new ImgFilter_Grayscale());
		window.getTreePanel().addTree(initialTree);
		window.pack();
		window.setVisible(true);
//		ImageFrame.display(img.toBufferedImage());
//		ImageFrame.display(tree.renderedImage().toBufferedImage());
	}
}
