package util;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;

public class EasyKernel {

	protected cl_kernel kernel = null;
	
	public EasyKernel(cl_kernel kernel) {
		this.kernel = kernel;
	}
	
	public cl_kernel get() {
		return kernel;
	}
	
	public void setArgumentAt(int index, long argSize, Pointer pointer){
		int errCode = CL.clSetKernelArg(kernel, index, argSize, pointer);
		
		if(errCode != CL.CL_SUCCESS){
			throw new RuntimeException("Could not set kernel argument:" + pointer + " at " + index + " Errorcode: " + errCode);
		}
	}
	
	public void setArgumentAt(int index, int value){
		setArgumentAt(index, Sizeof.cl_int, Pointer.to(new int[]{value}));
	}
	
	public void setArgumentAt(int index, long value){
		setArgumentAt(index, Sizeof.cl_long, Pointer.to(new long[]{value}));
	}
	
	public void setArgumentAt(int index, float value){
		setArgumentAt(index, Sizeof.cl_float, Pointer.to(new float[]{value}));
	}
	
	public void setArgumentAt(int index, double value){
		setArgumentAt(index, Sizeof.cl_double, Pointer.to(new double[]{value}));
	}
	
	public void setArgumentAt(int index, cl_mem buffer){
		setArgumentAt(index, Sizeof.cl_mem, Pointer.to(buffer));
	}
	
}
