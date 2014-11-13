package filters;

import java.awt.Dimension;

import util.CLInstance;
import CLDatatypes.CLBufferInt;

public abstract class AbstractFilterCLBuffer implements IFilterCLBuffer {
	
	protected CLInstance clInstance;
	
	protected int[] hostInput = null;
	protected CLBufferInt deviceInput = null;
	
	protected int[] hostOutput = null;
	protected CLBufferInt deviceOutput = null;
	
	protected Dimension inputDimension = null;
	
	public AbstractFilterCLBuffer(CLInstance clInstance) {
		this.clInstance = clInstance;
	}

	@Override
	public void setHostInputBuffer(int[] buffer, Dimension dimension) {
		this.hostInput = buffer;
		this.inputDimension = dimension;
	}

	@Override
	public abstract void apply();

	@Override
	public int[] getHostOutputBuffer() {
		if(hostOutput == null && deviceOutput != null){
			hostOutput = deviceOutput.toHostBuffer();
		}
		return hostOutput;
	}

	@Override
	public void setDeviceInputBuffer(CLBufferInt buffer, Dimension dimension) {
		if(!buffer.getClInstance().equals(this.clInstance)){
			this.hostInput = buffer.toHostBuffer();
		} else {
			this.deviceInput = buffer;
		}
		this.inputDimension = dimension;
	}

	@Override
	public CLBufferInt getDeviceOutput() {
		return deviceOutput;
	}
	
	@Override
	public Dimension getOutputDimension() {
		return this.inputDimension;
	}
	
	public Dimension getInputDimension() {
		return inputDimension;
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
	public CLInstance getCLInstance() {
		return this.clInstance;
	}
	

}
