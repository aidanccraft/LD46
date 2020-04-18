package com.draglantix.states;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;

public class GameOverState extends GameState {

	private String deathMessage;
	private String restartMessage = "Press Space or Enter to return to the menu";

	public GameOverState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}
	
	public void init() {
		Submarine sub = gsm.getSub();
		
		if(sub.getIntegrity() <= 0) {
			deathMessage = "Your submarine exploded!";
		} else if(sub.getOxygen() <= 0) {
			deathMessage = "You ran out of oxygen!";
		} else {
			deathMessage = "Your submarine ran out of power!";
		}
	}

	@Override
	public void tick() {
		if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_SPACE)
				|| Window.getInput().isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
			gsm.setState(States.MENU);
		}
	}

	@Override
	public void render() {
		g.drawMode(g.DRAW_SCREEN);
		g.drawString(Assets.font, deathMessage, new Vector2f(0, Window.getWidth() / 250f), new Vector2f(Window.getWidth() / 250f),
				new Color(255, 255, 255, 1), g.FONT_CENTER);
		g.drawString(Assets.font, restartMessage, new Vector2f(0, -Window.getWidth() / 250f), new Vector2f(Window.getWidth() / 250f),
				new Color(255, 255, 255, 1), g.FONT_CENTER);
	}
}