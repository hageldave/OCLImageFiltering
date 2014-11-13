package test;

import java.io.File;

import org.jocl.CL;
import org.jocl.cl_kernel;

import CLDatatypes.CLBufferInt;
import util.CLBoilerplate;
import util.CLInstance;
import util.EasyKernel;
import filters.AbstractFilterCLBuffer;

public class ContrastFilter extends AbstractFilterCLBuffer {
	
	EasyKernel kernel;
	int threshold = 128;
	float intensity = 0;
	
	public ContrastFilter(CLInstance clInstance) {
		super(clInstance);
		
		cl_kernel k = CLBoilerplate.getKernel(CLBoilerplate.getProgram(clInstance.device, clInstance.context, new File("oclKernels/contrast.cl")), "contrast");
		kernel = new EasyKernel(k);
	}

	@Override
	public void apply() {
		this.resetOutput();
		
		if(deviceInput == null){
			deviceInput = new CLBufferInt(hostInput, clInstance);
		}
		
		kernel.setArgumentAt(0, deviceInput.get());
		kernel.setArgumentAt(1, intensity);
		kernel.setArgumentAt(2, threshold);

		
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

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	public int getThreshold() {
		return threshold;
	}
	
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
	public float getIntensity() {
		return intensity;
	}

}
