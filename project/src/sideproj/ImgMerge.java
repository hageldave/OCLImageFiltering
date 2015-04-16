package sideproj;

import static sideproj.ImgFilter.*;

public class ImgMerge {
	
	public void merge(Img img1, Img img2){
		assert(img1.numPixels() == img2.numPixels());
		for(int i = 0; i < img1.numPixels(); i++){
			int c1 = img1.getData()[i];
			int c2 = img2.getData()[i];
			int c = rgba_bounded((a(c1)+a(c2))/2, (r(c1)+r(c2))/2, (g(c1)+g(c2))/2, (b(c1)+b(c2))/2);
			img1.getData()[i] = c;
		}
	}
	
	public static ImgMerge getInstance(){
		return new ImgMerge();
	}
}
