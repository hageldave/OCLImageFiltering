package test;

import java.io.File;

import org.jocl.CL;
import org.jocl.cl_kernel;
import org.jocl.cl_program;

import filters.AbstractFilterCLBuffer;
import util.CLBoilerplate;
import util.CLInstance;
import util.EasyKernel;
import CLDatatypes.CLBufferInt;

public class FilterMask extends AbstractFilterCLBuffer {

	EasyKernel kernel;
	int[] hostMask = null;
	CLBufferInt deviceMask = null;
	
	
	public FilterMask(CLInstance clInstance) {
		setCLInstance(clInstance);
		
		cl_program prog = CLBoilerplate.getProgram(clInstance.context, new File("oclKernels/mask.cl"));
		cl_kernel k = CLBoilerplate.getKernel(prog, "mask");
		kernel = new EasyKernel(k);
	}

	@Override
	public void apply() {
		this.resetOutput();
		
		if(deviceInput == null){
			deviceInput = new CLBufferInt(hostInput, clInstance.context);
		}
		
		if(deviceMask == null){
			deviceMask = new CLBufferInt(hostMask, clInstance.context);
		}
		
		if(deviceInput.numElements != deviceMask.numElements){
			throw new RuntimeException("Cannot apply mask. Number of mask elements does not match number of input buffer elements. Provided:" + deviceMask.numElements + " Awaited:" + deviceInput.numElements);
		}

		kernel.setArgumentAt(0, deviceInput.get());
		kernel.setArgumentAt(1, deviceMask.get());

		int errcode = CL.clEnqueueNDRangeKernel(
				clInstance.queue,
				kernel.get(), 
				1, null, 
				new long[] {deviceInput.numElements}, 
				null,
				0, null, null);
		CL.clFinish(clInstance.queue);
		
		if (errcode != CL.CL_SUCCESS) {
			throw new RuntimeException(
					"Could not enqueue NDRangeKernel. ErrorCode:" + errcode);
		}
		
		deviceOutput = deviceInput;
		this.resetInput();
	}
	
	public void setHostMaskBuffer(int[] mask){
		this.hostMask = mask;
	}
	
	public int[] getHostMaskBuffer() {
		if(hostMask == null && deviceMask != null){
			hostMask = deviceMask.toHostBuffer(clInstance.queue);
		}
		return hostMask;
	}
	
	public void setDeviceMaskBuffer(CLBufferInt mask){
		this.deviceMask = mask;
	}
	
	public CLBufferInt getDeviceMaskBuffer() {
		return deviceMask;
	}

	public void resetMask(){
		this.hostMask = null;
		this.deviceMask = null;
	}
	
}
