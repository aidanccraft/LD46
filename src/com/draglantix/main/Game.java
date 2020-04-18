package com.draglantix.main;

import com.draglantix.flare.main.FlareEngine;
import com.draglantix.states.GameStateManager;
import com.draglantix.main.Settings;

public class Game {

	public static void main(String[] args) {
		new FlareEngine(new GameStateManager(), Settings.FPS_CAP, 800, 600, Settings.WINDOW_TITLE, true, false, Settings.DEBUG);
	}
	
	public static void resetSettings() {
		
	}

}
