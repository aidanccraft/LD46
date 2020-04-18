package com.draglantix.utils;

import org.joml.Vector2f;

public class Quad {
	
	private Vector2f pos, halfLengths;
	
	public Quad(Vector2f pos, Vector2f halfLengths) {
		this.pos = pos;
		this.halfLengths = halfLengths;
	}

	public boolean contains(Vector2f point) {
		return !(point.x < pos.x-halfLengths.x || pos.x+halfLengths.x < point.x 
				|| point.y < pos.y-halfLengths.y || pos.y+halfLengths.y < point.y);
	}
	
	public boolean intersects(Quad b) {
		return !(b.pos.x+b.halfLengths.x < pos.x-halfLengths.x || pos.x+halfLengths.x < b.pos.x-b.halfLengths.x 
				|| b.pos.y+b.halfLengths.y < pos.y-halfLengths.y || pos.y+halfLengths.y < b.pos.y-b.halfLengths.y);
	}
	
	public Vector2f getPos() {
		return pos;
	}

	public Vector2f getHalfLengths() {
		return halfLengths;
	}

}
