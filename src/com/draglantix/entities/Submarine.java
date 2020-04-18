package com.draglantix.entities;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;

public class Submarine {
	
	private Vector2f position, velocity;
	private float resistiveForce;
	
	private boolean lights = false;
	
	public Submarine(Vector2f position, float resistiveForce) {
		this.position = position;
		this.velocity = new Vector2f(0, 0);
		this.resistiveForce = resistiveForce;
	}
	
	public void update() {
		move();
		if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_E)) {
			lights = !lights;
		}
	}
	
	private void move() {
		if(Window.getInput().isKeyDown(GLFW.GLFW_KEY_W)) {
			this.velocity.add(0, 0.0015f);
		} else if(Window.getInput().isKeyDown(GLFW.GLFW_KEY_S)) {
			this.velocity.add(0, -0.0015f);
		}
		
		if(Window.getInput().isKeyDown(GLFW.GLFW_KEY_A)) {
			this.velocity.add(-0.015f, 0);
		} else if(Window.getInput().isKeyDown(GLFW.GLFW_KEY_D)) {
			this.velocity.add(0.0015f, 0);
		}
		
		float speed = velocity.length();
		Vector2f temp = new Vector2f(0, 0);
		if(this.velocity.x != 0 || this.velocity.y != 0) {
			velocity.normalize(temp);
		}
		this.velocity.sub(temp.mul((float) Math.pow(speed, 2) * resistiveForce));
		this.position.add(this.velocity);
		
		boundPos();
	}
	
	private void boundPos() {
		if(position.x < 0) {
			position.x = 0;
			velocity.x = 0;
		}
		if(position.x > Assets.map.getWidth()-1) {
			position.x = Assets.map.getWidth()-1;
			velocity.x = 0;
		}
		if(position.y > 0) {
			position.y = 0;
			velocity.y = 0;
		}
		if(position.y < -Assets.map.getHeight()+1) {
			position.y = -Assets.map.getHeight()+1;
			velocity.y = 0;
		}
	}
	
	public float getDistance(String direction) {
		return 0.5f;
	}
	public float calculateLight() {
		float depth = getDepth() * 0.01f;
		if(depth < 1) {
			depth = 1;
		}
		if(depth > 9) {
			depth = 9;
		}
		return (float)Math.log(depth);
	}
	
	public float getDepth() {
		return -1 * getPosition().y;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public Vector2f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}

	public float getResistiveForce() {
		return resistiveForce;
	}

	public void setResistiveForce(float resistiveForce) {
		this.resistiveForce = resistiveForce;
	}

	public boolean isLights() {
		return lights;
	}

	public void setLights(boolean lights) {
		this.lights = lights;
	}

}
