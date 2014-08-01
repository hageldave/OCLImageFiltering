
__kernel void contrast(__global int* values, const float intensity, const int thresh){
	const size_t i = get_global_id(0);
	
	int b = values[i];
	int a = (b >> 24) & 0xFF;
	int r = (b >> 16) & 0xFF;
	int g = (b >> 8) & 0xFF;
	b = b & 0xFF;
	
	int delta = 0;
	
	delta = (int) ((thresh - r)*intensity);
	r = r - delta; r = r > 0 ? (r > 255 ? 255 : r) : 0;
	
	delta = (int) ((thresh - g)*intensity);
	g = g - delta; g = g > 0 ? (g > 255 ? 255 : g) : 0;
	
	delta = (int) ((thresh - b)*intensity);
	b = b - delta; b = b > 0 ? (b > 255 ? 255 : b) : 0;
	
	values[i] = (a << 24) | (r << 16) | (g << 8) | b;
}
