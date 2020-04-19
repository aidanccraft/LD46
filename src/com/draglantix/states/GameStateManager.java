package com.draglantix.states;

import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.main.GSM;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.main.Settings;

public class GameStateManager extends GSM {

	private GameState currentState;
	
	private SplashState splashState;
	private MenuState menuState;
	private IntroState introState;
	private PlayState playState;
	private GameOverState gameOverState;

	@Override
	public void init() {
		super.init();
		Assets.init(g);
	
		splashState = new SplashState(g, this);
		menuState = new MenuState(g, this);
		introState = new IntroState(g, this);
		playState = new PlayState(g, this);
		gameOverState = new GameOverState(g, this);
		
		setState(Settings.START_STATE);
	}

	@Override
	public void update() {
		super.update();
		currentState.tick();
		currentState.render();
		if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			Window.close();
		}
	}

	public void setState(States state) {
		if(currentState != null)
			currentState.stop();
		
		switch(state) {
			case SPLASH:
				currentState = splashState;
				break;
			case MENU:
				menuState.init();
				currentState = menuState;
				break;
			case INTRO:
				currentState = introState;
				break;
			case PLAY:
				playState.init();
				currentState = playState;
				break;
			case GAMEOVER:
				gameOverState.init();
				currentState = gameOverState;
				break;
			default:
				break;
		}
		
		currentState.start();
	}
	
	public Submarine getSub() {
		return playState.getSub();
	}
	
	public void respawn() {
		playState.respawn();
		currentState = playState;
	}
	
	public boolean respawnable() {
		if(playState.respawnable()) {
			return true;
		} else {
			return false;
		}
	}

}