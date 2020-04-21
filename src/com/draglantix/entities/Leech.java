package com.draglantix.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.draglantix.main.Assets;
import com.draglantix.states.PlayState;

public class Leech extends SeaMonster{

	public Leech(Submarine sub) {
		super(sub, 1);
		
		sfx0.play(Assets.leechHunting);
		sfx1.setPosition(new Vector2f(0));
		sfx1.setLooping(true);
	}
	
	@Override
	public void tick() {
		if(behaviorState == 0) {//Hunting
			swim();
			
			if(getRadialDistance() < 3f) {
				behaviorState = 1;
				sfx0.setPosition3D(new Vector3f(0));
				sfx0.play(Assets.collisionSFX);
				sfx1.setVolume(1f);
				sfx1.play(Assets.alarm);
				returnEvent = PlayState.eventOpenWindow(1);
			}
		}else if(behaviorState == 1) {// Attacking
			if(!sfx0.isPlaying()) {
				sfx0.setLooping(true);	
				sfx0.play(Assets.windowbreaking);
			}
			
			if(sub.isLights() && PlayState.getCurrentState() == returnEvent) {
				health -= 1f;
			}
			
			if(health == 0) {
				sfx0.setVolume(0f);
				sfx1.setVolume(0f);
				behaviorState = 3;
			}
			
			sub.setOxygen(sub.getOxygen() - 0.03f);
			this.position = sub.getPosition();
		}else { // Swim Away
			PlayState.resetEvent(returnEvent);
			this.target = new Vector2f((float) (50 * Math.cos(theta)), (float) (50 * Math.sin(theta)));
			position.lerp(new Vector2f(target), 0.007f);
			if(getRadialDistance() > 40) {
				dead = true;
			}
		}
	}
	
}
