package com.draglantix.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Functions;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;

public class DragonMath {

	public static Vector2f convertScreenSpace(Graphics g) {
		Vector2f ndc = new Vector2f((2.0f * Window.getInput().getMousePos().x) / Window.getWidth() - 1f,
				1f - (2.0f * Window.getInput().getMousePos().y) / Window.getHeight());
		Vector4f clipCoords = new Vector4f(ndc.x, ndc.y, -1.0f, 1.0f);
		Matrix4f m = Functions.createProjectionMatrix(Window.getWidth(), Window.getHeight())
				.mul(Assets.camera.createViewMatrix()).invertOrtho();
		Vector4f worldRay = clipCoords.mul(m);
		return new Vector2f(worldRay.x / g.getScale(), worldRay.y / g.getScale());
	}
	
	public static float percentToTheta(float percent) {
		float dec = percent / 100;
		return (dec * 300) - 150;
	}
	
	public static String evaluateIntegrity(float integrity) {
		if(integrity > 70) {
			return "Good";
		}else if(integrity <= 70 && integrity > 50) {
			return "Ok";
		}else if(integrity <= 50 && integrity > 20) {
			return "Severely Damaged";
		}else {
			return "Critical";
		}
	}

	public static int floor(float num) {
		return (int) Math.floor((double) num);
	}

	public static int ceil(float num) {
		return (int) Math.ceil((double) num);
	}

}
