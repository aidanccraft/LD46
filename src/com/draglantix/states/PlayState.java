package com.draglantix.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.utils.ImageDecoder;
import com.draglantix.utils.Quad;
import com.draglantix.utils.QuadTree;

public class PlayState extends GameState {

	private Submarine sub;
	
	private float sonarScale = 0;
	private float maxSonarScale = 190;
	
	private boolean[][] map;
	
	private Map<Integer, String> states = new HashMap<Integer, String>();
	private int currentState = 0;
	
	public static List<AABB> bounds = new ArrayList<AABB>();

	private QuadTree qt;
	
	public PlayState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}

	public void init() {
		sub = new Submarine(new Vector2f(50, -10), 0.05f);
		states.put(0, "CAMERA DOWN");
		states.put(1, "CAMERA UP");
		states.put(2, "CAMERA LEFT");
		states.put(3, "CAMERA RIGHT");
		states.put(4, "SONAR");
		
		map = ImageDecoder.decode("res/textures/map.png");
		
		qt = new QuadTree(new Quad(new Vector2f(Assets.map.getWidth()/2, -Assets.map.getWidth()/2),
				new Vector2f(Assets.map.getWidth()/2, Assets.map.getWidth()/2)), 5);
		
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[x].length; y++) {
				if(map[x][y])
					qt.insert(new AABB(new Vector2f(x, -y), new Vector2f(1), false));
			}
		}
		
	}

	@Override
	public void tick() {
		handleSubstates();
		
		if(sonarScale == 0) {
			Assets.submarineSFX.play(Assets.sonarPing);
		}
		
		sonarScale ++;
		if(sonarScale > maxSonarScale) {
			sonarScale = 0;
		}
		
		sub.update();
		
		PlayState.bounds = qt.query(new Quad(new Vector2f(sub.getPosition()), new Vector2f(5)));
	}

	@Override
	public void render() {
		g.clearColor(new Color(255, 255, 255, 1));
		if(currentState < 4) {
			drawCamera();
		}else {
			drawSonar();
		}
		drawStats();
		
		//qt.render(g);
		
		for(AABB b : bounds) {
			g.drawImage(Assets.blank, b.getCenter().sub(sub.getPosition(), new Vector2f()).mul(4), b.getScale().mul(4, new Vector2f()), new Vector2f(0, 0), new Color(255, 255, 255, 1));
		}
		
		g.drawImage(Assets.blank, sub.bounds.getCenter().sub(sub.getPosition(), new Vector2f()).mul(4), sub.bounds.getScale().mul(4, new Vector2f()), new Vector2f(0, 0), new Color(255, 255, 255, 1));
	}
	
	private void handleSubstates() {
		if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
			currentState ++;
			if(currentState > states.size() - 1) {
				currentState = 0;
			}
		}
		if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
			currentState --;
			if(currentState < 0) {
				currentState = states.size() - 1;
			}
		}
	}
	
	private void drawStats() {
		g.drawString(Assets.font, states.get(currentState), new Vector2f(0, 72), new Vector2f(6),
				new Color(255, 255, 255, 1), g.FONT_CENTER);
		g.drawString(Assets.font, "Depth: " + (int) sub.getDepth() + " m", new Vector2f(0, 64), new Vector2f(6),
				new Color(255, 255, 255, 1), g.FONT_CENTER);
		g.drawString(Assets.font, "Lights: " + sub.isLights(), new Vector2f(0, 56), new Vector2f(6),
				new Color(255, 255, 255, 1), g.FONT_CENTER);
	}
	
	private void drawSonar() {
		g.drawImage(Assets.sonarDot, new Vector2f(0, 0), new Vector2f(2), new Vector2f(0), new Color(128, 160, 128, 1));
		g.drawImage(Assets.sonarRing, new Vector2f(0, 0), new Vector2f(sonarScale), new Vector2f(0),new Color(128, 160, 128, 1 - (sonarScale/maxSonarScale)));
	}

	private void drawCamera() { //Add small details in bubbles for each direction
		g.drawMode(g.DRAW_SCREEN);
		g.drawTerrain(Assets.terrain, Assets.map, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255, 255, 255, 1), sub.getPosition(), currentState);
		//System.out.println(sub.getPosition());
//		g.drawImage(Assets.water, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
//				new Color(255, 255, 255, sub.getDistance("UP")));
//		g.drawImage(Assets.lens, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255, 255, 255, 1));
//		g.drawImage(sub.isLights() ? Assets.dark1 : Assets.dark0, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
//				new Color(255, 255, 255, sub.calculateLight()));
	}
}
