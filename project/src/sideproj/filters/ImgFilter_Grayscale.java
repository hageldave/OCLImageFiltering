package sideproj.filters;

import sideproj.Img;
import sideproj.ImgFilter;

public class ImgFilter_Grayscale extends ImgFilter {

	@Override
	public void applyTo(Img img) {
		for(int i = 0; i < img.getData().length; i++){
			int c = img.getData()[i];
			int val = ( ((r(c) * 3)/10) + ((g(c) * 6)/10) + (b(c)/10) );
			img.getData()[i] = rgba(a(c),val,val,val);
		}	
	}

	public static ImgFilter getInstance() {
		return new ImgFilter_Grayscale();
	}
}
