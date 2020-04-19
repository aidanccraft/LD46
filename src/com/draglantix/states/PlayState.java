package com.draglantix.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.Submarine;
import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.terrain.SupplyStation;
import com.draglantix.terrain.Terrain;
import com.draglantix.utils.DragonMath;
import com.draglantix.utils.ImageDecoder;
import com.draglantix.utils.Quad;
import com.draglantix.utils.QuadTree;

public class PlayState extends GameState {

	private Submarine sub;

	private Random rand = new Random();

	private Vector3f ambientDir = new Vector3f(1, 0, 0);

	private float sonarScale = 0;
	private float maxSonarScale = 80;

	private int[][] map;
	private List<SupplyStation> supplyStations = new ArrayList<SupplyStation>();
	private Vector2f[] smallStationLocations = { new Vector2f(219, -140), new Vector2f(96, -274),
			new Vector2f(255, -345), new Vector2f(273, -505), new Vector2f(245, -553), new Vector2f(346, -692),
			new Vector2f(356, -503), new Vector2f(528, -466), new Vector2f(410, -317), new Vector2f(656, -352),
			new Vector2f(356, -167), new Vector2f(271, -101) };
	private Vector2f[] largeStationLocations = { new Vector2f(200, -376), new Vector2f(226, -741), new Vector2f(794, -442),
			new Vector2f(623, -608) };

	private SupplyStation closestStation, nextStation;
	
	private SupplyStation respawn;

	private Map<Integer, String> states = new HashMap<Integer, String>();
	private int currentState;

	private Map<Integer, String> biomes = new HashMap<Integer, String>();

	public static List<AABB> bounds = new ArrayList<AABB>();
	private List<AABB> sonarBounds = new ArrayList<AABB>();
	private List<SupplyStation> sonarStations = new ArrayList<SupplyStation>();

	private QuadTree qt;

	public PlayState(Graphics g, GameStateManager gsm) {
		super(g, gsm);
	}

	public void init() {
		sub = new Submarine(new Vector2f(33, -10), 0.2f);
		currentState = 0;

		states.put(0, "WINDOW DOWN");
		states.put(1, "WINDOW UP");
		states.put(2, "WINDOW LEFT");
		states.put(3, "WINDOW RIGHT");
		states.put(4, "SONAR");

		biomes.put(0, "...");
		biomes.put(1, "Shallows");
		biomes.put(2, "Caves");
		biomes.put(3, "Deep Caves");
		biomes.put(4, "Open Ocean");
		biomes.put(5, "Abyssal Zone");

		map = ImageDecoder.decode("res/textures/map.png");

		qt = new QuadTree(new Quad(new Vector2f(Assets.map.getWidth() / 2, -Assets.map.getWidth() / 2),
				new Vector2f(Assets.map.getWidth() / 2, Assets.map.getWidth() / 2)), 5);

		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				if (map[x][y] == 0) {
					qt.insert(new AABB(new Vector2f(x, -y), new Vector2f(1), false));
				}
			}
		}

		for (Vector2f loc : smallStationLocations) {
			supplyStations.add(new SupplyStation(loc, 1));
		}
		
		for (Vector2f loc : largeStationLocations) {
			supplyStations.add(new SupplyStation(loc, 2));
		}

		closestStation = supplyStations.get(0);
		nextStation = supplyStations.get(12);
		respawn = null;

		Assets.submarineSFX0.setLooping(true);
		Assets.submarineSFX0.play(Assets.waterambient);

		Assets.submarineSFX1.setLooping(true);
		Assets.submarineSFX1.play(Assets.subengine);
	}
	
	public void respawn() {
		if(respawn != null) {
			sub = new Submarine(new Vector2f(respawn.getPosition()), 0.2f);
		} else {
			sub = new Submarine(new Vector2f(33, -10), 0.2f);
		}
		currentState = 0;
	}
	
	@Override
	public void tick() {
		handleSubstates();

		handleAudio();

		sonarScale += .5f;
		if (sonarScale > maxSonarScale) {
			sonarScale = 0;
			this.sonarBounds.removeAll(sonarBounds);
			this.sonarStations.removeAll(sonarStations);
		}

		sub.update();

		PlayState.bounds = qt.query(new Quad(new Vector2f(sub.getPosition()), new Vector2f(5)));

		if (currentState == 4) {
			List<AABB> tmpsonar = qt.query(new Vector2f(sub.getPosition()), 20 * (sonarScale / maxSonarScale));
			for (AABB t : tmpsonar) {
				if (!this.sonarBounds.contains(t)) {
					this.sonarBounds.add(t);
				}
			}

			for (SupplyStation station : supplyStations) {
				if (sub.getPosition().x + 20 * (sonarScale / maxSonarScale) > station.getPosition().x
						- station.getScale().x / 2
						&& sub.getPosition().x - 20 * (sonarScale / maxSonarScale) < station.getPosition().x
								+ station.getScale().x / 2) {
					if (sub.getPosition().y + 20 * (sonarScale / maxSonarScale) > station.getPosition().y
							- station.getScale().y / 2
							&& sub.getPosition().y - 20 * (sonarScale / maxSonarScale) < station.getPosition().y
									+ station.getScale().y / 2) {
						if (!this.sonarStations.contains(station)) {
							this.sonarStations.add(station);
						}
					}
				}

				if(station.getStationType() == 1) {
					if (station.getPosition().sub(sub.getPosition(), new Vector2f()).length() < closestStation.getPosition()
							.sub(sub.getPosition(), new Vector2f()).length()) {
						closestStation = station;
					}
				}

			}

		} else {
			this.sonarBounds.removeAll(sonarBounds);
			this.sonarStations.removeAll(sonarStations);
		}

		for (SupplyStation station : supplyStations) {
			station.checkCollision(sub);
		}
		
		if(nextStation.isVisited()) {
			respawn = nextStation;
			nextStation = supplyStations.get(supplyStations.indexOf(nextStation) + 1);
		}

		if (!sub.isAlive()) {
			Assets.submarineSFX1.setVolume(0f);
			gsm.setState(States.GAMEOVER);
		}

	}

	@Override
	public void render() {
		g.drawImage(Assets.blank, new Vector2f(0, 0), new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2),
				new Vector2f(0), new Color(100, 85, 76, 1));
		g.drawImage(Assets.panel, new Vector2f(0, 0), new Vector2f(128f), new Vector2f(0), new Color(255, 255, 255, 1));
		g.drawImage(Assets.screen, new Vector2f(0, 0), new Vector2f(92f), new Vector2f(0), new Color(255, 255, 255, 1));

		if (currentState < 4) {
			drawCamera();
		} else {
			drawSonar();
		}

		drawStats();

		g.drawImage(Assets.dark1, new Vector2f(0, 0), new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2),
				new Vector2f(0), new Color(100, 85, 76, 1));
	}

	private void handleSubstates() {
		if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
			currentState++;
			if (currentState > states.size() - 1) {
				currentState = 0;
			}
		} else if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
			currentState--;
			if (currentState < 0) {
				currentState = states.size() - 1;
			}
		}
	}

	private void handleAudio() {
		if (sonarScale == 0) {
			Assets.sonarSFX.play(Assets.sonarPing);
		}

		Assets.submarineSFX1.setVolume(6f * sub.getVelocity().length());

		if (!Assets.submarineSFX2.isPlaying() && rand.nextInt(1000) > 990) {

			float theta = (float) (rand.nextFloat() * 2 * Math.PI);
			float phi = (float) (rand.nextFloat() * 2 * Math.PI);

			ambientDir = new Vector3f((float) (Math.cos(theta) * ambientDir.x),
					(float) (Math.sin(theta) * ambientDir.y), (float) (Math.cos(phi) * ambientDir.z));

			Assets.submarineSFX2.setPosition3D(ambientDir);

			if (rand.nextInt(10) > 5) {
				Assets.submarineSFX2.play(Assets.subambient0);
			} else {
				Assets.submarineSFX2.play(Assets.subambient1);
			}
		}
		
	}

	private void drawStats() {
		g.drawImage(Assets.screen, new Vector2f(0, 65), new Vector2f(128, 16), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawImage(Assets.screen, new Vector2f(0, -65), new Vector2f(128, 16), new Vector2f(0),
				new Color(255, 255, 255, 1));

		g.drawImage(Assets.screen, new Vector2f(64, 45), new Vector2f(32, 16), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawString(Assets.font, "Oxygen", new Vector2f(64, 45), new Vector2f(4), new Color(200, 174, 146, 1),
				g.FONT_CENTER);

		g.drawImage(Assets.gaugeFace, new Vector2f(64, 20), new Vector2f(32), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawImage(Assets.needle, new Vector2f(64, 20), new Vector2f(32),
				new Vector2f(DragonMath.percentToTheta((float) Math.ceil(sub.getOxygen())), 0),
				new Color(255, 255, 255, 1));

		g.drawImage(Assets.screen, new Vector2f(64, -10), new Vector2f(32, 16), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawString(Assets.font, "Power", new Vector2f(64, -10), new Vector2f(4), new Color(200, 174, 146, 1),
				g.FONT_CENTER);

		g.drawImage(Assets.gaugeFace, new Vector2f(64, -35), new Vector2f(32), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawImage(Assets.needle, new Vector2f(64, -35), new Vector2f(32),
				new Vector2f(DragonMath.percentToTheta((float) Math.ceil(sub.getPower())), 0),
				new Color(255, 255, 255, 1));

		g.drawImage(Assets.screen, new Vector2f(-64, 45), new Vector2f(32, 16), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawString(Assets.font, "Lights", new Vector2f(-64, 45), new Vector2f(4), new Color(200, 174, 146, 1),
				g.FONT_CENTER);

		g.drawImage(Assets.screen, new Vector2f(-64, 28), new Vector2f(32, 16), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawString(Assets.font, (sub.isLights() ? "ON" : "OFF"), new Vector2f(-64, 28), new Vector2f(4),
				new Color(200, 174, 146, 1), g.FONT_CENTER);

		g.drawImage(Assets.screen, new Vector2f(-64, -10), new Vector2f(32, 16), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawString(Assets.font, "Hull", new Vector2f(-64, -10), new Vector2f(4), new Color(200, 174, 146, 1),
				g.FONT_CENTER);

		g.drawImage(Assets.screen, new Vector2f(-64, -27), new Vector2f(32, 16), new Vector2f(0),
				new Color(255, 255, 255, 1));
		g.drawString(Assets.font, DragonMath.evaluateIntegrity((int) Math.ceil(sub.getIntegrity())),
				new Vector2f(-64, -27), new Vector2f(3), new Color(200, 174, 146, 1), g.FONT_CENTER);

		g.drawString(Assets.font, states.get(currentState), new Vector2f(0, 64), new Vector2f(6),
				new Color(200, 174, 146, 1), g.FONT_CENTER);
		g.drawString(Assets.font,
				(int) sub.getDepth() + " m" + " - "
						+ biomes.get(map[(int) sub.getPosition().x][-1 * (int) sub.getPosition().y]),
				new Vector2f(0, -64), new Vector2f(6), new Color(200, 174, 146, 1), g.FONT_CENTER);
	}

	private void drawSonar() {
		float sonarLight = 1 - (sonarScale / maxSonarScale);

		g.drawImage(Assets.sonarDot, new Vector2f(0, 0), new Vector2f(2), new Vector2f(0), new Color(128, 160, 128, 1));

		g.drawImage(Assets.sonarRing, new Vector2f(0, 0), new Vector2f(maxSonarScale), new Vector2f(0),
				new Color(128, 160, 128, .4f));

		g.drawImage(Assets.sonarRing, new Vector2f(0, 0), new Vector2f(maxSonarScale / 2), new Vector2f(0),
				new Color(128, 160, 128, .4f));

		g.drawImage(Assets.sonarRing, new Vector2f(0, 0), new Vector2f(sonarScale), new Vector2f(0),
				new Color(128, 160, 128, sonarLight));

		for (AABB b : sonarBounds) {
			g.drawImage(Assets.blank, b.getCenter().sub(sub.getPosition(), new Vector2f()).mul(2),
					b.getScale().mul(2, new Vector2f()), new Vector2f(0, 0), new Color(128, 160, 128, sonarLight));
		}

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
					new Vector2f((float) (maxSonarScale / 2 * Math.cos(stationLoc)),
							(float) (maxSonarScale / 2 * Math.sin(stationLoc))),
					new Vector2f(2), new Vector2f(0), new Color(255, 0, 255, 1));
		}
		
		if (Math.abs(sub.getPosition().x - nextStation.getPosition().x) > 20
				|| Math.abs(sub.getPosition().y - nextStation.getPosition().y) > 20) {
			double stationLoc = Math.atan((sub.getPosition().y - nextStation.getPosition().y)
					/ (sub.getPosition().x - nextStation.getPosition().x));
			
			System.out.println("Station: " + nextStation.getPosition());
			System.out.println("Sub: " + sub.getPosition());
			
			
			if (sub.getPosition().y - nextStation.getPosition().y > 0
					&& sub.getPosition().x - nextStation.getPosition().x > 0) {
				stationLoc -= Math.PI;
			}
			
			if (sub.getPosition().y - nextStation.getPosition().y < 0
					&& sub.getPosition().x - nextStation.getPosition().x > 0) {
				stationLoc += Math.PI;
			}

			g.drawImage(Assets.blank,
					new Vector2f((float) (maxSonarScale / 2 * Math.cos(stationLoc)),
							(float) (maxSonarScale / 2 * Math.sin(stationLoc))),
					new Vector2f(2), new Vector2f(0), new Color(0, 255, 255, 1));
		}

		for (SupplyStation station : sonarStations) {
			station.render(g, sub.getPosition(), sonarLight);
		}
	}

	private void drawCamera() {
		g.drawMode(g.DRAW_SCREEN);

		Terrain.render(g, sub, bounds, currentState);

		g.drawImage(Assets.water, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
				new Color(255, 255, 255, sub.getDistance("UP")));

		if (currentState == 0) {
			g.drawImage(Assets.bubbleUpAnim.getTexture(), new Vector2f(0, -2), new Vector2f(50), new Vector2f(0),
					new Color(255, 255, 255, 0.5f));
		} else if (currentState == 1) {
			g.drawImage(Assets.bubbleDownAnim.getTexture(), new Vector2f(0, -2), new Vector2f(50), new Vector2f(0),
					new Color(255, 255, 255, 0.5f));
		} else {
			g.drawImage(Assets.bubbleAnim.getTexture(), new Vector2f(0, -2), new Vector2f(50), new Vector2f(0),
					new Color(255, 255, 255, 0.5f));
		}
		
		g.drawImage(Assets.leechAnim.getTexture(), new Vector2f(0, 0), new Vector2f(50), new Vector2f(0),
				new Color(255, 255, 255, 1));

		g.drawImage(sub.isLights() ? Assets.dark1 : Assets.dark0, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
				new Color(255, 255, 255, sub.calculateLight()));
		g.drawImage(Assets.lens, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255, 255, 255, 1));
	}

	public Submarine getSub() {
		return sub;
	}
	
	public boolean respawnable() {
		if(respawn != null) {
			return true;
		} else {
			return false;
		}
	}
}
