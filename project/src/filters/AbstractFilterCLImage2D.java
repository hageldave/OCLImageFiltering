package filters;

import java.awt.Dimension;

import util.CLInstance;
import CLDatatypes.CLImage2D;

public abstract class AbstractFilterCLImage2D implements IFilterCLImage2D {
	
	protected CLInstance clInstance;
	
	protected int[] hostInput = null;
	protected CLImage2D deviceInput = null;
	
	protected int[] hostOutput = null;
	protected CLImage2D deviceOutput = null;
	
	protected Dimension inputDimension = null;
	
	
	public AbstractFilterCLImage2D(CLInstance clInstance) {
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
	public void setDeviceInputImage(CLImage2D image) {
		if(!image.getClInstance().equals(this.clInstance)){
			this.hostInput = image.toHostBuffer();
		} else {
			this.deviceInput = image;
		}
		this.inputDimension = new Dimension(image.width, image.height);
	}

	@Override
	public CLImage2D getDeviceOutput() {
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
	public CLInstance getCLInstance() {
		return clInstance;
	}
	
	@Override
	public Dimension getOutputDimension() {
		return this.inputDimension;
	}
	
	public Dimension getInputDimension() {
		return inputDimension;
	}

}
