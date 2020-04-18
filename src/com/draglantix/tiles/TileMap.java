package com.draglantix.tiles;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Reader;
import com.draglantix.utils.DragonMath;
import com.draglantix.world.World;

public class TileMap {
	
	private static Tile[][] map = new Tile[World.TILE_MAP_SIZE][World.TILE_MAP_SIZE];
	
	private String name;
	
	public TileMap(String name) {
		load(name);
	}
	
	public void render(Graphics g) {
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[x].length; y++) {
				Tile t = map[x][y];
				if(DragonMath.isOnScreen(t.getPos(), new Vector2f(32))){
					t.render(g);
				}
			}
		}
	}
	
	public void load(String name) {
		String raw = Reader.loadFileAsString("maps/"+name + ".map");
		String[] tokens = raw.split("\\s+");
				
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[x].length; y++) {
				
				int id = Reader.parseInt(tokens[x+(y*(World.TILE_MAP_SIZE))]);
				
				map[x][y] = TileLib.createTile(id, new Vector2i(x, y));
			}
		}
	}
	
	public static Tile getTile(Vector2i pos) {
		try {
			return map[pos.x][pos.y];
		}catch(Exception e) {
			return null;
		}
	}
	
	public static void setTile(Tile t, Vector2i pos) {
		try {
			 map[pos.x][pos.y] = t;
		}catch(Exception e) {}
	}

	public String getName() {
		return name;
	}
	
}
