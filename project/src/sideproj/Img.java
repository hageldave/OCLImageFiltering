package sideproj;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import util.BufferedImageFactory;

public class Img {
	int[] data;
	Dimension dimension;
	
	public Img(int width, int height){
		this(new Dimension(width, height));
	}
	
	public Img(Dimension dimension){
		this.data = new int[dimension.width*dimension.height];
		this.dimension = dimension;
	}
	
	public Img(BufferedImage img){
		this.dimension = new Dimension(img.getWidth(),img.getHeight());
		this.data = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
	}
	
	private Img(Dimension dim, int[] data){
		this.dimension = dim;
		this.data = data;
	}
	
	public Dimension getDimension() {
		return dimension;
	}
	
	public int getWidth(){
		return dimension.width;
	}
	
	public int getHeight(){
		return dimension.height;
	}
	
	public int numPixels(){
		return getWidth()*getHeight();
	}
	
	public int[] getData() {
		return data;
	}
	
	public int pixel(int x, int y){
		return this.data[y*dimension.width + x];
	}
	
	public void setPixel(int x, int y, int px){
		this.data[y*dimension.width + x] = px;
	}
	
	public Img copy(){
		return new Img(getDimension(), Arrays.copyOf(getData(), getData().length));
	}
	
	public BufferedImage toBufferedImage(){
		BufferedImage img = BufferedImageFactory.getINT_ARGB(getDimension());
		img.setRGB(0, 0, getWidth(), getHeight(), getData(), 0, getWidth());
		return img;
	}
}
