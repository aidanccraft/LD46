package com.draglantix.states;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.main.Settings;

public class MenuState extends GameState {

	private float alpha = 0f;
	private boolean fadeOut = false;

	private static float offset = Window.getHeight() / 50f;

	private int settingsIndex;
	
	private float theta = 0;

	public enum MenuSection {
		MAIN((1 / 8), new String[] { "Start", "Options", "Quit" }),
		OPTIONS((3 / 8), new String[] { "Debug: " + Settings.DEBUG, "Back" }),
		SETBOOL((3 / 8), new String[] { "True", "False", "Cancel" });

		private float cursorOffset;
		private String[] list;

		private MenuSection(float cursorOffset, String[] list) {
			this.cursorOffset = cursorOffset;
			this.list = list;
		}

		public float getCursorOffset(float screenHeight) {
			return screenHeight * cursorOffset;
		}

		public void resetCursorOffset() {
			MAIN.cursorOffset = (1 / 8);
			OPTIONS.cursorOffset = (3 / 8);
			SETBOOL.cursorOffset = (3 / 8);
		}

		public String[] getList() {
			return list;
		}
	}

	private MenuSection currentSection = MenuSection.MAIN;
	private int currentIndex = 0;

	private float screenWidth;
	private float screenHeight;

	public MenuState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
		this.screenWidth = Window.getWidth() / g.getScale();
		this.screenHeight = Window.getHeight() / g.getScale();
	}

	public void init() {
		rescale();
	}

	private void rescale() {
		this.screenWidth = Window.getWidth() / g.getScale();
		this.screenHeight = Window.getHeight() / g.getScale();
		offset = Window.getHeight() / 50f;
		MenuSection.MAIN.resetCursorOffset();
	}

	@Override
	public void tick() {

		if (Window.hasResized()) {
			rescale();
		}

		fadeMenu();

		if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_SPACE)
				|| Window.getInput().isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
			switch (currentSection) {
			case MAIN:
				if (currentIndex == 0) {
					gsm.setState(States.INTRO);
				} else if (currentIndex == 1) {
					currentSection = MenuSection.OPTIONS;
				} else {
					Window.close();
				}
				break;
			case OPTIONS:
				if (currentIndex == 0) {
					settingsIndex = 0;
					currentSection = MenuSection.SETBOOL;
				} else {
					currentSection = MenuSection.MAIN;
				}
				break;
			case SETBOOL:
				if (currentIndex == 0) {
					switch (settingsIndex) {
					case 0:
						Settings.DEBUG = true;
					default:
						break;
					}
				} else if (currentIndex == 1) {
					switch (settingsIndex) {
					case 0:
						Settings.DEBUG = false;
					default:
						break;
					}
				}
				currentSection = MenuSection.OPTIONS;
				MenuSection.OPTIONS.list = new String[] { "Debug: " + Settings.DEBUG, "Back" };

			default:
				break;
			}

			currentIndex = 0;
		}

		if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_S) || Window.getInput().isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
			currentIndex++;
			if (currentIndex > currentSection.getList().length - 1) {
				currentIndex = 0;
			}
		}

		if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_W) || Window.getInput().isKeyPressed(GLFW.GLFW_KEY_UP)) {
			currentIndex--;
			if (currentIndex < 0) {
				currentIndex = currentSection.getList().length - 1;
			}
		}
	}

	private void fadeMenu() {
		if (!fadeOut) {
			if (alpha < 1) {
				alpha += 0.005f;
			} else {
				alpha = 1f;
			}
		} else {
			if (alpha > 0) {
				alpha -= 0.01f;
			} else {
				alpha = 0f;
				gsm.setState(States.PLAY);
			}
		}
	}

	@Override
	public void render() {
		if (currentSection == MenuSection.MAIN) {
			g.drawString(Assets.font, "LD46",
					new Vector2f((-screenWidth / 2) + (offset * .8f), (screenHeight / 2) - offset),
					new Vector2f(screenHeight / 10), new Color(255, 255, 255, alpha), g.FONT_LEFT);
		}

		for (int i = 0; i < currentSection.getList().length; i++) {
			g.drawString(Assets.font, currentSection.getList()[i],
					new Vector2f((-screenWidth / 2) + offset,
							currentSection.getCursorOffset(screenHeight) - (i * offset)),
					new Vector2f(screenHeight / 30), new Color(255, 255, 255, alpha), g.FONT_LEFT);
		}
		g.drawImage(Assets.selector,
				new Vector2f((-screenWidth / 2) + (offset * .8f),
						currentSection.getCursorOffset(screenHeight) - (currentIndex * offset)),
				new Vector2f(screenHeight / 30), new Vector2f(0, 0), new Color(255, 255, 255, alpha));
		
		theta += 0.01;
		if(theta > Math.PI * 2) {
			theta = 0;
		}
		
		g.drawImage(Assets.submarine, new Vector2f((screenWidth / 4) + (float)(2 * Math.cos(theta + (Math.PI/2))), (float)(4 * Math.sin(theta))), new Vector2f(screenHeight/5), new Vector2f(0), new Color(255, 255, 255, alpha));
	}
}
