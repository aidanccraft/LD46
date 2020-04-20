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
	
	private int msgIndex = 0;
	
	private String[] messages = {"Our Deep Water Marine Life Research Bases have stopped responding!",
			"Aquatical Life Corporations has hired you to discover the communication problem.", 
			"Unfortunately, the only sub we have the funds for has... Uhhh... No Problems!",
			"Anyways, get down there and keep the company alive!",
			"Oh, and I suppose keep yourself alive! We wouldn't want any more lawsuits now would we!",
			"Use AWSD to move. Use Left and Right Arrows to switch view. Press and hold E in a Window to shine light."};

	public IntroState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}

	@Override
	public void tick() {
		
		if(fadeIn) {
			alpha += 0.005f;
			if(alpha > 1) {
				alpha = 1;
			}
		}else {
			alpha -= 0.05f;
			if(alpha < 0) {
				alpha = 0;
				if(msgIndex != messages.length - 1) {
					msgIndex ++;
					fadeIn = true;
				}else {
					gsm.setState(States.PLAY);
				}
			}
		}
		
		if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			fadeIn = false;
		}
	}

	@Override
	public void render() {
		g.drawMode(g.DRAW_SCREEN);
		g.drawString(Assets.font, messages[msgIndex], new Vector2f(0, 50), new Vector2f(Window.getWidth()/250f), new Color(255, 255, 255, alpha), Window.getWidth()/5f, g.FONT_CENTER);
		g.drawString(Assets.font, "Press Space", new Vector2f(0, -20), new Vector2f(Window.getWidth()/300f), new Color(255, 255, 255, alpha), g.FONT_CENTER);
	}

}
