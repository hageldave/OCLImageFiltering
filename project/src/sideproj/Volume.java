package sideproj;

public class Volume {

	float[][][] data;
	final int[] dims = {0,0,0};
	
	public Volume(int w, int h, int d) {
		this.data = new float[w][h][d];
		dims[0] = w; dims[1] = h; dims[2] = d;
	}
	
	public int[] getDims() {
		return VectorUtilities.cpyVec3(dims);
	}
	
	public int getW(){
		return dims[0];
	}
	
	public int getH(){
		return dims[1];
	}
	
	public int getD(){
		return dims[2];
	}
	
	public float getVal(int x, int y, int z){
		return data[x][y][z];
	}
	
	public float getVal(int[] v){
		return getVal(v[0], v[1], v[2]);
	}
	
	public void setVal(int x, int y, int z, float val){
		data[x][y][z] = val;
	}
	
	/** check if point (x,y,z) is in bounds of this volume */
	public boolean inBounds(int x, int y, int z){
		return (x > -1) && (y > -1) && (z > -1) &&
				(x < getW()) && (y < getH()) && (z < getD());
	}
	
	public boolean inBounds(int[] v){
		return inBounds(v[0], v[1], v[2]);
	}
	
	/** initializes volume data */
	public void initData_Shades(){
		int i = 0;
		float total = getW()*getH()*getD();
		for(int x = 0; x < getW(); x++){
			for(int y = 0; y < getH(); y++){
				for(int z = 0; z < getD(); z++){
					setVal(x, y, z, i/total);
					i++;
				}
			}
		}
	}
	
}
