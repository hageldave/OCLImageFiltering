package filters;

import util.CLInstance;
import CLDatatypes.CLBufferInt;

public abstract class AbstractFilterCLBuffer implements IFilterCLBuffer {
	
	protected CLInstance clInstance;
	
	protected int[] hostInput = null;
	protected CLBufferInt deviceInput = null;
	
	protected int[] hostOutput = null;
	protected CLBufferInt deviceOutput = null;

	@Override
	public void setHostInputBuffer(int[] buffer) {
		this.hostInput = buffer;
	}

	@Override
	public abstract void apply();

	@Override
	public int[] getHostOutputBuffer() {
		if(hostOutput == null && deviceOutput != null){
			hostOutput = deviceOutput.toHostBuffer(this.clInstance.queue);
		}
		return hostOutput;
	}

	@Override
	public void setDeviceInputBuffer(CLBufferInt buffer) {
		this.deviceInput = buffer;
	}

	@Override
	public CLBufferInt getDeviceOutputBuffer() {
		return deviceOutput;
	}

	@Override
	public void resetInput() {
		this.deviceInput = null;
		this.hostInput = null;
	}

	@Override
	public void resetOutput() {
		this.deviceOutput = null;
		this.hostOutput = null;
	}

	@Override
	public void setCLInstance(CLInstance instance) {
		this.clInstance = instance;
	}
	
	@Override
	public CLInstance getCLInstance() {
		return this.clInstance;
	}
	

}
