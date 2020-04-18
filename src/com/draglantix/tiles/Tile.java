package com.draglantix.tiles;

import org.joml.Vector2i;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.textures.Animation;
import com.draglantix.flare.textures.Texture;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;
import com.draglantix.utils.DragonMath;

public class Tile {
	
	private Texture texture;
	private Animation animation = null;
	
	private boolean solid;
	private String name;
	private int id;
	private Vector2i pos;
	private Color color;
	
	private boolean morphed = false;
	
	public Tile(Texture texture, String name, int id, Vector2i pos, boolean solid, Color color) {
		this.texture = texture;
		this.name = name;
		this.id = id;
		this.pos = pos;
		this.solid = solid;
		this.color = color;
	}
	
	public Tile(Animation animation, String name, int id, Vector2i pos, boolean solid, Color color) {
		this.animation = animation;
		this.name = name;
		this.id = id;
		this.pos = pos;
		this.solid = solid;
		this.color = color;
	}

	public void render(Graphics g) {
		if(animation == null) {
			g.drawImage(texture, DragonMath.worldPos(pos), TileLib.scale, TileLib.rotation, color);
		}else {
			g.drawImage(animation.getTexture(), DragonMath.worldPos(pos), TileLib.scale, TileLib.rotation, color);
		}
	}
	
	public void morph(String name) {
		this.id = TileLib.TILE_IDS.get(name);
		this.texture = Assets.tiles.get(id);
		this.name = name;
		this.solid = TileLib.SOLIDS.contains(name);
		setMorphed(true);
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	public Vector2i getPos() {
		return pos;
	}

	public void setPos(Vector2i pos) {
		this.pos = pos;
	}

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	public boolean isMorphed() {
		return morphed;
	}

	public void setMorphed(boolean morphed) {
		this.morphed = morphed;
	}
	
}
