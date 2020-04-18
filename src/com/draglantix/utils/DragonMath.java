package com.draglantix.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Functions;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.world.World;

public class DragonMath {

	public static Vector2f worldPos(Vector2i tilePos) {
		return new Vector2f((tilePos.x * World.TILE_SIZE) - ((World.TILE_SIZE * World.TILE_MAP_SIZE)/2),
				(tilePos.y * World.TILE_SIZE) - ((World.TILE_SIZE * World.TILE_MAP_SIZE)/2));
	}
	
	public static Vector2i tilePos(Vector2f worldPos) {
		float x = (worldPos.x + ((World.TILE_SIZE * World.TILE_MAP_SIZE)/2)) / World.TILE_SIZE;
		float y = (worldPos.y + ((World.TILE_SIZE * World.TILE_MAP_SIZE)/2)) / World.TILE_SIZE;
		
		return new Vector2i((int)x, (int)y);
	}
	
	public static Vector2f convertScreenSpace() {
		Vector2f ndc = new Vector2f((2.0f * Window.getInput().getMousePos().x) / Window.getWidth() - 1f,
				1f - (2.0f * Window.getInput().getMousePos().y) / Window.getHeight());
		Vector4f clipCoords = new Vector4f(ndc.x, ndc.y, -1.0f, 1.0f);
		Matrix4f m = Functions.createProjectionMatrix(Window.getWidth(), Window.getHeight()).mul(Assets.camera.createViewMatrix()).invertOrtho();
		Vector4f worldRay = clipCoords.mul(m);
		return new Vector2f(worldRay.x/Graphics.getScale(), worldRay.y/Graphics.getScale());
	}

	public static int floor(float num) {
		return (int) Math.floor((double) num);
	}
	
	public static int ceil(float num) {
		return (int) Math.ceil((double) num);
	}
	
	public static boolean isOnScreen(Vector2i pos, Vector2f padding) {
		
		Vector2f loc = worldPos(pos);
		
		Vector2f initial = new Vector2f(
				Assets.camera.getPosition().x / Graphics.getScale()
						- Window.getWidth() / 2 / Graphics.getScale(),
				Assets.camera.getPosition().y / Graphics.getScale()
						- Window.getHeight() / 2 / Graphics.getScale());
		Vector2f end = new Vector2f(
				Assets.camera.getPosition().x / Graphics.getScale()
						+ Window.getWidth() / 2 / Graphics.getScale(),
				Assets.camera.getPosition().y / Graphics.getScale()
						+ Window.getHeight() / 2 / Graphics.getScale());
		
		if(loc.x > initial.x - (padding.x/2) && loc.x < end.x + (padding.x/2) 
				&& loc.y > initial.y - (padding.y/2) && loc.y < end.y + (padding.y/2)) {
			return true;
		}
		return false;
	}
}
