package com.draglantix.entities;

import java.util.ArrayList;
import java.util.List;

import com.draglantix.flare.graphics.Graphics;

public class EntityManager {

	private static List<Entity> entities = new ArrayList<Entity>();

	public static void tick() {
		for (Entity e : entities) {
			if (e != null) {
				e.tick();
			}
		}
	}

	public static void render(Graphics g) {
		for (Entity e : entities) {
			if (e != null) {
				e.render(g);
			}
		}
	}

	public static void addEntity(Entity e) {
		entities.add(e);
	}

}
