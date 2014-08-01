
__kernel void mask(__global int* values, __global const int* mask){
	const size_t i = get_global_id(0);
	
	int b = values[i];
	int a = (b >> 24) & 0xFF;
	const int r = (b >> 16) & 0xFF;
	const int g = (b >> 8) & 0xFF;
	b = b & 0xFF;
	
	const int val = mask[i];
	
	a = ( 	(((val >> 16 & 0xFF) * 3)/10) + 
			(((val >> 8 & 0xFF) * 6)/10) + 
			((val & 0xFF)/10) ); // gray value of mask
	
	values[i] = (a << 24) | (r << 16) | (g << 8) | b;
}
