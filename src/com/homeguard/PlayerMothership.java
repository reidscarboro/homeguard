package com.homeguard;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.homeguard.Touchable.State;

public class PlayerMothership extends Touchable{
	
	private int width;
	private int height;
	
	private IUpdateHandler updateHandler;
	
	public PlayerMothership(float x, float y){
		
		type = Type.MOTHERSHIP;
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();
		
		width = 512;
		height = 512;
		
		position = new Vector2(x,y);
		velocity = new Vector2(0,0);
		
		face = new Sprite(position.x - width/2, position.y - height/2, width, height, activity.mTexture_friendly_mothership, activity.vertexBufferObjectManager);
		scene.attachChild(face);
		
		scene.addFriendlyTouchable(this);
		
		updateHandler = new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				face.setRotation(face.getRotation() + 0.05f);//
					
			}	
		};
	
		scene.registerUpdateHandler(updateHandler);
	}

	@Override
	public void move(float x, float y) {
		
	}

	@Override
	public void attack(Touchable target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void damage(int hullDamage, int ShieldDamage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mine(int amount, float efficiency) {
		// TODO Auto-generated method stub
		
	}
}
