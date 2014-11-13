package CLDatatypes;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_mem;

import util.CLInstance;

public class CLBufferInt {
	
	private cl_mem memObj = null;
	private boolean filled = false;
	public final int numElements;
	public final long size;
	public final CLInstance clInstance;
	public final long clFlags;
	
	public CLBufferInt(int[] array, CLInstance clInstance) {
		this(array, clInstance, CL.CL_MEM_READ_WRITE);
	}
	
	/**
	 * 
	 * @param array data to be 
	 * @param context used to create the buffer
	 * @param clInstance with which the buffer is to be created
	 * @throws RuntimeException when creating buffer fails;
	 */
	public CLBufferInt(int[] array, CLInstance clInstance, long clFlags) {
		this.numElements = array.length;
		this.size = numElements * Sizeof.cl_int;
		this.clInstance = clInstance;
		this.clFlags = clFlags | CL.CL_MEM_COPY_HOST_PTR;
		
		int[] errCode = new int[1];
		this.memObj = CL.clCreateBuffer(clInstance.context, this.clFlags, size, Pointer.to(array), errCode);
		if(errCode[0] != CL.CL_SUCCESS){
			throw new RuntimeException("Creating CLBuffer failed. Error code: " + errCode[0]);
		}
		
		this.filled = true;
	}
	
	public CLBufferInt(int numElements, CLInstance clInstance, long clFlags){
		this.numElements = numElements;
		this.size = numElements * Sizeof.cl_int;
		this.clInstance = clInstance;
		this.clFlags = clFlags;
		
		int[] errCode = new int[1];
		this.memObj = CL.clCreateBuffer(clInstance.context, this.clFlags, size, null, errCode);
		if(errCode[0] != CL.CL_SUCCESS){
			throw new RuntimeException("Creating CLBuffer failed. Error code: " + errCode[0]);
		}
		this.filled = false;
	}
	
	public CLBufferInt(int numElements, CLInstance clInstance){
		this(numElements, clInstance, CL.CL_MEM_READ_WRITE);
	}
	
	
	public cl_mem get(){
		return memObj;
	}
	
	public boolean isFilled() {
		return filled;
	}
	
	public int[] toHostBuffer(){
		int[] buffer = new int[numElements];
		toHostBuffer(buffer);
		return buffer;
	}
	
	
	public void toHostBuffer(int[] buffer){
		if(buffer.length != this.numElements){
			throw new RuntimeException("Cannot copy to host buffer. Not the same number of elements. Provided:" + buffer.length + " Awaited:" + this.numElements);
		} else {
			int errCode = CL.clEnqueueReadBuffer(clInstance.queue, memObj, true, 0, size, Pointer.to(buffer), 0, null, null);
			
			if(errCode != CL.CL_SUCCESS){
				throw new RuntimeException("Reading from CLBuffer failed. Error code: " + errCode);
			}
		}
	}
	
	public void fill(int[] values){
		if(values.length != this.numElements){
			throw new RuntimeException("Cannot fill buffer. Not the same number of elements. Provided:" + values.length + " Awaited:" + this.numElements);
		} else {
			int errCode = CL.clEnqueueWriteBuffer(clInstance.queue, memObj, true, 0, size, Pointer.to(values), 0, null, null);
			
			if(errCode != CL.CL_SUCCESS){
				throw new RuntimeException("Writing to CLBuffer failed. Error code: " + errCode);
			}
			filled = true;
		}
	}
	
	public CLInstance getClInstance() {
		return clInstance;
	}
	
}
