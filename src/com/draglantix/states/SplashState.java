package com.draglantix.states;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;

public class SplashState extends GameState {
	
	private float alpha = 1f;
	
	public SplashState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}
	
	public void init() {
		Assets.music.setVolume(0.1f);
		Assets.music.play(Assets.abyssal);
	}

	@Override
	public void tick() {
		
		if(Assets.logoAnim.hasPlayed()) {
			alpha -= 0.005f;
			if(alpha < 0) {
				alpha = 0;	
			}
		}
		
		if(alpha == 0 || Window.getInput().isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			gsm.setState(States.INTRO);
		}
	}

	@Override
	public void render() {
		g.drawMode(g.DRAW_SCREEN);
		g.drawImage(Assets.logoAnim.getTexture(), new Vector2f(0, 0), new Vector2f(128, 128), new Vector2f(0, 0), new Color(255, 255, 255, alpha));
	}

}
