package com.draglantix.states;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;

public class PlayState extends GameState {

	private Submarine sub;
	
	private float scale = 0;
	private boolean radar = false;

	public PlayState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}

	public void init() {
		sub = new Submarine(new Vector2f(0, 0), 0.05f);
	}

	@Override
	public void tick() {
		if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_Q)) {
			radar = !radar;
			scale = 0;
		}
		
		if(radar) {
			if (scale >= 190) {
				scale = 0;
			} else {
				scale += 1;
			}
		} else {
			sub.update();
		}
	}

	@Override
	public void render() {
		g.clearColor(new Color(255, 255, 255, 1));
		if(radar) {
			g.drawImage(Assets.radar.getTextureID(), new Vector2f(0, 0), new Vector2f(200), new Vector2f(0),
					new Color(255, 255, 255, 1));
			g.drawImage(Assets.radarScan.getTextureID(), new Vector2f(0, 0), new Vector2f(scale), new Vector2f(0),
					new Color(255, 255, 255, 1));
		} else {
			drawCamera();
		}
	}

	private void drawCamera() {
		g.drawMode(g.DRAW_SCREEN);
		g.drawImage(Assets.terrain, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255, 255, 255, 1));
		g.drawImage(Assets.water, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
				new Color(255, 255, 255, sub.getDistance("UP")));
		g.drawImage(sub.isLights() ? Assets.dark1 : Assets.dark0, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
				new Color(255, 255, 255, sub.calculateLight()));
		g.drawImage(Assets.lens, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255, 255, 255, 1));

		g.drawString(Assets.font, "Depth: " + (int) sub.getDepth() + " m", new Vector2f(0, 72), new Vector2f(6),
				new Color(255, 255, 255, 1), g.FONT_CENTER);
		g.drawString(Assets.font, "Lights: " + sub.isLights(), new Vector2f(0, 64), new Vector2f(6),
				new Color(255, 255, 255, 1), g.FONT_CENTER);
	}
}
