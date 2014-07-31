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

public class FilterResize extends AbstractFilterCLBuffer {

	EasyKernel kernel;
	
	int srcWidth = 0;
	int srcHeight = 0;
	int tgtWidth = 0;
	int tgtHeight = 0;
	
	public FilterResize(CLInstance clInstance) {
		setCLInstance(clInstance);
		
		cl_program prog = CLBoilerplate.getProgram(clInstance.context, new File("oclKernels/resize.cl"));
		cl_kernel k = CLBoilerplate.getKernel(prog, "resize");
		kernel = new EasyKernel(k);
	}

	@Override
	public void apply() {
		this.resetOutput();
		
		if(deviceInput == null){
			deviceInput = new CLBufferInt(hostInput, clInstance.context);
		}
		
		deviceOutput = new CLBufferInt((tgtWidth * tgtHeight), clInstance.context);

		kernel.setArgumentAt(0, deviceInput.get());
		kernel.setArgumentAt(1, deviceOutput.get());
		kernel.setArgumentAt(2, srcWidth);
		kernel.setArgumentAt(3, srcHeight);
		kernel.setArgumentAt(4, tgtWidth);
		kernel.setArgumentAt(5, tgtHeight);

		int errcode = CL.clEnqueueNDRangeKernel(
				clInstance.queue,
				kernel.get(), 
				2, null, 
				new long[] { tgtWidth, tgtHeight }, 
				null,
				0, null, null);
		CL.clFinish(clInstance.queue);
		
		if (errcode != CL.CL_SUCCESS) {
			throw new RuntimeException(
					"Could not enqueue NDRangeKernel. ErrorCode:" + errcode);
		}
		
		this.resetInput();
	}
	
	public void setSourceDimensions(int w, int h){
		this.srcWidth = w;
		this.srcHeight = h;
	}
	
	public Dimension getSourceDimensions(){
		return new Dimension(srcWidth, srcHeight);
	}
	
	public void setTargetDimensions(int w, int h){
		this.tgtWidth = w;
		this.tgtHeight = h;
	}
	
	public Dimension getTargetDimensions(){
		return new Dimension(tgtWidth, tgtHeight);
	}

	
}
