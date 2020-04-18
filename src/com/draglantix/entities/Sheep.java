package com.draglantix.entities;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.draglantix.flare.textures.Animation;

public class Sheep extends Dynamic{

	public Sheep(Animation animation, Vector2f position, Vector2f scale) {
		super(animation, position, scale);
		pushable = true;
	}
	
	@Override
	public void tick() {
		super.tick();
	}

}
