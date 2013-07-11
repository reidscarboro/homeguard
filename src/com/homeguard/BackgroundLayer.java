package com.homeguard;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.badlogic.gdx.math.Vector2;

public class BackgroundLayer {
	
	MainActivity activity;
	GameScene scene;
	
	private float depth;
	
	private Sprite sprite;
	
	public BackgroundLayer(ITextureRegion texture, float depth){
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();
		
		this.depth = depth;
		
		sprite = new Sprite(-2048, -2048, 4096, 4096, texture, activity.vertexBufferObjectManager);
		
		scene.attachChild(sprite);

	}
	public void move(float x, float y){
		sprite.setPosition(-2048 + x / depth, -2048 + y / depth);
	}
	

}
