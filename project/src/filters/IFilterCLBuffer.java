package filters;

import util.CLInstance;
import CLDatatypes.CLBufferInt;

public interface IFilterCLBuffer extends IFilter {
	
	public void setCLInstance(CLInstance instance);
	
	public CLInstance getCLInstance();
	
	public void setDeviceInputBuffer(CLBufferInt buffer);
	
	public CLBufferInt getDeviceOutputBuffer();
	
}
