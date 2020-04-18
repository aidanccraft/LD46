package com.draglantix.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageDecoder {

	public static boolean[][] decode(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	 	int w = img.getWidth();
	    int h = img.getHeight();
	    
	    boolean[][] decode = new boolean[w][h];

	    for(int x = 0; x < w; x++) {
	    	 for(int y = 0; y < h; y++) {
	    		 int red = (img.getRGB(x, y) >> 16) & 0xFF;
	    		 if(red > 0) {
	    			 decode[x][y] = true;
	    		 }else {
	    			 decode[x][y] = false;
	    		 }
	    	 }
	    }
	    
	    return decode;
	}
	
}
