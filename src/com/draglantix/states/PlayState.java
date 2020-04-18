package com.draglantix.states;

import org.joml.Vector2f;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public class PlayState extends GameState {
	
	private Submarine sub;
	
	public PlayState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}

	public void init() {
		sub = new Submarine(new Vector2f(0, 0), 0.05f);
	}

	@Override
	public void tick() {
		sub.update();
	}

	@Override
	public void render() {
		g.clearColor(new Color(255, 255, 255, 1));
		drawCamera();
	}
	
	private void drawCamera() {
		g.drawMode(g.DRAW_SCREEN);
		g.drawImage(Assets.terrain, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255,255,255,1));
		g.drawImage(Assets.water, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255,255,255, sub.getDistance("UP")));
		g.drawImage(sub.isLights() ? Assets.dark1 : Assets.dark0, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255,255,255, sub.calculateLight()));
		g.drawImage(Assets.lens, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255,255,255,1));
		
		g.drawString(Assets.font, "Depth: " + (int)sub.getDepth() + " m", new Vector2f(0, 72), new Vector2f(6), new Color(255, 255, 255, 1), g.FONT_CENTER);
		g.drawString(Assets.font, "Lights: " + sub.isLights(), new Vector2f(0, 64), new Vector2f(6), new Color(255, 255, 255, 1), g.FONT_CENTER);
	}
}
