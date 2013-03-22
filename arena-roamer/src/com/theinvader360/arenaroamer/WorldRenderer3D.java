package com.theinvader360.arenaroamer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.theinvader360.arenaroamer.Door.DoorState;

public class WorldRenderer3D {

	private World world;
	private PerspectiveCamera camera;
	private Mesh cube;
	private Mesh gem;
	private Mesh key;
	private Texture floorTexture;
	private Texture wallTexture;
	private Texture exitTexture;
	private Texture lockedDoorTexture;
	private Texture gemTexture;
	private Texture keyTexture;
	private int gemRotation = 0;
	private OrthographicCamera hudCam;
	private SpriteBatch hudBatch;
	private BitmapFont hudFont;

	public WorldRenderer3D(World world) {
		this.world = world;
		cube = ObjLoader.loadObj(Gdx.files.internal("models/cube.obj").read());
		gem = ObjLoader.loadObj(Gdx.files.internal("models/gem.obj").read());
		key = ObjLoader.loadObj(Gdx.files.internal("models/dtKey.obj").read());
		floorTexture = new Texture(Gdx.files.internal("textures/grass.png"), true);
		floorTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		wallTexture = new Texture(Gdx.files.internal("textures/wall.png"), true);
		wallTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		exitTexture = new Texture(Gdx.files.internal("textures/exit.png"), true);
		exitTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		lockedDoorTexture = new Texture(Gdx.files.internal("textures/locked.png"), true);
		lockedDoorTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		gemTexture = new Texture(Gdx.files.internal("textures/gem.png"), true);
		gemTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		keyTexture = new Texture(Gdx.files.internal("textures/key.png"), true);
		keyTexture.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		camera = new PerspectiveCamera(70, 6f, 4f);
		camera.near = 0.01f;
		camera.direction.set(0, 2, -1);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, ArenaRoamer.WIDTH, ArenaRoamer.HEIGHT);
		hudBatch = new SpriteBatch();
		hudFont = new BitmapFont();
	}
	
	public void render() {
		renderPlayArea();
		renderHud();
	}
	
	// Useful links:
	// http://www.khronos.org/opengles/documentation/opengles1_0/html/
	// https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/ObjTest.java
	// https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/Pong.java
	// http://www.colorcodehex.com/afeeee/
	// http://www.youtube.com/watch?v=FI859_0-rrU
	// http://www.katsbits.com/tutorials/blender/learning-unwrapping-uvw-maps.php
	private void renderPlayArea() {
		if (gemRotation < 360) gemRotation++;
		else gemRotation = 0;
	
		camera.position.set(world.getPlayer().getCentrePos().x * 2f, (world.getPlayer().getCentrePos().y * 2f) -0, 0.75f);
		camera.rotate(world.getPlayer().getRotation(), 0, 0, 1);
		camera.update();
		camera.rotate(-world.getPlayer().getRotation(), 0, 0, 1);
		GL10 gl = Gdx.graphics.getGL10();
		gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gl.glClearColor(0.63686f, 0.76436f, 0.92286f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glLoadMatrixf(camera.projection.val, 0);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadMatrixf(camera.view.val, 0);

		for (int i = 0; i < world.getMap().getMap().size; i++) {
			for (int j = 0; j < world.getMap().getMap().get(i).length(); j++) {
				gl.glPushMatrix();
				gl.glTranslatef((j-1) * 2f, i * 2f, -2);
				gl.glScalef(1.0f, 1.0f, 1.0f);
				floorTexture.bind();
				cube.render(GL10.GL_TRIANGLES);
				gl.glPopMatrix();
			}
		}
				
		for(int i=0; i < world.getWalls().size; i++ ) {
			gl.glPushMatrix();
			gl.glTranslatef(world.getWalls().get(i).centrePosX * 2f, world.getWalls().get(i).centrePosY * 2f, 0);
			gl.glScalef(world.getWalls().get(i).width, world.getWalls().get(i).height, 1.0f);
			wallTexture.bind();
			cube.render(GL10.GL_TRIANGLES);
			gl.glPopMatrix();			
    	}
		
		for(int i=0; i < world.getExits().size; i++ ) {
			gl.glPushMatrix();
			gl.glTranslatef(world.getExits().get(i).centrePosX * 2f, world.getExits().get(i).centrePosY * 2f, 0);
			gl.glScalef(world.getExits().get(i).width, world.getExits().get(i).height, 1.0f);
			exitTexture.bind();
			cube.render(GL10.GL_TRIANGLES);
			gl.glPopMatrix();			
		}

		for(int i=0; i < world.getDoors().size; i++ ) {
			if (world.getDoors().get(i).state.equals(DoorState.LOCKED)) {
				gl.glPushMatrix();
				gl.glTranslatef(world.getDoors().get(i).centrePosX * 2f, world.getDoors().get(i).centrePosY * 2f, 0);
				gl.glScalef(world.getDoors().get(i).width, world.getDoors().get(i).height, 1.0f);
				lockedDoorTexture.bind();
				cube.render(GL10.GL_TRIANGLES);
				gl.glPopMatrix();			
			}
    	}

		for(int i=0; i < world.getKeys().size; i++ ) {
			gl.glPushMatrix();
			gl.glTranslatef(world.getKeys().get(i).centrePosX * 2f, world.getKeys().get(i).centrePosY * 2f, 0);
			gl.glScalef(world.getKeys().get(i).width * 0.5f, world.getKeys().get(i).height * 0.5f, 0.25f);
			gl.glRotatef(180, 1, 0, 0);
			gl.glRotatef(gemRotation, 0, 0, 1);
			keyTexture.bind();
			key.render(GL10.GL_TRIANGLES);
			gl.glPopMatrix();
		}

		for(int i=0; i < world.getGems().size; i++ ) {
			gl.glPushMatrix();
			gl.glTranslatef(world.getGems().get(i).centrePosX * 2f, world.getGems().get(i).centrePosY * 2f, 0f);
			gl.glScalef(world.getGems().get(i).width, world.getGems().get(i).height, 0.5f);
			gl.glRotatef(90, 1, 0, 0);
			gl.glRotatef(gemRotation, 0, 1, 0);
			gemTexture.bind();
			gem.render(GL10.GL_TRIANGLES);
			gl.glPopMatrix();
		}
	}
	
	private void renderHud() {
		hudCam.position.set(ArenaRoamer.WIDTH/2, ArenaRoamer.HEIGHT/2, 0.0f);
	    hudCam.update();
	    hudCam.apply(Gdx.gl10);
	    hudBatch.setProjectionMatrix(hudCam.combined);
	    hudBatch.begin();
	    hudFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	    hudFont.draw(hudBatch, "Level: "+world.getLevel(), 15, 305);
	    hudFont.draw(hudBatch, "Keys Held: "+world.getInventoryKeys(), 15, 275);
	    hudFont.draw(hudBatch, "Remaining Gems: "+world.getGems().size, 15, 260);
	    hudFont.draw(hudBatch, "Remaining Keys: "+world.getKeys().size, 15, 245);
	    hudBatch.end();
	}
}
