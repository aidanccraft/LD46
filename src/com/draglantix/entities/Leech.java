package com.draglantix.entities;

import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.draglantix.flare.audio.Source;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;
import com.draglantix.states.PlayState;

public class Leech {

	private Submarine sub;
	
	private Vector2f target = new Vector2f();
	
	private Random rand = new Random();
	private Vector2f position;
	private int state = 0;
	
	private Source sfx = new Source(0.4f, 500, 1000);
	private Source sfx1 = new Source(0.4f, 500, 1000);
	
	private float theta, dis;
	
	private float health = 100f;
	
	private int returnEvent;
	
	public boolean dead = false;
	
	public Leech(Submarine sub) {
		this.sub = sub;
		
		this.theta = (float) (rand.nextFloat() * Math.PI * 2);
		this.dis = rand.nextFloat() * 1 + 30f;
		
		float phi = (float) (rand.nextFloat() * Math.PI * 2);
		
		swim();
		
		sfx.setPosition3D(new Vector3f(this.position.x, this.position.y, (float)(dis * Math.cos(phi))));
		
		sfx.play(Assets.leechHunting);
		
		sfx1.setPosition(new Vector2f(0));
		sfx1.setLooping(true);
		
		this.target = new Vector2f(sub.getPosition());
	}
	
	public void update() {
		if(state == 0) {//Hunting
			
			theta += 0.01f;
			if(theta > (Math.PI * 2)) {
				theta = 0;
			}
			
			swim();
			
			if(getRadialDistance() < 2f) {
				state = 1;
				sfx.setPosition3D(new Vector3f(0));
				sfx.play(Assets.collisionSFX);
				
				sfx1.play(Assets.alarm);
				
				returnEvent = PlayState.eventOpenWindow(1);
			}
		}else if(state == 1) {// Attacking
			if(!sfx.isPlaying()) {
				sfx.setLooping(true);	
				sfx.play(Assets.windowbreaking);
			}
			
			if(sub.isLights() && PlayState.getCurrentState() == returnEvent) {
				health -= 1f;
			}
			
			if(health == 0) {
				sfx.setVolume(0f);
				sfx1.setVolume(0f);
				state = 3;
			}
			
			sub.setOxygen(sub.getOxygen() - 0.03f);
			this.position = sub.getPosition();
		}else {
			PlayState.resetEvent(returnEvent);
			this.target = new Vector2f((float) (50 * Math.cos(theta)), (float) (50 * Math.sin(theta)));
			position.lerp(new Vector2f(target), 0.007f);
			if(getRadialDistance() > 40) {
				dead = true;
			}
		}
	}
	
	private void swim() {
		
		target.lerp(new Vector2f(sub.getPosition()), 0.007f);
		
		if(rand.nextInt(100) < 20) {
			if(sub.isLights()) {
				dis += 0.2f;
			}else {
				dis -= 0.1f;
			}
		}
		
		this.dis += 0.1f * Math.cos(theta * 10);
		
		this.position = new Vector2f((float) (target.x + (dis * Math.cos(theta))),
				(float) (target.y + dis * Math.sin(theta)));
	}
	
	public void render(Graphics g, float sonarRadius, float alpha) {
		if(getRadialDistance() < sonarRadius) {
			g.drawImage(Assets.sonarDot, position.sub(sub.getPosition(), new Vector2f()).mul(2), new Vector2f(8),
					new Vector2f(0), new Color(150, 0, 0, alpha));
		}
	}
	
	private float getRadialDistance() {
		return sub.getPosition().distance(position);
	}
	
	public void fadeSFX(float value) {
		sfx.setVolume(value);
		sfx1.setVolume(value);
	}
	
	public void resumeSFX() {
		sfx.setVolume(1);
		sfx1.setVolume(1);
	}
}
