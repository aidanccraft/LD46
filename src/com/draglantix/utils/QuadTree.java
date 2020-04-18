package com.draglantix.utils;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;

public class QuadTree {

	private List<AABB> boxes = new ArrayList<Vector2f>();
	
	private Quad bound;
	private int capacity;
	private boolean divided;
	
	private QuadTree northEast;
	private QuadTree northWest;
	private QuadTree southEast;
	private QuadTree southWest;
	
	public QuadTree(Quad bound, int capacity) {
		this.bound = bound;
		this.capacity = capacity;
		this.divided = false;
	}
	
	public boolean insert(Vector2f p) {
		
		if(!bound.contains(p))
			return false;
		
		if(points.size() < capacity-1) {
			points.add(p);
			return true;
		}else {
			if(!divided) {
				subdivide();
			}
			if(northEast.insert(p)) {
				return true;
			}else if(northWest.insert(p)){
				return true;
			}else if(southEast.insert(p)) {
				return true;
			}else if(southWest.insert(p)) {
				return true;
			}
			return false;
		}
	}
	
	public void subdivide() {
		Quad ne = new Quad(new Vector2f(bound.getPos().x + (bound.getHalfLengths().x/2), bound.getPos().y - (bound.getHalfLengths().y/2)), bound.getHalfLengths().mul(.5f, new Vector2f()));
		Quad nw = new Quad(new Vector2f(bound.getPos().x - (bound.getHalfLengths().x/2), bound.getPos().y - (bound.getHalfLengths().y/2)), bound.getHalfLengths().mul(.5f, new Vector2f()));
		Quad se = new Quad(new Vector2f(bound.getPos().x + (bound.getHalfLengths().x/2), bound.getPos().y + (bound.getHalfLengths().y/2)), bound.getHalfLengths().mul(.5f, new Vector2f()));
		Quad sw = new Quad(new Vector2f(bound.getPos().x - (bound.getHalfLengths().x/2), bound.getPos().y + (bound.getHalfLengths().y/2)), bound.getHalfLengths().mul(.5f, new Vector2f()));
		
		northEast = new QuadTree(ne, capacity);
		northWest = new QuadTree(nw, capacity);
		southEast = new QuadTree(se, capacity);
		southWest = new QuadTree(sw, capacity);
		
		divided = true;
	}
	
	public List<Vector2f> query(Quad range) {
		return query(range, null);
	}
	
	private List<Vector2f> query(Quad range, List<Vector2f> last) {
		if(last == null) {
			last = new ArrayList<Vector2f>();
		}
			
		List<Vector2f> found = new ArrayList<Vector2f>();
		if(bound.intersects(range)) {
			for(Vector2f p : points) {
				if(range.contains(p))
					found.add(p);
			}
			
			if(divided) {
				found.addAll(northEast.query(range));
				found.addAll(northWest.query(range));
				found.addAll(southEast.query(range));
				found.addAll(southWest.query(range));
			}
		}
		return found;
	}
	
	public void render(Graphics g) {
		
		g.drawImage(Assets.debug, bound.getPos(), new Vector2f(bound.getHalfLengths().x * 2, bound.getHalfLengths().y * 2), new Vector2f(0), new Color(255, 255, 255, 1));
		
		if(divided) {
			northEast.render(g);
			northWest.render(g);
			southEast.render(g);
			southWest.render(g);
		}
		
		for(Vector2f p : points) {
			g.drawImage(Assets.debug, p, new Vector2f(1, 1), new Vector2f(90, 0), new Color(0, 255, 255, 1));
		}
	}
}
