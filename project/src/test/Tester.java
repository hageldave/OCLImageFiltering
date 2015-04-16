package test;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import util.BufferedImageFactory;
import util.CLBoilerplate;
import util.FilterImageContainer;
import util.ImageFrame;

public class Tester {
	
//	public static void main(String[] args) throws IOException {
//		if(!CLBoilerplate.isClInitialized()){
//			CLBoilerplate.initCL();
//		}
//		
//		BufferedImage img = BufferedImageFactory.getINT_ARGB(new ImageIcon("res/baywp.jpg").getImage());
//		
//		DataBufferInt buffer = (DataBufferInt) img.getRaster().getDataBuffer();
//		int[] data = buffer.getData();
//		
//		GrayscaleFilter gray = new GrayscaleFilter(CLBoilerplate.getCLInstance());
//		BoxBlurFilter blur = new BoxBlurFilter(CLBoilerplate.getCLInstance());
//		ColorchanelsFilter color = new ColorchanelsFilter(CLBoilerplate.getCLInstance());
//		ResizeFilter resize = new ResizeFilter(CLBoilerplate.getCLInstance());
//		ContrastFilter contrast = new ContrastFilter(CLBoilerplate.getCLInstance());
//		MaskFilter mask = new MaskFilter(CLBoilerplate.getCLInstance());
//		
//		color.setDeltas(0, 10, 20, -22);
//		resize.setOutputDimension(new Dimension(5000, 5000) );
//		blur.setRadius(50);
//		blur.setNumPasses(4);
//		contrast.setIntensity(0.5f);
//		contrast.setThreshold(80);
//		mask.setHostMaskBuffer(data, new Dimension(img.getWidth(), img.getHeight()));
//		
//		FilterImageContainer filterImage = new FilterImageContainer(data, new Dimension(img.getWidth(), img.getHeight()));
//		
//		long time = System.currentTimeMillis();
//		filterImage = filterImage
//				.apply(resize)
////				.apply(gray)
//				.apply(color)
//				.apply(contrast)
//				.apply(blur)
////				.apply(mask)
//				;
//		
//		img = BufferedImageFactory.getINT_ARGB(filterImage.getDimension());
//		buffer = (DataBufferInt) img.getRaster().getDataBuffer();
//		data = buffer.getData();
//		
//		filterImage.copyToHostBuffer(data);
//		
//		time = System.currentTimeMillis() - time;
//		System.out.println(time + " ms");
//		
//		final BufferedImage image = img;
//		SwingUtilities.invokeLater( () -> {
//			ImageFrame frame = new ImageFrame();
//			frame.setImg(image);
//			frame.setVisible(true);
//		} );
//		
////		ImageIO.write(img, "png", new File("res/out.png"));
////		System.out.println("saved. done.");
//	}

}
