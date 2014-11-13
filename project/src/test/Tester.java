package test;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import util.BufferedImageFactory;
import util.CLBoilerplate;
import util.FilterImageContainer;

public class Tester {
	
	public static void main(String[] args) throws IOException {
		if(!CLBoilerplate.isClInitialized()){
			CLBoilerplate.initCL();
		}
		
		BufferedImage img = BufferedImageFactory.getINT_ARGB(new ImageIcon("res/baywp.jpg").getImage());
		
		DataBufferInt buffer = (DataBufferInt) img.getRaster().getDataBuffer();
		int[] data = buffer.getData();
		
		GrayscaleFilter filter = new GrayscaleFilter(CLBoilerplate.getCLInstance());
		BoxBlurFilter blur = new BoxBlurFilter(CLBoilerplate.getCLInstance());
		ColorchanelsFilter color = new ColorchanelsFilter(CLBoilerplate.getCLInstance());
		ResizeFilter resize = new ResizeFilter(CLBoilerplate.getCLInstance());
		ContrastFilter contrast = new ContrastFilter(CLBoilerplate.getCLInstance());
		MaskFilter mask = new MaskFilter(CLBoilerplate.getCLInstance());
		
		color.setDeltas(0, 10, 20, -22);
		resize.setOutputDimension(new Dimension(5000, 5000) );
		blur.setRadius(50);
		blur.setNumPasses(3);
		contrast.setIntensity(0.5f);
		contrast.setThreshold(80);
		mask.setHostMaskBuffer(data, new Dimension(img.getWidth(), img.getHeight()));
		
		FilterImageContainer filterImage = new FilterImageContainer(data, new Dimension(img.getWidth(), img.getHeight()));
		
		long time = System.currentTimeMillis();
		filterImage = filterImage.apply(filter).apply(color).apply(contrast).apply(resize).apply(blur).apply(mask);
		
		img = BufferedImageFactory.getINT_ARGB(blur.getOutputDimension());
		buffer = (DataBufferInt) img.getRaster().getDataBuffer();
		data = buffer.getData();
		
		filterImage.copyToHostBuffer(data);
		
		time = System.currentTimeMillis() - time;
		System.out.println(time + " ms");
		
		ImageIO.write(img, "png", new File("res/out.png"));
		System.out.println("saved. done.");
	}

}
