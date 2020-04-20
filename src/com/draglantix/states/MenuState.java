package com.draglantix.states;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;

public class MenuState extends GameState {

	private float alpha = 0f;
	private boolean fadeOut = false;

	private static float offset = Window.getHeight() / 50f;

	private float theta = 0;

	public enum MenuSection {
		MAIN((1 / 8), new String[] { "Start", "Quit" });

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
				} else {
					Window.close();
				}
				break;
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

			g.drawImage(Assets.title, new Vector2f(0, screenHeight / 4),
					new Vector2f(screenHeight / 1, screenHeight / 2), new Vector2f(0), new Color(255, 255, 255, alpha));
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
		if (theta > Math.PI * 2) {
			theta = 0;
		}

		g.drawImage(Assets.submarine,
				new Vector2f((screenWidth / 4) + (float) (2 * Math.cos(theta + (Math.PI / 2))),
						(-screenHeight / 8) + (float) (4 * Math.sin(theta))),
				new Vector2f(screenHeight / 5), new Vector2f(0), new Color(255, 255, 255, alpha));
	}
}
