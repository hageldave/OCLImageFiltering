
__kernel void colorchanels(__global int* values, const int dA, const int dR, const int dG, const int dB) {
	size_t i = get_global_id(0);
	int a = values[i] >> 24 & 0xFF;
	int r = values[i] >> 16 & 0xFF;
	int g = values[i] >> 8 & 0xFF;
	int b = values[i] & 0xFF;
	
	a = a + dA; a = a > 0 ? (a > 255 ? 255 : a) : 0;
	r = r + dR; r = r > 0 ? (r > 255 ? 255 : r) : 0;
	g = g + dG; g = g > 0 ? (g > 255 ? 255 : g) : 0;
	b = b + dB; b = b > 0 ? (b > 255 ? 255 : b) : 0;
	
	values[i] = (a << 24) | (r << 16) | (g << 8) | b;
	values[i] = values[i];
}
