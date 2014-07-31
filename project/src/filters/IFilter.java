package filters;

public interface IFilter {
	
	public void setHostInputBuffer(int[] buffer);
	
	/** applies the filter (runs the kernel) */
	public void apply();
	
	public int[] getHostOutputBuffer();
	
	
	public void resetInput();
	
	public void resetOutput();
	
}
