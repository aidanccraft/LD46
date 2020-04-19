package com.draglantix.terrain;

import org.joml.Vector2f;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.collision.AABBCollider;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public class SupplyStation {

	private Vector2f position, scale;
	private AABB bounds;

	public SupplyStation(Vector2f position, Vector2f scale) {
		this.position = position;
		this.scale = scale;
		this.bounds = new AABB(position, scale, false);
	}

	private void resupply(Submarine sub) {
		sub.setOxygen(sub.getOxygen() + .1f);
		sub.setPower(sub.getPower() + .1f);
	}

	public void checkCollision(Submarine sub) {
		if (AABBCollider.collide(this.bounds, sub.bounds)) {
			resupply(sub);
		}
	}

	public void render(Graphics g, Vector2f subPos, float alpha) {
		g.drawImage(Assets.blank, position.sub(subPos, new Vector2f()).mul(2), scale.mul(2, new Vector2f()),
				new Vector2f(0), new Color(167, 204, 113, alpha));
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public Vector2f getScale() {
		return scale;
	}

	public void setScale(Vector2f scale) {
		this.scale = scale;
	}

	public AABB getBounds() {
		return bounds;
	}

	public void setBounds(AABB bounds) {
		this.bounds = bounds;
	}

}
