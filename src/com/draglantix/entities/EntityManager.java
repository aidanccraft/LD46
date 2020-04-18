package com.draglantix.entities;

import org.joml.Vector2i;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.utils.DragonMath;
import com.draglantix.world.World;

public class EntityManager {

	// private static BidiMap<Vector2i, Entity> entities = new ArrayList<Entity>();
	private static Entity[][] entity_map = new Entity[World.TILE_MAP_SIZE][World.TILE_MAP_SIZE];

	public static void tick() {
		for (Entity[] et : entity_map) {
			for (Entity e : et) {
				if (e != null) {
					e.tick();
				}
			}
		}
	}

	public static void render(Graphics g) {
		for (Entity[] et : entity_map) {
			for (Entity e : et) {
				if (e != null) {
					e.render(g);
				}
			}
		}
	}

	public static void addEntity(Entity e) {
		Vector2i pos = DragonMath.tilePos(e.getPosition());
		entity_map[pos.x][pos.y] = e;
	}

	public static Entity getEntity(Vector2i pos) {
		try {
			return entity_map[pos.x][pos.y];
		} catch (Exception e) {
			return null;
		}
	}

}
