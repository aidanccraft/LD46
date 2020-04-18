package com.draglantix.entities;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.textures.Texture;
import com.draglantix.flare.util.Color;
import com.draglantix.utils.DragonMath;

public abstract class Entity {
	
	protected Texture texture;
	protected Vector2f position;
	protected Vector2f scale;
	
	protected boolean onScreen;
	
	protected boolean pushable = false;
	
	protected float rot = 0;

	public Entity(Texture texture, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.position = position;
		this.scale = scale;
	}
	
	public abstract void tick();
	
	public void render(Graphics g) {
		g.drawImage(texture, position, scale, new Vector2f(0, rot), new Color(255, 255, 255, 1));
	}
	
	protected void flip() {
		if(rot == 0) {
			rot = -180;
		}else {
			rot = 0;
		}
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
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
	
	public boolean isOnScreen() {
		return onScreen;
	}

	public void setOnScreen(boolean onScreen) {
		this.onScreen = onScreen;
	}

}
