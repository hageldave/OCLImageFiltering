package filters;

import util.CLInstance;
import CLDatatypes.CLImage2D;

public abstract class AbstractFilterCLImage2D implements IFilterCLImage2D {
	
	protected CLInstance clInstance;
	
	protected int[] hostInput = null;
	protected CLImage2D deviceInput = null;
	
	protected int[] hostOutput = null;
	protected CLImage2D deviceOutput = null;

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
	public void setDeviceInputImage(CLImage2D image) {
		this.deviceInput = image;
	}

	@Override
	public CLImage2D getDeviceOutputImage() {
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
		return clInstance;
	}
	

}
