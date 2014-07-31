
__kernel void resize(__global int* inBuffer, __global int* outBuffer, const int s_w, const int s_h, const int t_w, const int t_h) {
		const int x = get_global_id(0);
		const int y = get_global_id(1);
		
		const float xr = ((float)(x))/t_w; // x relative to width
		const float yr = ((float)(y))/t_h;
		const float xs = ((float)s_w)*xr; // source x value (is decimal)
		const float ys = ((float)s_h)*yr;
		
		const int x1S = (int)xs; // target x rounded down
		const int x2S = (int)(xs+1); // target x rounded up
		const int y1S = (int)ys;
		const int y2S = (int)(ys+1);
		
		// 4 pixels surrounding the target position (have to check if is right or bottom border)
		const int argb1 = inBuffer[s_w * y1S + x1S];
		int argb2 = 0;
			if(y2S < s_h){
				argb2 = inBuffer[s_w * y2S + x1S];
			} else {
				argb2 = inBuffer[s_w * y1S + x1S];
			}
		int argb3 = 0;
			if(x2S < s_w){
				argb3 = inBuffer[s_w * y1S + x2S];
			} else {
				argb3 = inBuffer[s_w * y1S + x1S];
			}
		int argb4 = 0;
			if(x2S < s_w && y2S < s_h){
				argb4 = inBuffer[s_w * y2S + x2S];
			} else if(x2S < s_w){
				argb4 = inBuffer[s_w * y1S + x2S];
			} else if(y2S < s_h){
				argb4 = inBuffer[s_w * y2S + x1S];
			} else {
				argb4 = inBuffer[s_w * y1S + x1S];
			}
			
		const float factor1 = (1.0f-(xs-((float)x1S))); // horizontal factor for left pixels (when target x is nearer to left pixels, left values are taken more into account
		const float factor2 = (1.0f-(((float)x2S)-xs)); // horizontal factor for right pixels
		const float factor3 = (1.0f-(ys-((float)y1S)));
		const float factor4 = (1.0f-(((float)y2S)-ys));
		
		int avg = 0;
		int argb = 0;
		
		float avg1 = (( ((float)((argb1 >> 24) & 0xFF)) * (factor1)+ ((float)((argb3 >> 24) & 0xFF))*(factor2))*(factor3)); // average alpha of the 2 horizontal top pixels multiplied by vertical factor for top pixels
		float avg2 = (( ((float)((argb2 >> 24) & 0xFF)) * (factor1)+ ((float)((argb4 >> 24) & 0xFF))*(factor2))*(factor4)); // average alpha of the 2 horizontal bottom pixels multiplied by vertical factor for bottom pixels
		avg = (int) (avg1+avg2);
		argb = avg; // alpha
		
		avg1 = (( ((argb1 >> 16) & 0xFF) *(factor1)+ ((argb3 >> 16) & 0xFF)*(factor2))*(factor3)); // average red ..
		avg2 = (( ((argb2 >> 16) & 0xFF)*(factor1)+ ((argb4 >> 16) & 0xFF) *(factor2))*(factor4));
		avg = (int) (avg1+avg2);
		argb = (argb * 256) + (avg); // red
		
		avg1 = (( ((argb1 >> 8) & 0xFF) *(factor1)+ ((argb3 >> 8) & 0xFF)*(factor2))*(factor3)); // average green ..
		avg2 = (( ((argb2 >> 8) & 0xFF)*(factor1)+ ((argb4 >> 8) & 0xFF) *(factor2))*(factor4));
		avg = (int) (avg1+avg2);
		argb = (argb * 256) + (avg); // green
		
		
		avg1 = (( ((argb1) & 0xFF)* (factor1)+ ((argb3) & 0xFF)*(factor2))*(factor3)); // average blue ..
		avg2 = (( ((argb2) & 0xFF)* (factor1)+ ((argb4) & 0xFF)*(factor2))*(factor4));
		avg = (int) (avg1+avg2);
		argb = (argb * 256) + (avg); // blue
		// ----
		
		outBuffer[y*t_w + x] = argb;
}
