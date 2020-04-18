package com.draglantix.states;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;

public class IntroState extends GameState {
	
	private float alpha = 0f;
	private boolean fadeIn = true;
	
	private String message = "Can you survive the murky depths...";

	public IntroState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}

	@Override
	public void tick() {
		
		if(fadeIn) {
			alpha += 0.005f;
			if(alpha > 1) {
				alpha = 1;
				fadeIn = false;
			}
		}else {
			alpha -= 0.005f;
			if(alpha < 0) {
				alpha = 0;
				gsm.setState(States.PLAY);
			}
		}
		
		if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			gsm.setState(States.PLAY);
		}
	}

	@Override
	public void render() {
		g.drawMode(g.DRAW_SCREEN);
		g.drawString(Assets.font, message, new Vector2f(0, 0), new Vector2f(Window.getWidth()/250f), new Color(255, 255, 255, alpha), g.FONT_CENTER);
	}

}
