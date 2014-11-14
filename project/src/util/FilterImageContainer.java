package util;

import java.awt.Dimension;

import filters.IFilter;
import CLDatatypes.CLBufferInt;
import CLDatatypes.CLImage2D;

public class FilterImageContainer {

	final int[] hostBuffer;
	final CLBufferInt deviceBuffer;
	final CLImage2D deviceImage;
	final Dimension dimension;
	
	public FilterImageContainer(int[] hostBuffer, CLBufferInt deviceBuffer, CLImage2D deviceImage, Dimension dimension) {
		if(hostBuffer == null && deviceBuffer == null && deviceImage == null){
			throw new RuntimeException(
					"Cannot create FilterImageContainer with null arguments. "
					+ "Need at least one not null argument");
		}
		this.hostBuffer = hostBuffer;
		this.deviceBuffer = deviceBuffer;
		this.deviceImage = deviceImage;
		this.dimension = dimension;
	}
	
	public FilterImageContainer(int[] hostBuffer, Dimension dimension) {
		this(hostBuffer,null,null, dimension);
	}

	public FilterImageContainer(CLBufferInt deviceBuffer, Dimension dimension) {
		this(null, deviceBuffer, null, dimension);
	}

	public FilterImageContainer(CLImage2D deviceImage) {
		this(null,null,deviceImage, new Dimension(deviceImage.width, deviceImage.height));
	}

	public FilterImageContainer apply(IFilter filter){
		FilterImageContainer result;
		if(filter instanceof IFilter.DeviceBufferInputFilter){
			if(this.deviceBuffer != null){
				((IFilter.DeviceBufferInputFilter) filter).setDeviceInputBuffer(deviceBuffer, this.dimension);
			} else {
				filter.setHostInputBuffer(makeHostBuffer(null), this.dimension);
			}
			
		} else if(filter instanceof IFilter.DeviceImageInputFilter){
			if(this.deviceImage != null){
				((IFilter.DeviceImageInputFilter) filter).setDeviceInputImage(deviceImage);
			} else {
				filter.setHostInputBuffer(makeHostBuffer(null), this.dimension);
			}
			
		} else {
			filter.setHostInputBuffer(makeHostBuffer(null), this.dimension);
		}
		
		filter.apply();
		
		if(filter instanceof IFilter.DeviceBufferOutputFilter){
			result = new FilterImageContainer(((IFilter.DeviceBufferOutputFilter) filter).getDeviceOutput(), filter.getOutputDimension());
		} else if(filter instanceof IFilter.DeviceImageOutputFilter){
			result = new FilterImageContainer(((IFilter.DeviceImageOutputFilter) filter).getDeviceOutput());
		} else {
			result = new FilterImageContainer(filter.getHostOutputBuffer(), filter.getOutputDimension());
		}
		
		return result;
	}

	public int[] getHostBuffer() {
		return hostBuffer;
	}

	public CLBufferInt getDeviceBuffer() {
		return deviceBuffer;
	}

	public CLImage2D getDeviceImage() {
		return deviceImage;
	}
	
	private int[] makeHostBuffer(int[] target) {
		if(target != null){
			if(hostBuffer != null){
				for(int i = 0; i < hostBuffer.length && i < target.length; i++)
					target[i] = hostBuffer[i];
			} else if(deviceBuffer != null){
				deviceBuffer.toHostBuffer(target);
			} else {
				deviceImage.toHostBuffer(target);
			}
			return target;
		} else {
			if(hostBuffer != null){
				return hostBuffer;
			} else if(deviceBuffer != null){
				return deviceBuffer.toHostBuffer();
			} else {
				return deviceImage.toHostBuffer();
			}
		}
	}
	
	public void copyToHostBuffer(int[] target){
		if(target != null && target != hostBuffer){
			makeHostBuffer(target);
		}
	}
	
	public Dimension getDimension() {
		return dimension;
	}
	
}
