package com.draglantix.entities;

import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.draglantix.flare.audio.Source;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public abstract class SeaMonster {

	protected Submarine sub;
	
	protected Random rand = new Random();
	protected Vector2f position = new Vector2f();
	protected Vector2f target = new Vector2f();
	
	protected int behaviorState = 0;
	
	protected Source sfx0 = new Source(0.4f, 500, 1000);
	protected Source sfx1 = new Source(0.4f, 500, 1000);
	
	protected float theta, dis;
	
	protected float health = 100f;
	protected boolean dead = false;
	
	protected int returnEvent;
	
	public SeaMonster(Submarine sub) {
		this.sub = sub;
		
		this.theta = (float) (rand.nextFloat() * Math.PI * 2);
		this.dis = rand.nextFloat() * 1 + 30f;
		
		float phi = (float) (rand.nextFloat() * Math.PI * 2);
		
		this.position = new Vector2f((float) (sub.getPosition().x + (dis * Math.cos(theta))),
				(float) (sub.getPosition().y + dis * Math.sin(theta)));
		this.target = new Vector2f(position);
		
		sfx0.setPosition3D(new Vector3f(this.position.x, this.position.y, (float)(dis * Math.cos(phi))));
	}
	
	public abstract void tick();
	
	public void render(Graphics g, float sonarRadius, float alpha) {
		if(getRadialDistance() < sonarRadius) {
			g.drawImage(Assets.sonarDot, position.sub(sub.getPosition(), new Vector2f()).mul(2), new Vector2f(8),
					new Vector2f(0), new Color(150, 0, 0, alpha));
		}
	}
	
	protected void swim() {
		
		theta += 0.01f;
		if(theta > (Math.PI * 2)) {
			theta = 0;
		}
		
		if(rand.nextInt(100) < 20) {
			if(dis > 0) {
				dis -= 0.1f;
			}
		}
		
		this.dis += 0.5 * Math.cos(theta * 10);
		
		this.target = new Vector2f((float) (sub.getPosition().x + (dis * Math.cos(theta))),
				(float) (sub.getPosition().y + dis * Math.sin(theta)));
		
		this.position.lerp(new Vector2f(this.target), 0.007f);
	}
	
	protected float getRadialDistance() {
		return sub.getPosition().distance(position);
	}
	
	public void fadeSFX(float value) {
		sfx0.setVolume(value);
		sfx1.setVolume(value);
	}
	
	public void resumeSFX() {
		sfx0.setVolume(1);
		sfx1.setVolume(1);
	}

	public boolean isDead() {
		return dead;
	}
	
}
