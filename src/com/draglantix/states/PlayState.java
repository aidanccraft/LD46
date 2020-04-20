package com.draglantix.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.draglantix.entities.Leech;
import com.draglantix.entities.Submarine;
import com.draglantix.flare.collision.AABB;
import com.draglantix.flare.graphics.Graphics;
import com.draglantix.flare.util.Color;
import com.draglantix.flare.window.Window;
import com.draglantix.main.Assets;
import com.draglantix.terrain.StationHandler;
import com.draglantix.terrain.SupplyStation;
import com.draglantix.terrain.Terrain;
import com.draglantix.utils.DragonMath;
import com.draglantix.utils.ImageDecoder;
import com.draglantix.utils.Quad;
import com.draglantix.utils.QuadTree;

public class PlayState extends GameState {

	private Submarine sub;

	private static Random rand = new Random();

	private Vector3f ambientDir = new Vector3f(1, 0, 0);

	private float sonarScale = 0;
	private float maxSonarScale = 80;

	private int[][] map;

	private Map<Integer, String> states = new HashMap<Integer, String>();

	private static Map<Integer, Integer> events = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> nextEvents = new HashMap<Integer, Integer>();
	private float[] eventAlpha = new float[] {0f, 0f, 0f, 0f};
	private boolean[] eventIn = new boolean[] {true, true, true, true};
	
	private static int currentState;

	private int previousState;
	private int switchableStates = 4;

	private Map<Integer, String> biomes = new HashMap<Integer, String>();

	public static List<AABB> bounds = new ArrayList<AABB>();
	private List<AABB> sonarBounds = new ArrayList<AABB>();
	private List<SupplyStation> sonarStations = new ArrayList<SupplyStation>();

	private List<Leech> leeches = new ArrayList<Leech>();

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
		states.put(5, "STATION");

		events.put(0, 0); //Window, Event
		events.put(1, 0);
		events.put(2, 0);
		events.put(3, 0);
		
		nextEvents.put(0, 0); //Window, Event
		nextEvents.put(1, 0);
		nextEvents.put(2, 0);
		nextEvents.put(3, 0);

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
		
		StationHandler.init();

		Assets.submarineSFX0.setLooping(true);
		Assets.submarineSFX0.play(Assets.waterambient);

		Assets.submarineSFX1.setLooping(true);
		Assets.submarineSFX1.play(Assets.subengine);
	}

	public void respawn() {
		if (StationHandler.getRespawn() != null) {
			sub = new Submarine(new Vector2f(StationHandler.getRespawn().getPosition()), 0.2f);
		} else {
			sub = new Submarine(new Vector2f(33, -10), 0.2f);
		}
		currentState = 0;
	}

	@Override
	public void tick() {
		handleSubstates();

		handleAudio();

		if (getCurrentState() != switchableStates + 1) {
			sonarScale += .5f;
			if (sonarScale > maxSonarScale) {
				sonarScale = 0;
				this.sonarBounds.removeAll(sonarBounds);
				this.sonarStations.removeAll(sonarStations);
			}

			sub.update();

			PlayState.bounds = qt.query(new Quad(new Vector2f(sub.getPosition()), new Vector2f(5)));

			if (getCurrentState() == 4) {
				List<AABB> tmpsonar = qt.query(new Vector2f(sub.getPosition()), 20 * (sonarScale / maxSonarScale));
				for (AABB t : tmpsonar) {
					if (!this.sonarBounds.contains(t)) {
						this.sonarBounds.add(t);
					}
				}
				
				this.sonarStations = StationHandler.checkSonar(sub, sonarScale / maxSonarScale, sonarStations);

			} else {
				this.sonarBounds.removeAll(sonarBounds);
				this.sonarStations.removeAll(sonarStations);
			}

			handleCreatures();
			
			StationHandler.checkCollisions(sub, this);

			if (!sub.isAlive()) {
				fadeAllSources();
				leeches.removeAll(leeches);
				gsm.setState(States.GAMEOVER);
			}
		} else {
			StationHandler.tick();
			
			if(Window.getInput().isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
				PlayState.currentState = this.previousState;
				resumeAllSources();
			}
		}
		
		phaseEvents();

	}

	@Override
	public void render() {
		g.drawImage(Assets.blank, new Vector2f(0, 0), new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2),
				new Vector2f(0), new Color(100, 85, 76, 1));
		g.drawImage(Assets.panel, new Vector2f(0, 0), new Vector2f(128f), new Vector2f(0), new Color(255, 255, 255, 1));
		g.drawImage(Assets.screen, new Vector2f(0, 0), new Vector2f(92f), new Vector2f(0), new Color(255, 255, 255, 1));

		if (getCurrentState() < 4) {
			drawCamera();
		} else if (getCurrentState() == 5) {
			drawStation();
		} else {
			drawSonar();
		}

		drawStats();

		g.drawImage(Assets.dark1, new Vector2f(0, 0), new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2),
				new Vector2f(0), new Color(100, 85, 76, 1));
	}

	private void handleSubstates() {
		if (getCurrentState() != 5) {
			if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
				currentState = getCurrentState() + 1;
				if (getCurrentState() > switchableStates) {
					currentState = 0;
				}
			} else if (Window.getInput().isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
				currentState = getCurrentState() - 1;
				if (getCurrentState() < 0) {
					currentState = switchableStates;
				}
			}
		}
	}

	private void handleAudio() {

		if (Assets.music.isPlaying()) {
			Assets.music.setVolume(0);
		}

		if(getCurrentState() != 5) {
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
		} else {
			fadeAllSources();
		}
	}

	private void handleCreatures() {
		if (leeches.size() < 1) {
			leeches.add(new Leech(sub));
			System.out.println("ADDED LEECH");
		}

		for (int i = 0; i < leeches.size(); i++) {
			Leech l = leeches.get(i);
			l.update();
			if(l.dead) {
				leeches.remove(l);
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

		g.drawString(Assets.font, states.get(getCurrentState()), new Vector2f(0, 64), new Vector2f(6),
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

		StationHandler.drawSonar(g, sub, maxSonarScale);

		for (SupplyStation station : sonarStations) {
			station.render(g, sub.getPosition(), sonarLight);
		}

		for (Leech l : leeches) {
			l.render(g, 20 * (sonarScale / maxSonarScale), sonarLight);
		}
	}

	private void drawCamera() {
		g.drawMode(g.DRAW_SCREEN);

		Terrain.render(g, sub, bounds, getCurrentState());

		g.drawImage(Assets.water, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
				new Color(255, 255, 255, sub.getDistance("UP")));

		if (getCurrentState() == 0) {
			g.drawImage(Assets.bubbleUpAnim.getTexture(), new Vector2f(0, -2), new Vector2f(50), new Vector2f(0),
					new Color(255, 255, 255, 0.5f));
		} else if (getCurrentState() == 1) {
			g.drawImage(Assets.bubbleDownAnim.getTexture(), new Vector2f(0, -2), new Vector2f(50), new Vector2f(0),
					new Color(255, 255, 255, 0.5f));
		} else {
			g.drawImage(Assets.bubbleAnim.getTexture(), new Vector2f(0, -2), new Vector2f(50), new Vector2f(0),
					new Color(255, 255, 255, 0.5f));
		}
		
		drawWindowEvent(g);
		
		g.drawImage(sub.isLights() ? Assets.dark1 : Assets.dark0, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0),
				new Color(255, 255, 255, sub.calculateLight()));
		g.drawImage(Assets.lens, new Vector2f(0, 0), new Vector2f(64), new Vector2f(0), new Color(255, 255, 255, 1));
	}
	
	private void drawWindowEvent(Graphics g) {
		
		int event = events.get(getCurrentState());
		
		if(event == 1) {
			g.drawImage(Assets.leechAnim.getTexture(), new Vector2f(0, 0), new Vector2f(50), new Vector2f(0),
					new Color(255, 255, 255, eventAlpha[getCurrentState()]));
		}
	}
	
	public static int eventOpenWindow(int event) {
		List<Integer> open = new ArrayList<Integer>();
		for(int i = 0; i < 3; i++) {
			if(events.get(i) == 0 && nextEvents.get(i) == 0) {
				open.add(i);
			}
		}
		int selection = rand.nextInt(open.size());
		nextEvents.replace(selection, event);
		return selection;
	}
	
	public static void resetEvent(int returnEvent) {
		nextEvents.replace(returnEvent, 0);
	}
	
	private void phaseEvents() {
		for(int i = 0; i < 3; i++) {
			if(events.get(i) != nextEvents.get(i)) {
				if(eventIn[i]) {
					eventAlpha[i]+= 0.1f;
					if(eventAlpha[i] > 1) {
						eventAlpha[i] = 1;
						eventIn[i] = false;
						events.replace(i, nextEvents.get(i));
					}
				}else {
					eventAlpha[i]-= 0.01f;
					if(eventAlpha[i] < 0) {
						eventAlpha[i] = 0;
						eventIn[i] = true;
						events.replace(i, nextEvents.get(i));
					}
				}
			}
		}
	}

	private void drawStation() {
		g.drawMode(g.DRAW_SCREEN);
		StationHandler.renderText(g, this);
	}

	public Submarine getSub() {
		return sub;
	}

	public boolean respawnable() {
		if (StationHandler.getRespawn() != null) {
			return true;
		} else {
			return false;
		}
	}

	public void setState(int state) {
		this.previousState = getCurrentState();
		PlayState.currentState = state;
	}

	private void fadeAllSources() {

		Assets.submarineSFX0.setVolume(0);
		Assets.submarineSFX1.setVolume(0);
		Assets.submarineSFX2.setVolume(0);
		Assets.submarineSFX3.setVolume(0);
		Assets.sonarSFX.setVolume(0);
		Assets.submarineEngine.setVolume(0);

		for (Leech l : leeches) {
			l.fadeSFX(0);
		}

		Assets.music.setVolume(1);
	}
	
	private void resumeAllSources() {
		Assets.submarineSFX0.setLooping(true);
		Assets.submarineSFX0.play(Assets.waterambient);

		Assets.submarineSFX1.setLooping(true);
		Assets.submarineSFX1.play(Assets.subengine);

		Assets.submarineSFX0.setVolume(1);
		Assets.submarineSFX1.setVolume(1);
		Assets.submarineSFX2.setVolume(1);
		Assets.submarineSFX3.setVolume(1);
		Assets.sonarSFX.setVolume(1);
		Assets.submarineEngine.setVolume(1);
		for(Leech l : leeches) {
			l.resumeSFX();
		}
	}

	public static int getCurrentState() {
		return currentState;
	}
	
	public String getBiome(int biome) {
		return biomes.get(biome);
	}
}
