package com.theinvader360.arenaroamer;

import com.badlogic.gdx.Screen;

public class GameScreen implements Screen {

	private ArenaRoamer game;
	private World world;
	private WorldRenderer2D renderer2D;
	private WorldRenderer3D renderer3D;
	
	public GameScreen(ArenaRoamer game) {
		this.game = game;
		world = new World(game);
		renderer2D = new WorldRenderer2D(world);
		renderer3D = new WorldRenderer3D(world);
	}

	@Override
	public void render(float delta) {
		world.update(delta);
		if (ArenaRoamer.RENDER_3D) renderer3D.render();
		else renderer2D.render();
	}
	
	@Override public void resize(int width, int height) {}
	@Override public void show() {}
	@Override public void hide() {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}
}
