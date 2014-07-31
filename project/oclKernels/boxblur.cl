
__kernel void hblur(__global const int* inValues, __global int* outValues, const uint width, const uint height, const int radius){
	const int x = get_global_id(0);
	const int y = get_global_id(1);
	const uint i = y*width + x;
	
	int a = inValues[i] >> 24 & 0xFF;
	int r = inValues[i] >> 16 & 0xFF;
	int g = inValues[i] >> 8 & 0xFF;
	int b = inValues[i] & 0xFF;
	
	// horizontal blur
	
	int count = 1;
	for(int j = x - radius; j <= x + radius; j++){
		if(-1 < j && j < width){
			int idx =(size_t) (y*width + j);
			
			a = a + (inValues[idx] >> 24 & 0xFF);
			r = r + (inValues[idx] >> 16 & 0xFF);
			g = g + (inValues[idx] >> 8 & 0xFF);
			b = b + (inValues[idx] & 0xFF);
			
			count = count + 1;
		}
	}
		
	a = a/count;
	r = r/count;
	g = g/count;
	b = b/count;
	
	outValues[i] = (a << 24) | (r << 16) | (g << 8) | b;
}

__kernel void vblur(__global const int* inValues, __global int* outValues, const uint width, const uint height, const int radius){
	const int x = get_global_id(0);
	const int y = get_global_id(1);
	const uint i = y*width + x;
	
	int a = inValues[i] >> 24 & 0xFF;
	int r = inValues[i] >> 16 & 0xFF;
	int g = inValues[i] >> 8 & 0xFF;
	int b = inValues[i] & 0xFF;
	
	// vertical blur
	
	int count = 1;
	for(int j = y - radius; j <= y + radius; j++){
		if(-1 < j && j < height){
			int idx = (size_t)(j*width + x);
			
			a = a + (inValues[idx] >> 24 & 0xFF);
			r = r + (inValues[idx] >> 16 & 0xFF);
			g = g + (inValues[idx] >> 8 & 0xFF);
			b = b + (inValues[idx] & 0xFF);
			
			count = count + 1;
		}
	}
		
	a = a/count;
	r = r/count;
	g = g/count;
	b = b/count;
	
	outValues[i] = (a << 24) | (r << 16) | (g << 8) | b;
}
