package com.draglantix.terrain;

import org.joml.Vector2f;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.collision.AABBCollider;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;
import com.draglantix.states.PlayState;

public class SupplyStation {

	private Vector2f position, scale;
	private AABB bounds;
	private int r, g, b;
	private int stationType;
	private boolean visited = false;
	private boolean isColliding = false;
	private String message;
	private String exitMessage = "Press Space to exit";

	public SupplyStation(Vector2f position, int stationType) {
		this.position = position;
		this.stationType = stationType;
		
		if(this.stationType == 1) {
			this.scale = new Vector2f(3);
			this.r = 167;
			this.g = 204;
			this.b = 113;
		} else if(this.stationType == 2) {
			this.scale = new Vector2f(7);
			this.r = 60;
			this.g = 105;
			this.b = 46;
		}
		
		this.bounds = new AABB(position, scale, false);
	}

	private void resupply(Submarine sub) {
		sub.setOxygen(sub.getOxygen() + .1f);
		sub.setPower(sub.getPower() + .1f);
	}

	public void checkCollision(Submarine sub, PlayState state) {
		if (AABBCollider.collide(this.bounds, sub.bounds)) {
			resupply(sub);
			
			if(this.stationType == 2) {
				this.visited = true;
				
				if(!this.isColliding) {
					state.setState(5);
				}
				
				this.isColliding = true;
			}
		} else {
			this.isColliding = false;
		}
	}

	public void render(Graphics g, Vector2f subPos, float alpha) {
		g.drawImage(Assets.blank, position.sub(subPos, new Vector2f()).mul(2), scale.mul(2, new Vector2f()),
				new Vector2f(0), new Color(this.r, this.g, this.b, alpha));
	}
	
	public void renderText(Graphics g) {
		g.drawString(Assets.font, this.message, new Vector2f(-44, 40), new Vector2f(4), new Color(200, 174, 146, 1),
				88, g.FONT_LEFT);
		g.drawString(Assets.font, this.exitMessage, new Vector2f(-44, -40), new Vector2f(4), new Color(200, 174, 146, 1),
				g.FONT_LEFT);
	}
	
	public void setMessage(String message) {
		this.message = message;
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

	public int getStationType() {
		return stationType;
	}

	public void setStationType(int stationType) {
		this.stationType = stationType;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

}
