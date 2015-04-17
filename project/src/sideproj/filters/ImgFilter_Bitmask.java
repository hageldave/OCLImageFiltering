package sideproj.filters;

import sideproj.Img;
import sideproj.ImgFilter;

public class ImgFilter_Bitmask extends ImgFilter {

	int bitmask;
	
	public ImgFilter_Bitmask(int bitMask) {
		this.bitmask = bitMask | 0xff000000;
	}
	
	@Override
	public void applyTo(Img img) {
		for(int i = 0; i < img.getData().length; i++){
			img.getData()[i] = img.getData()[i] & this.bitmask;
		}	
	}
	

	public static ImgFilter getInstance(int bitmask) {
		return new ImgFilter_Bitmask(bitmask);
	}

	@Override
	public String filterName() {
		return "Bitmask Filter";
	}
	
}
