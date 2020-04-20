package com.draglantix.entities;

import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.draglantix.flare.audio.Source;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public class Leech {

	private Submarine sub;
	
	private Random rand = new Random();
	private Vector2f position;
	private int state = 0;
	
	private Source sfx = new Source(0.4f, 500, 1000);
	private Source sfx1 = new Source(0.4f, 500, 1000);
	
	private float alarmPitch = 1f;
	
	public Leech(Submarine sub) {
		this.sub = sub;
		
		float theta = (float) (rand.nextFloat() * Math.PI * 2);
		float phi = (float) (rand.nextFloat() * Math.PI * 2);
		float dis = 0; //rand.nextFloat() * 40;
		this.position = new Vector2f((float) (sub.getPosition().x + (dis * Math.cos(theta))),
				(float) (sub.getPosition().y + dis * Math.sin(theta)));
		
		sfx.setPosition3D(new Vector3f(this.position.x, this.position.y, (float)(dis * Math.cos(phi))));
		
		sfx.play(Assets.leechHunting);
		
		sfx1.setPosition(new Vector2f(0));
		sfx1.setLooping(true);
	}
	
	public void update() {
		if(state == 0) {//Hunting
			if(getRadialDistance() < 2f) {
				state = 1;
				sfx.setPosition3D(new Vector3f(0));
				sfx.play(Assets.collisionSFX);
				
				sfx1.play(Assets.alarm);
			}
		}else if(state == 1) {//
			if(!sfx.isPlaying()) {
				sfx.setLooping(true);	
				sfx.play(Assets.windowbreaking);
			}
			
			sfx1.setPitch(alarmPitch);

			if(alarmPitch < 1.5f)
				alarmPitch += 0.0001f;
			
			sub.setOxygen(sub.getOxygen() - 0.03f);
			this.position = sub.getPosition();
		}
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
	
	
}
