package com.draglantix.states;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.utils.ImageDecoder;

public class PlayState extends GameState {

	private Submarine sub;
	
	private float sonarScale = 0;
	private float maxSonarScale = 190;
	
	private boolean[][] map;
	
	private Map<Integer, String> states = new HashMap<Integer, String>();
	private int currentState = 0;

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
		
		//Maybe use this for collision?
		map = ImageDecoder.decode("res/textures/map.png");
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
		System.out.println(sub.getPosition());
//		g.drawImage(Assets.water, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
//				new Color(255, 255, 255, sub.getDistance("UP")));
//		g.drawImage(Assets.lens, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255, 255, 255, 1));
//		g.drawImage(sub.isLights() ? Assets.dark1 : Assets.dark0, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
//				new Color(255, 255, 255, sub.calculateLight()));
	}
}
