package test;

import java.awt.Dimension;
import java.io.File;

import org.jocl.CL;
import org.jocl.cl_kernel;
import org.jocl.cl_program;

import filters.AbstractFilterCLBuffer;
import util.CLBoilerplate;
import util.CLInstance;
import util.EasyKernel;
import CLDatatypes.CLBufferInt;

public class ResizeFilter extends AbstractFilterCLBuffer {

	EasyKernel kernel;
	
	Dimension outputDimension = null;
	
	public ResizeFilter(CLInstance clInstance) {
		super(clInstance);
		
		cl_program prog = CLBoilerplate.getProgram(clInstance.device, clInstance.context, new File("oclKernels/resize.cl"));
		cl_kernel k = CLBoilerplate.getKernel(prog, "resize");
		kernel = new EasyKernel(k);
	}

	@Override
	public void apply() {
		this.resetOutput();
		
		if(deviceInput == null){
			deviceInput = new CLBufferInt(hostInput, clInstance);
		}
		
		deviceOutput = new CLBufferInt((outputDimension.width * outputDimension.height), clInstance);

		kernel.setArgumentAt(0, this.deviceInput.get());
		kernel.setArgumentAt(1, this.deviceOutput.get());
		kernel.setArgumentAt(2, this.inputDimension.width);
		kernel.setArgumentAt(3, this.inputDimension.height);
		kernel.setArgumentAt(4, this.outputDimension.width);
		kernel.setArgumentAt(5, this.outputDimension.height);

		int errcode = CL.clEnqueueNDRangeKernel(
				clInstance.queue,
				kernel.get(), 
				2, null, 
				new long[] { outputDimension.width, outputDimension.height }, 
				null,
				0, null, null);
		CL.clFinish(clInstance.queue);
		
		if (errcode != CL.CL_SUCCESS) {
			throw new RuntimeException(
					"Could not enqueue NDRangeKernel. ErrorCode:" + errcode);
		}
		
		this.resetInput();
	}
	
	public void setOutputDimension(Dimension outputDimension) {
		this.outputDimension = outputDimension;
	}
	
	@Override
	public Dimension getOutputDimension() {
		return outputDimension;
	}

	
}
