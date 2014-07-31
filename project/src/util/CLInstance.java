package util;

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

public class CLInstance {

	public final cl_context context;
	
	public final cl_command_queue queue;
	
	public final cl_device_id device;
	
	public final cl_platform_id platform;

	public CLInstance(cl_context context, cl_command_queue queue,
			cl_device_id device, cl_platform_id platform) {
		this.context = context;
		this.queue = queue;
		this.device = device;
		this.platform = platform;
	}
	
	
	
	
}
