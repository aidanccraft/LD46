package com.draglantix.states;

import com.draglantix.flare.graphics.Graphics;

public abstract class GameState {

	protected Graphics g;
	protected GameStateManager gsm;

	public GameState(Graphics g, GameStateManager gsm) {
		this.g = g;
		this.gsm = gsm;
	}

	public abstract void tick();

	public abstract void render();

	public void start() {}

	public void stop() {}

}
