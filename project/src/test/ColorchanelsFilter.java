package test;

import java.io.File;

import org.jocl.CL;
import org.jocl.cl_kernel;

import CLDatatypes.CLBufferInt;
import util.CLBoilerplate;
import util.CLInstance;
import util.EasyKernel;
import filters.AbstractFilterCLBuffer;

public class ColorchanelsFilter extends AbstractFilterCLBuffer {
	
	EasyKernel kernel;
	int alpha = 0;
	int red = 0;
	int green = 0;
	int blue=0;
	
	public ColorchanelsFilter(CLInstance clInstance) {
		super(clInstance);
		
		cl_kernel k = CLBoilerplate.getKernel(CLBoilerplate.getProgram(clInstance.device, clInstance.context, new File("oclKernels/colorchanels.cl")), "colorchanels");
		kernel = new EasyKernel(k);
	}

	@Override
	public void apply() {
		this.resetOutput();
		
		if(deviceInput == null){
			deviceInput = new CLBufferInt(hostInput, clInstance);
		}
		
		kernel.setArgumentAt(0, deviceInput.get());
		kernel.setArgumentAt(1, alpha); // alpha
		kernel.setArgumentAt(2, red); // red
		kernel.setArgumentAt(3, green); // green
		kernel.setArgumentAt(4, blue); // blue
		
		int errcode = CL.clEnqueueNDRangeKernel(
				clInstance.queue, 
				kernel.get(), 
				1, null, 
				new long[]{deviceInput.numElements}, 
				null,
				0, null, null);
		CL.clFinish(clInstance.queue);
		
		if(errcode != CL.CL_SUCCESS){
			throw new RuntimeException("Could not enqueue NDRangeKernel. ErrorCode:"+ errcode);
		}
		
		deviceOutput = deviceInput;
		this.resetInput();
	}

	public int getDeltaAlpha() {
		return alpha;
	}

	public void setDeltaAlpha(int alpha) {
		this.alpha = alpha;
	}

	public int getDeltaRed() {
		return red;
	}

	public void setDeltaRed(int red) {
		this.red = red;
	}

	public int getDeltaGreen() {
		return green;
	}

	public void setDeltaGreen(int green) {
		this.green = green;
	}

	public int getDeltaBlue() {
		return blue;
	}

	public void setDeltaBlue(int blue) {
		this.blue = blue;
	}
	
	public void setDeltas(int dAlpha, int dRed, int dGreen, int dBlue){
		this.alpha = dAlpha;
		this.red = dRed;
		this.green = dGreen;
		this.blue = dBlue;
	}

	

}
