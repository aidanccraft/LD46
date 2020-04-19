package com.draglantix.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageDecoder {

	public static int[][] decode(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	 	int w = img.getWidth();
	    int h = img.getHeight();
	    
	    int[][] decode = new int[w][h];

	    for(int x = 0; x < w; x++) {
	    	 for(int y = 0; y < h; y++) {
	    		 int red = (img.getRGB(x, y) >> 16) & 0xFF;
	    		 int green = (img.getRGB(x, y) >> 8) & 0xFF;
	    		 int blue = (img.getRGB(x, y) >> 0) & 0xFF;
	    		 if(red == 255 && green == 255 && blue == 255) { //Wall
	    			 decode[x][y] = 0;
	    		 }else if (red == 255 && green == 255 && blue == 0){ //Shallows
	    			 decode[x][y] = 1;
	    		 }else if (red == 255 && green == 0 && blue == 0){ //Shallow Cave
	    			 decode[x][y] = 2;
	    		 }else if (red == 0 && green == 0 && blue == 255){ //Deep Cave
	    			 decode[x][y] = 3;
	    		 }else if (red == 255 && green == 0 && blue == 255){ //Open Ocean
	    			 decode[x][y] = 4;
	    		 }else { //Abyss
	    			 decode[x][y] = 5;
	    		 }
	    	 }
	    }
	    
	    return decode;
	}
	
}
