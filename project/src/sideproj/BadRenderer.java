package sideproj;

import static sideproj.VectorUtilities.*;

public class BadRenderer {

	public static ImageBuffer renderSlice(Volume vol, CuttingPlane cp) {
		// calculate length of diagonal through Volume
		float ld = len(toF(vol.getDims()));
		int l = (int) Math.ceil(ld);

		// resulting image is 2*l by 2*l in size to ensure that the
		// intersecting points are on the image later
		Boundingbox boundingbox = get2DBoundingbox(vol, cp);
		
//		int width = l * 2;
//		int height = width;
		int width = boundingbox.w;
		int height = boundingbox.h;
		ImageBuffer buff = new ImageBuffer(width, height);

		int[] imgBuffer = buff.buff;
		

		int[] intPoint3D = { 0, 0, 0 };
		float val = 0;
		int color = 0;
		int x, y;
		// iterate over imagebuffer
		int i = 0;
		for(y = (int) boundingbox.bottom; y < (int) boundingbox.top; y++){
		for (x = (int) boundingbox.left; x < (int) boundingbox.right; x++){

//			x = (i % width);
//			y = (i / width);
//			x = x - l; // subtract l so (0,0) is in center of image
//			y = y - l;
			// translate (x,y) to 3D point in volume
			float[] floatPoint3D = cp.planePoint2VolumePoint(x, y);
			toI(floatPoint3D, intPoint3D);

			if (vol.inBounds(intPoint3D)) {
				val = vol.getVal(intPoint3D);
				color = (int) (val * 255f);
				// //make argb
				color = (255 << 24) | (color << 16) | (color << 8) | color;
			} else {
				// transparent black
				color = 0;
			}
			imgBuffer[i] = color;
			i++;
		}
		}

		return buff;
	}

	public static Boundingbox get2DBoundingbox(Volume vol, CuttingPlane cut) {
		float[][] stdBase = { 
				{ 1f, 0f, 0f }, // x direction
				{ 0f, 1f, 0f }, // y direction
				{ 0f, 0f, 1f } // z direction
		};
		float w = vol.getW()-1;
		float h = vol.getH()-1;
		float d = vol.getD()-1;
		float[] corner0 = { 0f, 0f, 0f };
		float[] corner1 = {w, 0f, d};
		float[] corner2 = {w, h, 0f};
		float[] corner3 = {0f, h, d};
		
		float[] planePos = cut.getOrigin();
		float[] planeNormal = cut.getNormal();

		float[][] piercePoints = new float[][]{
				pierceThrough(corner0, stdBase[0], planePos,planeNormal),
				pierceThrough(corner0, stdBase[1], planePos,planeNormal),
				pierceThrough(corner0, stdBase[2], planePos,planeNormal),
				
				pierceThrough(corner1, stdBase[0], planePos,planeNormal),
				pierceThrough(corner1, stdBase[1], planePos,planeNormal),
				pierceThrough(corner1, stdBase[2], planePos,planeNormal),
				
				pierceThrough(corner2, stdBase[0], planePos,planeNormal),
				pierceThrough(corner2, stdBase[1], planePos,planeNormal),
				pierceThrough(corner2, stdBase[2], planePos,planeNormal),
				
				pierceThrough(corner3, stdBase[0], planePos,planeNormal),
				pierceThrough(corner3, stdBase[1], planePos,planeNormal),
				pierceThrough(corner3, stdBase[2], planePos,planeNormal)
		};
		
		float left,right,top,bottom; left=bottom = 1000000; 
		right=top = -100000;
		for(float[] vec: piercePoints){
			if(vec != null && vol.inBounds(toI(vec))){
				float[] point = translate3D_to_PlanePoint(vec, cut);
				
				// left 
				if(left > point[0]){
					left = point[0];
				}
				// right 
				if(right < point[0]){
					right = point[0];
				}
				// top 
				if(top < point[1]){
					top = point[1];
				}
				//bottom
				if(bottom > point[1]){
					bottom = point[1];
				}
			}
		}

		return new Boundingbox(left, right, top, bottom);
	}

	private static float[] translate3D_to_PlanePoint(float[] point, CuttingPlane cp) {
		// pRel = p - origin;
		float[] pointRelative = plus(point, neg(cp.origin));
		// LGS a*x + b*y = p
		float[] x = cp.xVec.clone();
		float[] y = cp.yVec.clone();
		float[] p = pointRelative.clone();
		float a, b, tmp; a = b = tmp = 0;
		
		if(!is0(x[0]) ){
			// Zeilenstufenform in 2ter und dritter zeile
			tmp = x[1] / x[0];
			x[1] = 0; y[1] -= y[0]*tmp; p[1] -=  p[0]*tmp;
			tmp = x[2] / x[0];
			x[2] = 0; y[2] -= y[0]*tmp; p[2] -=  p[0]*tmp;
			
			// berechne b
			if(!is0(y[1])) {
				b = p[1] / y[1];
			} else {
				b = p[2] / y[2];
			}
			// berechne a
			a = (p[0] - (b * y[0])) / x[0];
		} else if (!is0(x[1])) {
			// Zeilenstufenform in erster und dritter zeile
			tmp = x[0] / x[1];
			x[0] = 0; y[0] -= y[1]*tmp; p[0] -=  p[1]*tmp;
			tmp = x[2] / x[1];
			x[2] = 0; y[2] -= y[1]*tmp; p[2] -=  p[1]*tmp;
			
			// berechne b
			if(!is0(y[0])) {
				b = p[0] / y[0];
			} else {
				b = p[2] / y[2];
			}
			// berechne a
			a = (p[1] - (b * y[1])) / x[1];
		} else {
			// Zeilenstufenform in erster und 2ter zeile
			tmp = x[0] / x[2];
			x[0] = 0; y[0] -= y[2]*tmp; p[0] -=  p[2]*tmp;
			tmp = x[1] / x[2];
			x[1] = 0; y[1] -= y[2]*tmp; p[1] -=  p[2]*tmp;
			
			// berechne b
			if(!is0(y[0])) {
				b = p[0] / y[0];
			} else {
				b = p[1] / y[1];
			}
			// berechne a
			a = (p[2] - (b * y[2])) / x[2];
		}

		return new float[]{a,b};
	}

	private static boolean is0(float f) {
		return Math.abs(f) < 1e-5f;
	}

	public static float[] pierceThrough(float[] Lpos, float[] dir,
			float[] Ppos, float[] normal) {
		// assuming vectors are all normalized
		float denom = dot(normal, dir);
		if (!is0(denom)) {
			float[] p0l0 = plus(Ppos, neg(Lpos));
			float t = dot(p0l0, normal) / denom;
			// if((t < 0)){
			// return null;
			// }
			return plus(Lpos, mult(dir, t));
		} else {
			// parallel so no pierce point
			return null;
		}
	}
	
	private static class Boundingbox {
		float left; 
		float right; 
		float top; 
		float bottom;
		
		float width;
		float height;
		
		int w;
		int h;
		public Boundingbox(float left, float right, float top, float bottom) {
			left = left -10; right = right +10;
			
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
			
			width = right - left;
			height = top - bottom;
			w = (int) right; w -= (int) left;
			h = (int) top; h -= (int) bottom;
		}
		
	}
	
	public static class ImageBuffer {
		public int[] buff;
		public int w;
		public int h;
		public ImageBuffer(int w, int h) {
			this.w = w;
			this.h = h;
			this.buff = new int[w*h];
		}
		
	}

}
