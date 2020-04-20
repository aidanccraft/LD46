package com.draglantix.terrain;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.main.Assets;
import com.draglantix.states.PlayState;

public class StationHandler {

	private static List<SupplyStation> supplyStations = new ArrayList<SupplyStation>();
	private static Vector2f[] smallStationLocations = { new Vector2f(219, -140), new Vector2f(96, -274),
			new Vector2f(255, -345), new Vector2f(273, -505), new Vector2f(245, -553), new Vector2f(346, -692),
			new Vector2f(356, -503), new Vector2f(528, -466), new Vector2f(410, -317), new Vector2f(656, -352),
			new Vector2f(356, -167), new Vector2f(271, -101) };
	private static Vector2f[] largeStationLocations = { new Vector2f(133, -85), new Vector2f(200, -376), new Vector2f(226, -741),
			new Vector2f(794, -442), new Vector2f(623, -608) };

	private static SupplyStation closestStation, nextStation, respawn;

	private static String[] stationLogs;
	
	public static void init() {
		stationLogs = Assets.stationText.split("\n# ");
		stationLogs[0] = stationLogs[0].substring(2);
		
		for (Vector2f loc : smallStationLocations) {
			supplyStations.add(new SupplyStation(loc, 1));
		}

		int index = 0;
		for (Vector2f loc : largeStationLocations) {
			SupplyStation station = new SupplyStation(loc, 2);
						station.setLogs(stationLogs[index]);

			station.setBiome(index + 1);
			
			supplyStations.add(station);
			
			index++;
		}

		closestStation = supplyStations.get(0);
		nextStation = supplyStations.get(16);
		respawn = supplyStations.get(15);
	}
	
	public static List<SupplyStation> checkSonar(Submarine sub, float scale, List<SupplyStation> sonarStations) {
		for (SupplyStation station : supplyStations) {
			if (sub.getPosition().x + 20 * (scale) > station.getPosition().x
					- station.getScale().x / 2
					&& sub.getPosition().x - 20 * (scale) < station.getPosition().x
							+ station.getScale().x / 2) {
				if (sub.getPosition().y + 20 * (scale) > station.getPosition().y
						- station.getScale().y / 2
						&& sub.getPosition().y - 20 * (scale) < station.getPosition().y
								+ station.getScale().y / 2) {
					if (!sonarStations.contains(station)) {
						sonarStations.add(station);
					}
				}
			}

			if (station.getStationType() == 1) {
				if (station.getPosition().sub(sub.getPosition(), new Vector2f()).length() < closestStation
						.getPosition().sub(sub.getPosition(), new Vector2f()).length()) {
					closestStation = station;
				}
			}

		}
		
		return sonarStations;
	}
	
	public static void tick() {
		respawn.tick();
	}
	
	public static void checkCollisions(Submarine sub, PlayState state) {
		for (SupplyStation station : supplyStations) {
			station.checkCollision(sub, state);
		}

		if (nextStation.isVisited()) {
			respawn = nextStation;
			if(supplyStations.indexOf(nextStation) + 1 == supplyStations.size()) {
				PlayState.setEndGame(true);
			}else {
				nextStation = supplyStations.get(supplyStations.indexOf(nextStation) + 1);
			}
		}
	}
	
	public static void drawSonar(Graphics g, Submarine sub, float scale) {
		if (Math.abs(sub.getPosition().x - closestStation.getPosition().x) > 20
				|| Math.abs(sub.getPosition().y - closestStation.getPosition().y) > 20) {

			double stationLoc = Math.atan((sub.getPosition().y - closestStation.getPosition().y)
					/ (sub.getPosition().x - closestStation.getPosition().x));

			if (sub.getPosition().y - closestStation.getPosition().y > 0
					&& sub.getPosition().x - closestStation.getPosition().x > 0) {
				stationLoc -= Math.PI;
			}

			if (sub.getPosition().y - closestStation.getPosition().y < 0
					&& sub.getPosition().x - closestStation.getPosition().x > 0) {
				stationLoc += Math.PI;
			}

			g.drawImage(Assets.blank,
					new Vector2f((float) (scale / 2 * Math.cos(stationLoc)),
							(float) (scale / 2 * Math.sin(stationLoc))),
					new Vector2f(2), new Vector2f(0), new Color(255, 0, 255, 1));
		}

		if (Math.abs(sub.getPosition().x - nextStation.getPosition().x) > 20
				|| Math.abs(sub.getPosition().y - nextStation.getPosition().y) > 20) {
			double stationLoc = Math.atan((sub.getPosition().y - nextStation.getPosition().y)
					/ (sub.getPosition().x - nextStation.getPosition().x));

			if (sub.getPosition().y - nextStation.getPosition().y > 0
					&& sub.getPosition().x - nextStation.getPosition().x > 0) {
				stationLoc -= Math.PI;
			}

			if (sub.getPosition().y - nextStation.getPosition().y < 0
					&& sub.getPosition().x - nextStation.getPosition().x > 0) {
				stationLoc += Math.PI;
			}

			g.drawImage(Assets.blank,
					new Vector2f((float) (scale / 2 * Math.cos(stationLoc)),
							(float) (scale / 2 * Math.sin(stationLoc))),
					new Vector2f(2), new Vector2f(0), new Color(0, 255, 255, 1));
		}
	}
	
	public static void renderText(Graphics g, PlayState state) {
		respawn.renderScreen(g, state);
	}
	
	public static SupplyStation getRespawn() {
		return respawn;
	}
	
}
