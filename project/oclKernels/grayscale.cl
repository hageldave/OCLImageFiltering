
__kernel void grayscale(__global int* values) {
	size_t i = get_global_id(0);
	int a = values[i] >> 24 & 0xFF;
	int r = values[i] >> 16 & 0xFF;
	int g = values[i] >> 8 & 0xFF;
	int b = values[i] & 0xFF;
	
	int grey = ( ((r * 3)/10) + ((g * 6)/10) + (b/10) );
	
	r = grey;
	g = grey;
	b = grey;
	
	values[i] = (a << 24) | (r << 16) | (g << 8) | b;
	values[i] = values[i];
}
