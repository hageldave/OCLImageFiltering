package filters;

import java.awt.Dimension;
import CLDatatypes.CLBufferInt;
import CLDatatypes.CLImage2D;
import util.CLInstance;

public interface IFilter {
	
	public void setHostInputBuffer(int[] buffer, Dimension dimension);
	
	/** applies the filter (runs the kernel) */
	public void apply();
	
	public int[] getHostOutputBuffer();
	
	public Dimension getOutputDimension();
	
	public Dimension getInputDimension();
	
	public void resetInput();
	
	public void resetOutput();
	
	
	public static interface CLEnabledFilter {
		public CLInstance getCLInstance();
	}
	
	public static interface DeviceBufferInputFilter extends CLEnabledFilter {
		public void setDeviceInputBuffer(CLBufferInt buffer, Dimension dimension);
	}
	
	public static interface DeviceImageInputFilter extends CLEnabledFilter {
		public void setDeviceInputImage(CLImage2D buffer);
	}
	
	public static interface DeviceBufferOutputFilter extends CLEnabledFilter {
		public CLBufferInt getDeviceOutput();
	}
	
	public static interface DeviceImageOutputFilter extends CLEnabledFilter {
		public CLImage2D getDeviceOutput();
	}
	
}
