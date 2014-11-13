package sideproj;

import static sideproj.VectorUtilities.*;

public class CuttingPlane {
	
	final float[] normal = 	{ 1, 0, 0 };
	float dist = 0;
	
	float[] xVec = 			{ 0, 1, 0 };
	float[] yVec = 			{ 0, 0, 1 };
	float[] origin = 		{ 0, 0, 0 };

	
	public float[] getNormal() {
		// return copy of normal
		return cpyVec3(normal);
	}

	public void setNormal(float x, float y, float z) {
		// make vector from values
		float[] t = { x, y, z };
		// make unit vector (lentgth =1)
		t = unit(t);
		// copy values to this normal vector
		normal[0] = t[0];
		normal[1] = t[1];
		normal[2] = t[2];
	
		// set up vectors for x and y axis
		setupAxis();
		// set up origin (solve normal * x = distance -> origin := x)
		this.origin = solve_VXeqD(normal, dist);
	}

	public float getDist() {
		return dist;
	}

	public void setDist(float dist) {
		this.dist = dist;
		// set up origin (solve normal * x = distance -> origin := x)
		this.origin = solve_VXeqD(normal, dist);
	}

	public float[] getOrigin() {
		// return copy of origin
		return cpyVec3(origin);
	}

	public float[] getxVec() {
		// return copy of xAxis vector
		return cpyVec3(xVec);
	}

	public float[] getyVec() {
		// return copy of yAxis vector
		return cpyVec3(yVec);
	}

	public float[] planePoint2VolumePoint(int x, int y){
		// volume point = origin + (x* xVector) + (y*yVector)
		return plus(origin, plus(mult(xVec, x), mult(yVec, y)));
	}

	private void setupAxis() {
		// find any vector that is perpendicular to normal and make it a unit vector
		xVec = unit(findPerp(normal));
		// cross product of xVec and normal yields third perpendicular vector
		yVec = unit(cross(normal, xVec));
	}
	

}
