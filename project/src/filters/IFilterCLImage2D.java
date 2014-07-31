package filters;

import util.CLInstance;
import CLDatatypes.CLImage2D;

public interface IFilterCLImage2D extends IFilter {
	
	public void setCLInstance(CLInstance instance);
	
	public CLInstance getCLInstance();
	
	public void setDeviceInputImage(CLImage2D buffer);
	
	public CLImage2D getDeviceOutputImage();
	
	
}
