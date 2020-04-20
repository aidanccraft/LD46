package com.draglantix.terrain;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.collision.AABBCollider;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.textures.Texture;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.states.PlayState;

public class SupplyStation {

	private Vector2f position, scale;
	private AABB bounds;
	private int r, g, b;
	private int stationType;
	private boolean visited = false;
	private boolean isColliding = false;
	private int biome;
	
	private String[] logList;
	
	private String bottomMessage1 = "Press D for next log";
	private String bottomMessage2 = "Press A or D for logs";
	private String bottomMessage3 = "Press Space to exit";
	private String bottomMessage = bottomMessage1;
	
	private int currentScreen = 0;

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
	
	public void tick() {
		if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_A)) {
			currentScreen--;
		} else if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_D)) {
			currentScreen++;
		}
		
		bottomMessage = bottomMessage2;
		
		if(currentScreen <= 0) {
			currentScreen = 0;
			bottomMessage = bottomMessage1;
		} else if(currentScreen >= logList.length) {
			currentScreen = logList.length;
			bottomMessage = bottomMessage3;
		}
	}

	public void render(Graphics g, Vector2f subPos, float alpha) {
		g.drawImage(Assets.blank, position.sub(subPos, new Vector2f()).mul(2), scale.mul(2, new Vector2f()),
				new Vector2f(0), new Color(this.r, this.g, this.b, alpha));
	}
	
	public void renderScreen(Graphics g, PlayState state) {
		if(currentScreen != logList.length) {
			g.drawString(Assets.font, this.logList[currentScreen], new Vector2f(-44, 40), new Vector2f(4), new Color(200, 174, 146, 1),
					88, g.FONT_LEFT);
			g.drawString(Assets.font, this.bottomMessage, new Vector2f(-44, -40), new Vector2f(4), new Color(200, 174, 146, 1),
					g.FONT_LEFT);
		} else {
			g.drawString(Assets.font, "Map of " + state.getBiome(biome), new Vector2f(0, 40), new Vector2f(4), new Color(200, 174, 146, 1),
					88, g.FONT_CENTER);
			g.drawImage(getMinMap(biome), new Vector2f(0), new Vector2f(60), new Vector2f(0), new Color(255, 255, 255, 1));
			
			g.drawString(Assets.font, this.bottomMessage, new Vector2f(-44, -40), new Vector2f(4), new Color(200, 174, 146, 1),
					g.FONT_LEFT);
		}
	}
	
	private Texture getMinMap(int biome) {
		switch(biome) {
			
			case 1:
				return Assets.minMap0;
			
			case 2:
				return Assets.minMap1;
				
			case 3:
				return Assets.minMap2;
				
			case 4:
				return Assets.minMap3;
				
			case 5:
				return Assets.minMap4;
			
			default:
				return null;
		
		}
	}
	
	public void setLogs(String logs) {
		this.logList = logs.trim().split("\n- ");
		this.logList[0] = this.logList[0].substring(2);
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

	public int getBiome() {
		return biome;
	}

	public void setBiome(int biome) {
		this.biome = biome;
	}

}
