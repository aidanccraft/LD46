package com.draglantix.terrain;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.collision.AABBCollider;
import com.draglantix.flare.collision.Ray;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public class Terrain {

	public static void render(Graphics g, Submarine sub, List<AABB> boxes, int direction) {
		Vector2f dir;
		float offset;

		if (direction == 0) {
			dir = new Vector2f(0, -1);
			offset = -1.5f;
		} else if (direction == 1) {
			dir = new Vector2f(0, 1);
			offset = -.5f;
		} else if (direction == 2) {
			dir = new Vector2f(-1, 0);
			offset = -1.5f;
		} else {
			dir = new Vector2f(1, 0);
			offset = -.5f;
		}

		Ray ray = new Ray(sub.getPosition(), dir);
		List<Vector2f> collisions = new ArrayList<Vector2f>();
		List<AABB> boxCollisions = new ArrayList<AABB>();

		for (AABB b : boxes) {
			Vector2f collision = AABBCollider.intersect(b, ray);

			if (collision.x != 0 || collision.y != 0) {
				collisions.add(collision);
				boxCollisions.add(b);
			}
		}

		if (collisions.size() > 0) {

			Vector2f min = collisions.get(0);

			for (Vector2f c : collisions) {
				if (direction == 1 || direction == 2) {
					if (c.length() > min.length()) {
						min = c;
					}
				} else {
					if (c.length() < min.length()) {
						min = c;
					}
				}
			}

			float distance = Math.max(Math.abs(sub.getPosition().x - min.x) + offset,
					Math.abs(sub.getPosition().y - min.y) + offset);

			System.out.println(distance);

			distance = (float) (Math.log10(distance) / Math.log10(4));

			if (distance > 1) {
				distance = 1;
			} else if (distance < 0) {
				distance = 0;
			}

			g.drawImage(Assets.terrain, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
					new Color(255, 255, 255, 1 - distance));
		}
	}

}
