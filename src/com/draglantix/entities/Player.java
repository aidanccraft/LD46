package com.draglantix.entities;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.flare.textures.Animation;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;

public class Player extends Dynamic{

	private Vector2f velocity = new Vector2f(0, 0);
	private Vector2f gravity = new Vector2f(0, -.02f);
	private float resistiveForce = .05f;
	
	public Player(Animation animation, Vector2f position, Vector2f scale) {
		super(animation, position, scale);
	}

	@Override
	public void tick() {
		super.tick();
		run();
		
		handleAnimations();
	}
	
	private void run() {
		if(Window.getInput().isKeyDown(GLFW.GLFW_KEY_W)) {
			this.velocity.add(0, 0.15f);
		} else if(Window.getInput().isKeyDown(GLFW.GLFW_KEY_S)) {
			this.velocity.add(0, -0.15f);
		}
		
		if(Window.getInput().isKeyDown(GLFW.GLFW_KEY_A)) {
			this.velocity.add(-0.15f, 0);
		} else if(Window.getInput().isKeyDown(GLFW.GLFW_KEY_D)) {
			this.velocity.add(0.15f, 0);
		}
		
		this.velocity.add(gravity);
		float speed = velocity.length();
		Vector2f temp = new Vector2f(0, 0);
		if(this.velocity.x != 0 || this.velocity.y != 0) {
			velocity.normalize(temp);
		}
		this.velocity.sub(temp.mul((float) Math.pow(speed, 2) * resistiveForce));
		this.position.add(this.velocity);
	}
	
	private void handleAnimations() {
		if(!alive) {
			this.animation = Assets.playerDie;
		}else if(interacting) {
			this.animation = Assets.playerInteract;
		}else if(swimming){
			this.animation = Assets.playerSwimming;
		}else {
			this.animation = Assets.playerIdle;
		}
	}

}
