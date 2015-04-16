package test;

import org.jocl.CL;
import org.jocl.cl_kernel;

import CLDatatypes.CLBufferInt;
import util.CLBoilerplate;
import util.CLInstance;
import util.EasyKernel;
import filters.AbstractFilterCLBuffer;

public class GrayscaleFilter extends AbstractFilterCLBuffer {
	
	EasyKernel kernel;
	
	public GrayscaleFilter(CLInstance clInstance) {
		super(clInstance);
		
		cl_kernel k = CLBoilerplate.getKernel(CLBoilerplate.getProgram(clInstance.device, clInstance.context, getKernelCode()), "grayscale");
		kernel = new EasyKernel(k);
	}

	@Override
	public void apply() {
		this.resetOutput();
		
		if(deviceInput == null){
			deviceInput = new CLBufferInt(hostInput, clInstance);
		}
		
		kernel.setArgumentAt(0, deviceInput.get());
		
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
	
	
	private final String getKernelCode(){
		return
			"__kernel void grayscale(__global int* values) {"+
				"size_t i = get_global_id(0);"+
				"int a = values[i] >> 24 & 0xFF;"+
				"int r = values[i] >> 16 & 0xFF;"+
				"int g = values[i] >> 8 & 0xFF;"+
				"int b = values[i] & 0xFF;"+
				""+
				"int grey = ( ((r * 3)/10) + ((g * 6)/10) + (b/10) );"+
				""+
				"r = grey;"+
				"g = grey;"+
				"b = grey;"+
				""+
				"values[i] = (a << 24) | (r << 16) | (g << 8) | b;"+
				"values[i] = values[i];"+
			"}";
	}

	

}
