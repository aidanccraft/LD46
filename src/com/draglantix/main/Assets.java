package com.draglantix.main;

import org.joml.Vector2f;

import com.draglantix.flare.audio.AudioMaster;
import com.draglantix.flare.audio.Source;
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
	
	public static Texture panel, screen;
	
	public static Texture sonarRing;
	public static Texture sonarDot;
	
	public static SpriteSheet bubbles;
	public static Animation bubbleAnim;
	
	public static SpriteSheet bubblesUp;
	public static Animation bubbleUpAnim;
	
	public static SpriteSheet bubblesDown;
	public static Animation bubbleDownAnim;
	
	public static Texture dark0, dark1, lens, terrain, water, map;
	
	public static Texture blank;
	
	public static Source submarineSFX;
	
	public static int sonarPing;
	
	public static void init(Graphics g) {

		camera = new Camera(new Vector2f(0, 0), 0, 0, .07f);
		font = new Font(new SpriteSheet("textures/font.png"), 8, 1);
		g.setCamera(camera);
		g.setScale(4);
		
		debug = new Texture("textures/debug.png");
		
		draglantix = new SpriteSheet("textures/draglantix.png");
		logoAnim = new Animation(3, 3, 64, 20, draglantix, 0, 9, false);
		
		selector = new Texture("textures/selector.png");
		
		panel = new Texture("textures/panel.png");
		screen = new Texture("textures/screen.png");
		
		sonarRing = new Texture("textures/sonar/sonarring.png");
		sonarDot = new Texture("textures/sonar/sonardot.png");
		
		dark0 = new Texture("textures/cameras/dark0.png");
		dark1 = new Texture("textures/cameras/dark1.png");
		lens = new Texture("textures/cameras/lens.png");
		terrain = new Texture("textures/cameras/terrain.png");
		water = new Texture("textures/cameras/water.png");
		map = new Texture("textures/map.png");
		
		bubbles = new SpriteSheet("textures/cameras/bubbles.png");
		bubbleAnim = new Animation(4, 4, 32, 15, bubbles, 0, 11, true);
		
		bubblesUp = new SpriteSheet("textures/cameras/bubblesUp.png");
		bubbleUpAnim = new Animation(3, 3, 32, 15, bubblesUp, 0, 8, true);
		
		bubblesDown = new SpriteSheet("textures/cameras/bubblesDown.png");
		bubbleDownAnim = new Animation(3, 3, 32, 15, bubblesDown, 0, 8, true);
		
		blank = new Texture("textures/blank.png");
		
		submarineSFX = new Source(1.5f, 1000, 0);
		submarineSFX.setPosition(new Vector2f(0, 0));
		
		sonarPing = AudioMaster.loadSound("sfx/sonarPing.wav");
	}
}
