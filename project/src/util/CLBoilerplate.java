package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.jocl.*;

public class CLBoilerplate {

	private static cl_context context = null;

	private static cl_command_queue queue = null;
	
	private static cl_platform_id platformID = null;
	
	private static cl_device_id deviceID = null;
	
	private static boolean isClInitialized = false;
	
	// ----
	public static CLInstance getCLInstance(){
		if(!isClInitialized()){
			initCL();
		}
		
		return new CLInstance(getContext(), getQueue(), getDeviceID(), getPlatformID());
	}
	
	
	public static cl_context getContext() {
		return context;
	}

	public static cl_command_queue getQueue() {
		return queue;
	}
	
	public static cl_platform_id getPlatformID() {
		return platformID;
	}
	
	public static cl_device_id getDeviceID() {
		return deviceID;
	}
	
	public static boolean isClInitialized() {
		return isClInitialized;
	}


	public static cl_platform_id[] getPlatformIDs(){
		int[] numPlatforms = new int[1];
		CL.clGetPlatformIDs(0, null, numPlatforms);
		
		cl_platform_id[] platformIDs = new cl_platform_id[ numPlatforms[0] ];
		CL.clGetPlatformIDs(numPlatforms[0], platformIDs, numPlatforms);
		return platformIDs;
	}
	
	public static cl_device_id[] getDeviceIDs(cl_platform_id platformID, long deviceType){
		int[] numDevices = new int[1];
		CL.clGetDeviceIDs(platformID, deviceType, 0, null, numDevices);
		
		cl_device_id[] deviceIDs = new cl_device_id[ numDevices[0] ];
		CL.clGetDeviceIDs(platformID, deviceType, numDevices[0], deviceIDs, numDevices);
		return deviceIDs;
	}
	
	public static long getDeviceInfo(cl_device_id deviceID, int infoParameter){
		long[] value = new long[1];
		CL.clGetDeviceInfo(deviceID, infoParameter, Sizeof.cl_long, Pointer.to(value), null);
		
		return value[0];
	}
	
	private static cl_platform_id getPlatformMaxGpuMem(cl_platform_id[] platformIDs){
		cl_platform_id bestPID = null;
		long bestMem = 0;
		for(cl_platform_id pID : platformIDs){
			// if cpu on platform
			if(getDeviceIDs(pID, CL.CL_DEVICE_TYPE_CPU).length > 0){
				cl_device_id[] gpus = getDeviceIDs(pID, CL.CL_DEVICE_TYPE_GPU);
				
				// if gpu on platform
				if(gpus.length > 0){
					for(cl_device_id gpu : gpus){
						long mem = getDeviceInfo(gpu, CL.CL_DEVICE_GLOBAL_MEM_SIZE);
						if(mem > bestMem){
							bestPID = pID;
							bestMem = mem;
						}
					}
				}
			}
		}
		
		return bestPID;
	}
	
	private static cl_device_id getGPUMaxMem(cl_platform_id platformID){
		long bestMem = 0;
		cl_device_id bestGPU = null;
		cl_device_id[] gpus = getDeviceIDs(platformID, CL.CL_DEVICE_TYPE_GPU);
		
		// if gpu on platform
		if(gpus.length > 0){
			for(cl_device_id gpu : gpus){
				long mem = getDeviceInfo(gpu, CL.CL_DEVICE_GLOBAL_MEM_SIZE);
				if(mem > bestMem){
					bestGPU = gpu;
					bestMem = mem;
				}
			}
		}
		
		return bestGPU;
	}
	
	public static cl_context createContext(cl_platform_id pID, cl_device_id dID){
		cl_context_properties props = new cl_context_properties();
		props.addProperty(CL.CL_CONTEXT_PLATFORM, pID);
		
		cl_context context = CL.clCreateContext(props, 1, new cl_device_id[]{dID}, null, null, null);
		return context;
	}
	
	public static cl_command_queue createCommandQueue(cl_context context, cl_device_id deviceID){
		return CL.clCreateCommandQueue(context, deviceID, 0, null);
	}
	
	public static void initCL() {
		cl_platform_id platform = getPlatformMaxGpuMem(getPlatformIDs());
		cl_device_id device = null;
		if(platform != null){
			device = getGPUMaxMem(platform);
		} else {
			// no gpu -> fallback to cpu
			platform = getPlatformIDs()[0];
			device = getDeviceIDs(platform, CL.CL_DEVICE_TYPE_CPU)[0];
			System.err.println("WARNING: FALLBACK TO CPU");
		}
		
		CLBoilerplate.platformID = platform;
		CLBoilerplate.deviceID = device;
		CLBoilerplate.context = createContext(platform, device);
		CLBoilerplate.queue = createCommandQueue(CLBoilerplate.context, device);
		CLBoilerplate.isClInitialized = true;
	}
	
	public static cl_program getProgram(cl_device_id device, cl_context context, String source, String buildOptions){
		
		System.out.println(source);
		
		int[] errcode = new int[1];
		cl_program prog = CL.clCreateProgramWithSource(context, 1, new String[]{source}, null, errcode);
		
		if(errcode[0] != CL.CL_SUCCESS){	
			throw new RuntimeException("Could not create clProgram with Source. Errorcode: " + errcode[0] );
		}
		
		int status = CL.clBuildProgram(prog, 0, null, buildOptions, null, null);
		if(status != CL.CL_SUCCESS){
			
			long logSize[] = new long[1];
            CL.clGetProgramBuildInfo(prog, device, CL.CL_PROGRAM_BUILD_LOG, 0, null, logSize);
           
            byte logData[] = new byte[(int)logSize[0]];
            CL.clGetProgramBuildInfo(prog, device, CL.CL_PROGRAM_BUILD_LOG, logSize[0], Pointer.to(logData), null);
            String buildlog = new String(logData, 0, logData.length-1);
			
			throw new RuntimeException("Could not build clProgram. Errorcode: " + status + "\n" + buildlog);
		}
		return prog;
	}
	
	public static cl_program getProgram(cl_device_id device, cl_context context, String source){
		return getProgram(device, context, source, "");
	}
	
	public static cl_program getProgram(String source, String buildOptions){
		return getProgram(getDeviceID(), getContext(), source, buildOptions);
	}
	
	public static cl_program getProgram(String source){
		return getProgram(source, "");
	}
	
	public static cl_program getProgram(cl_device_id device, cl_context context, File source, String buildOptions){
		try {
			Scanner sc = new Scanner(source);
			StringBuilder sb = new StringBuilder(512);
			while(sc.hasNext()){
				sb.append(sc.nextLine());
				sb.append("\n");
			}
			sc.close();
//			System.out.println(sb.toString());
			return getProgram(device, context, sb.toString(), buildOptions);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static cl_program getProgram(cl_device_id device, cl_context context, File source){
		return getProgram(device, context, source, "");
	}
	
	public static cl_program getProgram(File source, String buildOptions){
		return getProgram(getDeviceID(), getContext(), source, buildOptions);
	}
	
	public static cl_program getProgram(File source){
		return getProgram(getDeviceID(), getContext(), source);
	}
	
	public static cl_kernel getKernel(cl_program program, String kernelName){
		cl_kernel kernel = CL.clCreateKernel(program, kernelName, null);
		return kernel;
	}
	
}
