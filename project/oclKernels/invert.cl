
const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_REPEAT | CLK_FILTER_NEAREST;

__kernel void invertimg(__read_only image2d_t input, __write_only image2d_t output) {
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
