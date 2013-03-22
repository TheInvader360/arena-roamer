package com.theinvader360.arenaroamer;

import com.badlogic.gdx.Game;

public class ArenaRoamer extends Game {

	public static final boolean DEBUG = false;
	public static final boolean RENDER_3D = true;
	public static final int WIDTH = 480;
	public static final int HEIGHT = 320;
	public GameScreen gameScreen;
	
	@Override
	public void create() {
		gameScreen = new GameScreen(this);
		setScreen(gameScreen);
	}
}
