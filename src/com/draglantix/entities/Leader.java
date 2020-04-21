package com.draglantix.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.draglantix.main.Assets;
import com.draglantix.states.PlayState;

public class Leader extends SeaMonster{

	public Leader(Submarine sub) {
		super(sub, 3);
		
		sfx0.play(Assets.colossalHunting);
		sfx0.setLooping(true);
		sfx1.setPosition(new Vector2f(0));
		sfx1.setLooping(true);
	}
	
	@Override
	public void tick() {
		if(behaviorState == 0) {//Hunting
			swim();
			
			System.out.println(getRadialDistance());
			
			if(getRadialDistance() < 8f) {
				behaviorState = 1;
				sfx0.setPosition3D(new Vector3f(0));
				sfx1.play(Assets.alarm);
				returnEvent = PlayState.eventOpenWindow(4);
			}
		}else if(behaviorState == 1) {// Attacking
			sub.setOxygen(sub.getOxygen() - 0.3f);
			this.position = sub.getPosition();
		}
	}
	
}
