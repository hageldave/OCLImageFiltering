package sideproj;

public class VectorUtilities {

	/**
	 * multiplies 3x3 matrix by 3d vector
	 * @param m matrix
	 * @param v vector
	 * @return resulting vector
	 */
	public static float[] multiply(float[][] m, float[] v){
		float[] res = new float[3];
		res[0] = m[0][0]*v[0] + m[0][1]*v[1] + m[0][2]*v[2];
		res[1] = m[1][0]*v[0] + m[1][1]*v[1] + m[1][2]*v[2];
		res[2] = m[2][0]*v[0] + m[2][1]*v[1] + m[2][2]*v[2];
		return res;
	}
	
	/**
	 * rotates vector arround x axis by angle a (in radians)
	 * @param v 
	 * @param a
	 * @return
	 */
	public static float[] rotX(float[] v, float a){
		float c = (float) Math.cos(a);
		float s = (float) Math.sin(a);
		float[][] m = {
				{1f, 0f, 0f},
				{0f,  c, -s},
				{0f,  s,  c}
		};
		
		return multiply(m, v);
	}
	
	/**
	 * rotates vector arround y axis by angle a (in radians)
	 * @param v 
	 * @param a
	 * @return
	 */
	public static float[] rotY(float[] v, float a){
		float c = (float) Math.cos(a);
		float s = (float) Math.sin(a);
		float[][] m = {
				{ c, 0f,  s},
				{0f, 1f, 0f},
				{-s, 0f,  c}
		};
		
		return multiply(m, v);
	}
	
	/** calcualates cross product of 2 vectors */
	public static float[] cross(float[] v1, float[] v2){
		float[] res = new float[3];
		
		res[0] = v1[1]*v2[2] - v1[2]*v2[1];
		res[1] = v1[2]*v2[0] - v1[0]*v2[2];
		res[2] = v1[0]*v2[1] - v1[1]*v2[0];
		
		return res;
	}
	
	/** returns a unit vector of v */
	public static float[] unit(float[] v){
		float l = len(v);
		return new float[]{v[0]/l, v[1]/l, v[2]/l};
	}
	
	/** dot (scalar) product of two vectors */
	public static float dot(float[] v1, float[] v2){
		return v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2];
	}
	
	/** length of a vector */
	public static float len(float[] v){
		return (float) Math.sqrt(dot(v,v));
	}
	
	/** finds an arbitrary vector perpendicular to v */
	public static float[] findPerp(float[] v){
		//find element of v with smallest magnitude
		float min = v[0]*v[0];
		int i = 0;
		
		float t = v[1]*v[1];
		if(t < min){
			min = t;
			i = 1;
		}
		t = v[2]*v[2];
		if(t < min){
			i = 2;
		}
		
		
		float[][] stdBase = {
				{1f,0f,0f},
				{0f,1f,0f},
				{0f,0f,1f}
		};
		
		// return cross product of v and a vector that has highest magnitude where v has smallest
		return cross(v, stdBase[i]);
	}
	
	/** 
	 * solves linear system of equations: v * x = d, and returns d.
	 * (this is the formula for point of plane with smallest distance to origin)
	 */
	public static float[] solve_VXeqD(float[] v, float d){
		float div = v[0]*v[0] + v[1]*v[1] + v[2]*v[2];
		return new float[]{ (v[0]*d)/div, (v[1]*d)/div, (v[2]*d)/div };
	}
	
	/** string of vector */
	public static String string(float[] v){
		return "(" +v[0] + "," + v[1] + "," + v[2] +")";
	}
	
	/** multiply v with scalar s */
	public static float[] mult(float[] v, float s){
		return new float[]{v[0]*s, v[1]*s, v[2]*s};
	}
	
	/** adds two vectors */
	public static float[] plus(float[] v1, float[] v2){
		return new float[]{v1[0]+v2[0], v1[1]+v2[1], v1[2]+v2[2]};
	}
	
	/** negates a vector */
	public static float[] neg(float[] v){
		return mult(v, -1f);
	}
	
	/** copies int vector */
	public  static int[] cpyVec3(int[] v){
		return new int[] {v[0],v[1],v[2]};
	}
	
	/** copies float vector */
	public static float[] cpyVec3(float[] v){
		return new float[] {v[0],v[1],v[2]};
	}
	
	/** integer vector to float vector */
	public static float[] toF(int[] v){
		return new float[] {v[0],v[1],v[2]};
	}
	
	/** 
	 * float vector to integer vector.
	 * writes directly into vI
	 */
	public static void toI(float[] vF, int[] vI){
		vI[0]= (int) vF[0];
		vI[1]= (int) vF[1];
		vI[2]= (int) vF[2];
	}
	
	public static int[] toI(float[] v){
		int[] vI = new int[3];
		toI(v, vI);
		return vI;
	}
	
}
