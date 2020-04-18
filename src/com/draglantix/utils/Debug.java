package com.draglantix.utils;

import org.joml.Vector2f;

import com.draglantix.flare.collision.Polygon;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public class Debug {

	public static void renderBounds(Polygon bounds, Graphics g) {
		g.drawImage(Assets.debug, bounds.getPosition(), new Vector2f(bounds.getWidth(), bounds.getHeight()), new Vector2f(0, 0), new Color(255, 255, 255, 1));
	}
	
}
