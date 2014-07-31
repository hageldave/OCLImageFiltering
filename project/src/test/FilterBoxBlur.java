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

public class FilterBoxBlur extends AbstractFilterCLBuffer {

	EasyKernel hkernel;
	EasyKernel vkernel;
	
	int width = 0;
	int height = 0;
	int radius = 0;
	int numPasses = 1;
	
	public FilterBoxBlur(CLInstance clInstance) {
		setCLInstance(clInstance);
		
		cl_program prog = CLBoilerplate.getProgram(clInstance.context, new File("oclKernels/boxblur.cl"));
		cl_kernel hk = CLBoilerplate.getKernel(prog, "hblur");
		cl_kernel vk = CLBoilerplate.getKernel(prog, "vblur");
		hkernel = new EasyKernel(hk);
		vkernel = new EasyKernel(vk);
	}

	@Override
	public void apply() {
		this.resetOutput();
		
		if(deviceInput == null){
			deviceInput = new CLBufferInt(hostInput, clInstance.context);
		}
		
		deviceOutput = new CLBufferInt(deviceInput.numElements, clInstance.context);
		
		for (int pass = 0; pass < numPasses; pass++) {

			// horizontal blur
			hkernel.setArgumentAt(0, deviceInput.get());
			hkernel.setArgumentAt(1, deviceOutput.get());
			hkernel.setArgumentAt(2, width);
			hkernel.setArgumentAt(3, height);
			hkernel.setArgumentAt(4, radius);

			int errcode = CL.clEnqueueNDRangeKernel(
					clInstance.queue,
					hkernel.get(), 
					2, null, 
					new long[] { width, height }, 
					null,
					0, null, null);
			CL.clFinish(clInstance.queue);

			if (errcode != CL.CL_SUCCESS) {
				throw new RuntimeException(
						"Could not enqueue NDRangeKernel. ErrorCode:" + errcode);
			}

			// vertical blur
			vkernel.setArgumentAt(0, deviceOutput.get());
			vkernel.setArgumentAt(1, deviceInput.get()); // swapped buffers
			vkernel.setArgumentAt(2, width);
			vkernel.setArgumentAt(3, height);
			vkernel.setArgumentAt(4, radius);

			errcode = CL.clEnqueueNDRangeKernel(
					clInstance.queue,
					vkernel.get(), 
					2, null, 
					new long[] { width, height }, 
					null,
					0, null, null);
			CL.clFinish(clInstance.queue);

			if (errcode != CL.CL_SUCCESS) {
				throw new RuntimeException(
						"Could not enqueue NDRangeKernel. ErrorCode:" + errcode);
			}
		}
		
		deviceOutput = deviceInput; // swap buffers again
		
		this.resetInput();
	}
	
	public void setDimensions(int w, int h){
		this.width = w;
		this.height = h;
	}
	
	public Dimension getDimensions(){
		return new Dimension(width, height);
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void setNumPasses(int num){
		this.numPasses = num;
	}
	
	public int getNumPasses() {
		return numPasses;
	}
	
}
