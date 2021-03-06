package CLDatatypes;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_image_desc;
import org.jocl.cl_image_format;
import org.jocl.cl_mem;

import util.CLInstance;

public class CLImage2D {
	private cl_mem memObj = null;
	private boolean filled = false;
	public final int width;
	public final int height;
	public final CLInstance clInstance;
	public final long clFlags;
	public final cl_image_format imageFormat;
	public final cl_image_desc imageDesc;
	
	public CLImage2D(int[] buffer, int width, int height, CLInstance clInstance, long clFlags) {
		this.width = width;
		this.height = height;
		this.clInstance = clInstance;
		this.clFlags = clFlags | CL.CL_MEM_COPY_HOST_PTR;
		
		int[] errCode = new int[1];
		
		imageFormat = new cl_image_format();
        imageFormat.image_channel_order = CL.CL_RGBA;
        imageFormat.image_channel_data_type = CL.CL_UNORM_INT8;
        
        imageDesc = new cl_image_desc();
        imageDesc.image_type = CL.CL_MEM_OBJECT_IMAGE2D;
        imageDesc.image_width = width;
        imageDesc.image_height = height;
        imageDesc.image_row_pitch = width * Sizeof.cl_int;
        imageDesc.image_slice_pitch = imageDesc.image_row_pitch * height;
        
		this.memObj = CL.clCreateImage(clInstance.context, this.clFlags, imageFormat, imageDesc, Pointer.to(buffer), errCode);
		
		if(errCode[0] != CL.CL_SUCCESS){
			throw new RuntimeException("Creating CLImage2D failed. Error code: " + errCode[0]);
		}
		this.filled = true;
	}
	
	public CLImage2D(int[] buffer, int width, int height, CLInstance clInstance) {
		this(buffer, width, height, clInstance, CL.CL_MEM_READ_WRITE);
	}
	
	public CLImage2D(int width, int height, CLInstance clInstance, long clFlags) {
		this.width = width;
		this.height = height;
		this.clInstance = clInstance;
		this.clFlags = clFlags;
		
		int[] errCode = new int[1];
		
		imageFormat = new cl_image_format();
        imageFormat.image_channel_order = CL.CL_RGBA;
        imageFormat.image_channel_data_type = CL.CL_UNORM_INT8;
        
        imageDesc = new cl_image_desc();
        imageDesc.image_type = CL.CL_MEM_OBJECT_IMAGE2D;
        imageDesc.image_width = width;
        imageDesc.image_height = height;
        imageDesc.image_row_pitch = 0;
        imageDesc.image_slice_pitch = 0;
        
		this.memObj = CL.clCreateImage(clInstance.context, this.clFlags, imageFormat, imageDesc, null, errCode);
		
		if(errCode[0] != CL.CL_SUCCESS){
			throw new RuntimeException("Creating CLImage2D failed. Error code: " + errCode[0]);
		}
		this.filled = false;
	}
	
	public CLImage2D(int width, int height, CLInstance clInstance) {
		this(width, height, clInstance, CL.CL_MEM_READ_WRITE);
	}
	
	public cl_mem get(){
		return memObj;
	}
	
	public boolean isFilled() {
		return filled;
	}
	
	public int[] toHostBuffer(){
		int[] buffer = new int[width*height];
		
		toHostBuffer(buffer);
		
		return buffer;
	}
	
	public void toHostBuffer(int[] buffer){
		if(buffer.length != width*height){
			throw new RuntimeException("Cannot copy to host buffer. Not the same number of elements. Provided:" + buffer.length + " Awaited:" + width*height);
		} else {
			int errCode = CL.clEnqueueReadImage(clInstance.queue, memObj, true, new long[]{0,0,0}, new long[]{width,height,1}, imageDesc.image_row_pitch, imageDesc.image_slice_pitch, Pointer.to(buffer), 0, null, null);
			
			if(errCode != CL.CL_SUCCESS){
				throw new RuntimeException("Reading from CLImage2D failed. Error code: " + errCode);
			}
		}
	}
	
	
	public void fill(int[] values){
		if(values.length != width*height){
			throw new RuntimeException("Cannot fill image. Not the same number of elements. Provided:" + values.length + " Awaited:" + (width*height));
		} else {
			int errCode = CL.clEnqueueWriteImage(clInstance.queue, memObj, true, new long[]{0,0,0}, new long[]{width,height,1}, imageDesc.image_row_pitch, imageDesc.image_slice_pitch, Pointer.to(values), 0, null, null);
			
			if(errCode != CL.CL_SUCCESS){
				throw new RuntimeException("Writing to CLImage2D failed. Error code: " + errCode);
			}
			filled = true;
		}
	}
	
	public CLInstance getClInstance() {
		return clInstance;
	}
	
	@Override
	protected void finalize() throws Throwable {
		CL.clReleaseMemObject(this.memObj);
		super.finalize();
	}
	
}
