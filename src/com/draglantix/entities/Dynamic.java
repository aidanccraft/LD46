package com.draglantix.entities;

import org.joml.Vector2f;

import com.draglantix.flare.audio.AudioMaster;
import com.draglantix.flare.audio.Source;
import com.draglantix.flare.textures.Animation;

public abstract class Dynamic extends Entity {

	protected Source source;

	protected boolean alive = true, interacting = false, swimming = false;
	protected float health = 1f;

	protected Animation animation = null;

	public Dynamic(Animation animation, Vector2f position, Vector2f scale) {
		super(animation.getTexture(), position, scale);
		this.animation = animation;
		source = new Source(1.5f, 10f, 0);
		source.setPosition(this.position);
		AudioMaster.sources.add(source);
	}

	@Override
	public void tick() {
		if (interacting && this.animation.hasPlayed()) {
			interacting = false;
			this.animation.setHasPlayed(false);
		}
		if (animation != null) {
			this.texture = animation.getTexture();
		}
		source.setPosition(this.position);
	}

	public void move(Vector2f dir) {
		Vector2f open = checkCollisions(new Vector2f(dir));
		this.position.add(open);
	}

	private Vector2f checkCollisions(Vector2f dir) {
		return null;
		
	}
}
