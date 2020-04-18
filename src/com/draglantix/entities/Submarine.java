package com.draglantix.entities;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.collision.AABBCollider;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.states.PlayState;

public class Submarine {

	private Vector2f position, velocity;
	private float resistiveForce;

	private boolean lights = false;

	private float integrity = 100, power = 100, oxygen = 100;

	private boolean alive = true;

	public AABB bounds;

	public Submarine(Vector2f position, float resistiveForce) {
		this.position = position;
		this.velocity = new Vector2f(0, 0);
		this.resistiveForce = resistiveForce;
		this.bounds = new AABB(new Vector2f(position), new Vector2f(1), true);
		PlayState.bounds.add(this.bounds);
	}

	public void update() {
		getInput();
		move();

		if (lights) {
			power -= .005f;
		}

		oxygen -= .0075f;

		checkAlive();

	}

	private void move() {
		Vector2f direction = new Vector2f(0, 0);
		
		if (this.velocity.x != 0 || this.velocity.y != 0) {
			velocity.normalize(direction);
		}
		
		this.velocity.sub(direction.mul((float) Math.pow(velocity.length(), 2) * resistiveForce));

		checkCollisions();
		boundPos();
	}
	
	private void getInput() {
		boolean moved = false;

		if (Window.getInput().isKeyDown(GLFW.GLFW_KEY_W)) {
			this.velocity.add(0, 0.0015f);
			moved = true;
		} else if (Window.getInput().isKeyDown(GLFW.GLFW_KEY_S)) {
			this.velocity.add(0, -0.0015f);
			moved = true;
		}

		if (Window.getInput().isKeyDown(GLFW.GLFW_KEY_A)) {
			this.velocity.add(-0.0015f, 0);
			moved = true;
		} else if (Window.getInput().isKeyDown(GLFW.GLFW_KEY_D)) {
			this.velocity.add(0.0015f, 0);
			moved = true;
		}
		
		if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_E)) {
			lights = !lights;
		}

		if (moved) {
			power -= .025f;
		}		
	}

	private void checkCollisions() {
		this.bounds.setCenter(this.position.add(this.velocity, new Vector2f()));

		for (AABB other : PlayState.bounds) {
			if (!other.equals(this.bounds)) {
				if (AABBCollider.collide(this.bounds, other)) {
					Vector2f force = AABBCollider.correct(this.bounds, other);
					this.bounds.getCenter().add(force);

					System.out.println(this.velocity.length());

					if (Math.abs(force.x) > 0) {
						this.velocity.x = -this.velocity.x;
					}

					if (Math.abs(force.y) > 0) {
						this.velocity.y = -this.velocity.y;
					}

					integrity -= this.velocity.length() * 50;
				}
			}
		}

		this.position = this.bounds.getCenter();
	}

	private void boundPos() {
		if (position.x < 0) {
			position.x = 0;
			velocity.x = 0;
		}
		if (position.x > Assets.map.getWidth() - 1) {
			position.x = Assets.map.getWidth() - 1;
			velocity.x = 0;
		}
		if (position.y > 0) {
			position.y = 0;
			velocity.y = 0;
		}
		if (position.y < -Assets.map.getHeight() + 1) {
			position.y = -Assets.map.getHeight() + 1;
			velocity.y = 0;
		}
	}

	public float getDistance(String direction) {
		return 0.5f;
	}

	public float calculateLight() {
		float depth = getDepth() * 0.01f;
		if (depth < 1) {
			depth = 1;
		}
		if (depth > 9) {
			depth = 9;
		}
		return (float) Math.log(depth);
	}

	private void checkAlive() {
		if (oxygen <= 0 || power <= 0 || integrity <= 0) {
			alive = false;
		}
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

	public float getIntegrity() {
		return integrity;
	}

	public void setIntegrity(float integrity) {
		this.integrity = integrity;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}

	public float getOxygen() {
		return oxygen;
	}

	public void setOxygen(float oxygen) {
		this.oxygen = oxygen;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
