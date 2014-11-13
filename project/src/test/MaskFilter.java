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
import CLDatatypes.CLImage2D;

public class MaskFilter extends AbstractFilterCLBuffer {

	EasyKernel kernel;
	int[] hostMask = null;
	Dimension maskDimension = null;
	CLImage2D deviceMask = null;
	
	
	public MaskFilter(CLInstance clInstance) {
		super(clInstance);
		
		cl_program prog = CLBoilerplate.getProgram(clInstance.device, clInstance.context, new File("oclKernels/mask.cl"));
		cl_kernel k = CLBoilerplate.getKernel(prog, "mask");
		kernel = new EasyKernel(k);
	}

	@Override
	public void apply() {
		this.resetOutput();
		
		if(deviceInput == null){
			deviceInput = new CLBufferInt(hostInput, clInstance);
		}
		
		if(deviceMask == null){
			deviceMask = new CLImage2D(hostMask, maskDimension.width, maskDimension.height, clInstance);
		}

		kernel.setArgumentAt(0, deviceInput.get());
		kernel.setArgumentAt(1, deviceMask.get());
		kernel.setArgumentAt(2, inputDimension.width);
		kernel.setArgumentAt(3, inputDimension.height);

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
	
	public void setHostMaskBuffer(int[] mask, Dimension maskDimension){
		this.hostMask = mask;
		this.maskDimension = maskDimension;
	}
	
	public int[] getHostMaskBuffer() {
		if(hostMask == null && deviceMask != null){
			hostMask = deviceMask.toHostBuffer();
		}
		return hostMask;
	}
	
	public void setDeviceMaskImage(CLImage2D mask){
		if(!mask.getClInstance().equals(this.getCLInstance())){
			this.hostMask = mask.toHostBuffer();
			this.deviceMask = null;
		} else {
			this.deviceMask = mask;
		}
		this.maskDimension = new Dimension(mask.width, mask.height);
	}
	
	public CLImage2D getDeviceMaskBuffer() {
		return deviceMask;
	}

	public void resetMask(){
		this.hostMask = null;
		this.deviceMask = null;
		this.maskDimension = null;
	}
	
}
