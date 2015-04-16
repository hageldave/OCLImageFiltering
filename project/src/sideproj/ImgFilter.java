package sideproj;

public abstract class ImgFilter {
	
	public abstract void applyTo(Img img);
	
	public static int a(int color){
		return (color >> 24) & 0xff;
	}
	
	public static int r(int color){
		return (color >> 16) & 0xff;
	}
	
	public static int g(int color){
		return (color >> 8) & 0xff;
	}
	
	public static int b(int color){
		return (color) & 0xff;
	}
	
	public static int rgba(int a, int r, int g, int b){
		return (a<<24)|(r<<16)|(g<<8)|b;
	}
	
	public static int rgba_safe(int a, int r, int g, int b){
		return rgba(a & 0xff, r & 0xff, g & 0xff, b & 0xff);
	}
	
	public static int rgba_bounded(int a, int r, int g, int b){
		return rgba(
				a > 255 ? 255: a < 0 ? 0:a, 
				r > 255 ? 255: r < 0 ? 0:r, 
				g > 255 ? 255: g < 0 ? 0:g,
				b > 255 ? 255: b < 0 ? 0:b);
	}
	
	public static int rgb(int r, int g, int b){
		return (0xff<<24)|(r<<16)|(g<<8)|b;
	}
}
