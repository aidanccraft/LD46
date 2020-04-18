package com.draglantix.states;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.EntityManager;
import com.draglantix.entities.Player;
import com.draglantix.entities.Sheep;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.world.World;

public class PlayState extends GameState {

	private World world;

	private float zoom = 0;

	private static Player player;

	public PlayState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}

	public void init() {
		world = new World();
		player = new Player(Assets.playerIdle, new Vector2f(64, 64), new Vector2f(8, 8));
		EntityManager.addEntity(player);
		EntityManager.addEntity(new Sheep(Assets.sheepIdle, new Vector2f(0, 0), new Vector2f(8, 8)));
	}

	@Override
	public void tick() {
		EntityManager.tick();
		updateCamera();
	}

	@Override
	public void render() {
		g.clearColor(new Color(255, 255, 255, 1));
		g.drawMode(g.DRAW_WORLD);
		world.render(g);
		EntityManager.render(g);
	}

	public void updateCamera() {
		Assets.camera.move(player.getPosition());
		zoom = 0;

		if (Window.getInput().isKeyDown(GLFW.GLFW_KEY_Q)) {
			zoom += 0.01;
			Assets.camera.setZoom(Assets.camera.getZoom() + zoom);
		}

		if (Window.getInput().isKeyDown(GLFW.GLFW_KEY_E)) {
			zoom += 0.01;
			Assets.camera.setZoom(Assets.camera.getZoom() - zoom);
		}

	}
}
