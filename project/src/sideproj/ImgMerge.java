package sideproj;

import static sideproj.ImgFilter.*;

public class ImgMerge {
	
	public static enum MergePolicy {
		AVERAGE("Average"),
		ADD("Add"),
		SUBTRACT("Subtract"),
		MULTIPLY("Multiply"),
		SCREEN("Screen");
		
		public String name;
		private MergePolicy(String name) {
			this.name =name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	// ---
	private MergePolicy policy;
	
	public ImgMerge(MergePolicy mergePolicy) {
		this.policy = mergePolicy;
	}
	
	public ImgMerge() {
		this(MergePolicy.AVERAGE);
	}
	
	public MergePolicy getMergePolicy() {
		return policy;
	}
	
	public void setMergePolicy(MergePolicy policy) {
		this.policy = policy;
	}
	
	public void merge(Img img1, Img img2){
		assert(img1.numPixels() == img2.numPixels());
		for(int i = 0; i < img1.numPixels(); i++){
			int c1 = img1.getData()[i];
			int c2 = img2.getData()[i];
			int c = rgba_bounded((a(c1)+a(c2))/2, mergeChannel(r(c1),r(c2)), mergeChannel(g(c1),g(c2)), mergeChannel(b(c1),b(c2)));
			img1.getData()[i] = c;
		}
	}
	
	private int mergeChannel(int c1, int c2){
		switch (policy) {
		case AVERAGE:
			return (c1+c2)>>1;
		case ADD:
			return (c1+c2);
		case SUBTRACT:
			return (c1-c2);
		case MULTIPLY:
			return (c1*c2)>>8;
		case SCREEN:
			return 0xff-(( (0xff-c1)*(0xff-c2) )>>8);
		default:
			return c1;
		}
	}
	
	public static ImgMerge getInstance(){
		return new ImgMerge();
	}
}
