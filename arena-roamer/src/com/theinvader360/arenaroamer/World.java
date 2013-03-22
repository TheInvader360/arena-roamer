package com.theinvader360.arenaroamer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.theinvader360.arenaroamer.Door.DoorState;

public class World {
	
	private ArenaRoamer game;
	private Player player;
	private Map map = new Map();
	private Array<Wall> walls = new Array<Wall>();
	private Array<Exit> exits = new Array<Exit>();
	private Array<Gem> gems = new Array<Gem>();
	private Array<Door> doors = new Array<Door>();
	private Array<Key> keys = new Array<Key>();
	private int inventoryGems;
	private int inventoryKeys;
	private int currentLevel = 1;
	private int finalLevel = 3;

	public World(ArenaRoamer game) {
		this.game = game;
		player = new Player(this);
		generateLevel(currentLevel);
	}
	
	private void generateLevel(int levelNumber) {
		inventoryGems = 0;
		inventoryKeys = 0;
		map.load("Level"+levelNumber+".map");
		walls.clear();
		exits.clear();
		gems.clear();
		doors.clear();
		keys.clear();
		for (int y=0; y < map.getMap().size; y++) {
			for (int x=0; x < map.getMap().get(y).length(); x++) {
				if (map.getTile(x, y).equals("S")) {
					// Set start position
					player.getCentrePos().set(x, map.getMap().size-y);
				}
				if (map.getTile(x, y).equals("W")) {
					// Generate walls
					walls.add(new Wall(x, map.getMap().size-y, 1f, 1f));
				}
				if (map.getTile(x, y).equals("E")) {
					// Create exit
					exits.add(new Exit(x, map.getMap().size-y, 0.75f, 0.75f));
				}
				if (map.getTile(x, y).equals("T")) {
					// Generate gems
					gems.add(new Gem(x, map.getMap().size-y, 0.5f, 0.5f));
				}
				if (map.getTile(x, y).equals("U")) {
					// Generate unlocked doors
					doors.add(new Door(x, map.getMap().size-y, 1.0f, 1.0f, DoorState.CLOSED));
				}
				if (map.getTile(x, y).equals("L")) {
					// Generate locked doors
					doors.add(new Door(x, map.getMap().size-y, 1.0f, 1.0f, DoorState.LOCKED));
				}
				if (map.getTile(x, y).equals("K")) {
					// Generate keys
					keys.add(new Key(x, map.getMap().size-y, 0.5f, 0.5f));
				}
			}
		}
	}
	
	public void update(float delta) {
		handleInput(delta);
	}
	
	// Called by player.tryMove() - returns true if collision with blocking object, false if collision with non-blocking object
	public boolean collision() {
		for(int i=0; i < walls.size; i++ ) {
			if ((walls.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (walls.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (walls.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (walls.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) return true;
		}
		for(int i=0; i < exits.size; i++ ) {
			if ((exits.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (exits.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (exits.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (exits.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) {
				if (currentLevel != finalLevel) nextLevel();
				else restartGame();
			}
		}
		for(int i=0; i < gems.size; i++ ) {
			if ((gems.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (gems.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (gems.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (gems.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) {
				gems.removeIndex(i);
				addGem();
			}
		}
		for(int i=0; i < doors.size; i++ ) {
			if ((doors.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (doors.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (doors.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (doors.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) {
				if (doors.get(i).state.equals(DoorState.CLOSED)) doors.get(i).open();
				else if (doors.get(i).state.equals(DoorState.LOCKED)) {
					if (inventoryKeys == 0) return true;
					else {
						doors.get(i).unlock();
						removeKey();
					}
				}
			}
		}
		for(int i=0; i < keys.size; i++ ) {
			if ((keys.get(i).bounds.contains(player.getHitboxFrontRight().x, player.getHitboxFrontRight().y)) || (keys.get(i).bounds.contains(player.getHitboxBackRight().x, player.getHitboxBackRight().y)) || (keys.get(i).bounds.contains(player.getHitboxBackLeft().x, player.getHitboxBackLeft().y)) || (keys.get(i).bounds.contains(player.getHitboxFrontLeft().x, player.getHitboxFrontLeft().y))) {
				keys.removeIndex(i);
				addKey();
			}
		}
		return false;
	}

	private void handleInput(float delta) {
		// Android controls
		if (Gdx.app.getType() == ApplicationType.Android) {
			if (Gdx.input.getAccelerometerY() < -2) player.strafeLeft(delta);
			if (Gdx.input.getAccelerometerY() > 2) player.strafeRight(delta);
			if (Gdx.input.getAccelerometerX() < 7) player.moveForward(delta);
			if (Gdx.input.getAccelerometerX() > 9) player.moveBackward(delta);
			
			if (Gdx.input.isTouched()) {
				if (Gdx.input.getX() < ArenaRoamer.WIDTH / 2) player.turnLeft(delta);
				if (Gdx.input.getX() > ArenaRoamer.WIDTH / 2) player.turnRight(delta);
			}
		}

		// Desktop controls
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			if(Gdx.input.isKeyPressed(Input.Keys.UP)) player.moveForward(delta);
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) player.moveBackward(delta);
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.turnLeft(delta);
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.turnRight(delta);
			if(Gdx.input.isKeyPressed(Input.Keys.Z)) player.strafeLeft(delta);
			if(Gdx.input.isKeyPressed(Input.Keys.X)) player.strafeRight(delta);
			if(Gdx.input.isKeyPressed(Input.Keys.R)) restartLevel();
			if(Gdx.input.isKeyPressed(Input.Keys.Q)) restartGame();
		}
	}
	
	private void restartLevel() {
		player.setRotation(0.0f);
		generateLevel(currentLevel);
	}
	
	private void restartGame() {
		player.setRotation(0.0f);
		currentLevel = 1;
		generateLevel(currentLevel);
	}
	
	private void addGem() {
		inventoryGems++;
	}

	private void addKey() {
		inventoryKeys++;
	}

	private void removeKey() {
		inventoryKeys--;
	}
	
	private void nextLevel() {
		currentLevel++;
		player.setRotation(0.0f);
		generateLevel(currentLevel);
	}

	public ArenaRoamer getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	public Map getMap() {
		return map;
	}

	public Array<Wall> getWalls() {
		return walls;
	}

	public Array<Exit> getExits() {
		return exits;
	}

	public Array<Gem> getGems() {
		return gems;
	}

	public Array<Door> getDoors() {
		return doors;
	}

	public Array<Key> getKeys() {
		return keys;
	}
	
	public int getInventoryGems() {
		return inventoryGems;
	}

	public int getInventoryKeys() {
		return inventoryKeys;
	}

	public int getLevel() {
		return currentLevel;
	}
}
