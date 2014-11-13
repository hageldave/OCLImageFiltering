const sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_LINEAR;

__kernel void mask(__global int* values, __read_only image2d_t  mask, int width, int height){
	const size_t i = get_global_id(0);
	
	int b = values[i];
	int a = (b >> 24) & 0xFF;
	const int r = (b >> 16) & 0xFF;
	const int g = (b >> 8) & 0xFF;
	b = b & 0xFF;

	float2 coord = (float2) {((i%width)*1.0f) / width, ((i/width)*1.0f) / height};

	float4 maskColor = read_imagef(mask, sampler, coord);
	
	a = (int)(
			((maskColor.x *255 * 3)/10) + 
			((maskColor.y *255 * 6)/10) + 
			((maskColor.z *255)/10) ); // gray value of mask

	
	values[i] = (a << 24) | (r << 16) | (g << 8) | b;
}
