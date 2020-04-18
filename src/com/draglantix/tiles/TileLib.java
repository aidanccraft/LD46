package com.draglantix.tiles;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;
import com.draglantix.utils.BidiMap;
import com.draglantix.world.World;

public class TileLib {

	public static Vector2f scale = new Vector2f(World.TILE_SIZE, World.TILE_SIZE);
	public static Vector2f rotation = new Vector2f(0, 0);
	
	public static BidiMap<String, Integer> TILE_IDS = new BidiMap<String, Integer>();
	public static List<String> SOLIDS = new ArrayList<String>();
	
	static{
		TILE_IDS.put("stone_floor_0", 0);
	    TILE_IDS.put("stone_floor_1", 1);
	    TILE_IDS.put("stone_floor_2", 2);
	    TILE_IDS.put("stone_floor_3", 3);
		
		TILE_IDS.put("stone_wall", 4);
		TILE_IDS.put("stone_wall_cracked", 5);
		
		TILE_IDS.put("door_closed", 6);
		TILE_IDS.put("door_open", 7);
		
		TILE_IDS.put("wood", 8);
	    
	    TILE_IDS.put("void", 9);
	    
	    TILE_IDS.put("stair_up", 10);
	    TILE_IDS.put("stair_down", 11);
	    
	    TILE_IDS.put("water", 12);
	    
	    //Solids///////////
	    
		SOLIDS.add("stone_wall");
		SOLIDS.add("stone_wall_cracked");
		
		SOLIDS.add("door_closed");
	}
	
	public static Tile createTile(int id, Vector2i pos) {
		String name = TILE_IDS.getKey(id);
		if(name == "water") {
			return new Tile(Assets.tiles.getAnimation(id, 2), name, id, pos, SOLIDS.contains(name), new Color(255, 255, 255, 1));
		}else {
			return new Tile(Assets.tiles.get(id), name, id, pos, SOLIDS.contains(name), new Color(255, 255, 255, 1));
		}
	}
	
}
