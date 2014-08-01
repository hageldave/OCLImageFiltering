package test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import CLDatatypes.CLBufferInt;
import util.BufferedImageFactory;
import util.CLBoilerplate;

public class Tester {
	
	public static void main(String[] args) throws IOException {
		if(!CLBoilerplate.isClInitialized()){
			CLBoilerplate.initCL();
		}
		
		BufferedImage img = BufferedImageFactory.getINT_ARGB(new ImageIcon("res/baywp.jpg").getImage());
		
		DataBufferInt buffer = (DataBufferInt) img.getRaster().getDataBuffer();
		int[] data = buffer.getData();
		
		FilterGrayscale filter = new FilterGrayscale(CLBoilerplate.getCLInstance());
		FilterBoxBlur blur = new FilterBoxBlur(CLBoilerplate.getCLInstance());
		FilterColorchanels color = new FilterColorchanels(CLBoilerplate.getCLInstance());
		FilterResize resize = new FilterResize(CLBoilerplate.getCLInstance());
		FilterContrast contrast = new FilterContrast(CLBoilerplate.getCLInstance());
		FilterMask mask = new FilterMask(CLBoilerplate.getCLInstance());
		
		CLBufferInt devicebuffer = null;
		
		long time = System.currentTimeMillis();
		
		filter.setHostInputBuffer(data);
		filter.apply();
		devicebuffer = filter.getDeviceOutputBuffer();
		
		color.setDeltas(0, 10, 20, -22);
		
		color.setDeviceInputBuffer(devicebuffer);
		color.apply();
		devicebuffer = color.getDeviceOutputBuffer();
		
		
		resize.setSourceDimensions(img.getWidth(), img.getHeight());
		
		img = BufferedImageFactory.getINT_ARGB(1000, 1000);
		buffer = (DataBufferInt) img.getRaster().getDataBuffer();
		data = buffer.getData();
		
		resize.setTargetDimensions(img.getWidth(), img.getHeight());
		
		resize.setDeviceInputBuffer(devicebuffer);
		resize.apply();
		devicebuffer = resize.getDeviceOutputBuffer();
		
		int[] maskbuffer = resize.getHostOutputBuffer(); // for later
		
		contrast.setIntensity(0.5f);
		contrast.setThreshold(80);
		
		contrast.setDeviceInputBuffer(devicebuffer);
		contrast.apply();
		devicebuffer = contrast.getDeviceOutputBuffer();
		
		
		blur.setDimensions(img.getWidth(), img.getHeight());
		blur.setRadius(10);
		blur.setNumPasses(3);
		
		blur.setDeviceInputBuffer(devicebuffer);
		blur.apply();
		devicebuffer = blur.getDeviceOutputBuffer();
		
//		mask.setHostMaskBuffer(maskbuffer);
		mask.setDeviceMaskBuffer(devicebuffer);
		
		mask.setDeviceInputBuffer(devicebuffer);
		mask.apply();
		devicebuffer = mask.getDeviceOutputBuffer();
		
		devicebuffer.toHostBuffer(blur.getCLInstance().queue, data);
		
		time = System.currentTimeMillis() - time;
		System.out.println(time + " ms");
		
		ImageIO.write(img, "png", new File("res/out.png"));
	}

}
