package com.draglantix.main;

import org.joml.Vector2f;

import com.draglantix.flare.graphics.Camera;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.textures.Animation;
import com.draglantix.flare.textures.Font;
import com.draglantix.flare.textures.SpriteSheet;
import com.draglantix.flare.textures.Texture;

public class Assets {

	public static Camera camera;
	public static Font font;
	
	public static Texture debug;

	public static SpriteSheet draglantix;
	public static Animation logoAnim;
	
	public static Texture selector;
	
	public static Texture radar;
	public static Texture radarScan;
	
	public static Texture dark0, dark1, lens, terrain, water;
	
	public static void init(Graphics g) {

		camera = new Camera(new Vector2f(0, 0), 0, 0, .07f);
		font = new Font(new SpriteSheet("textures/font.png"), 8, 1);
		g.setCamera(camera);
		Graphics.setScale(4);
		
		debug = new Texture("textures/debug.png");
		
		draglantix = new SpriteSheet("textures/draglantix.png");
		logoAnim = new Animation(3, 3, 64, 20, draglantix, 0, 9, false);
		
		selector = new Texture("textures/selector.png");
		
		radar = new Texture("textures/radar.png");
		radarScan = new Texture("textures/radarScan.png");
		
		dark0 = new Texture("textures/cameras/dark0.png");
		dark1 = new Texture("textures/cameras/dark1.png");
		lens = new Texture("textures/cameras/lens.png");
		terrain = new Texture("textures/cameras/terrain.png");
		water = new Texture("textures/cameras/water.png");
	}
}
