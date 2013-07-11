package com.homeguard;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.homeguard.Touchable.State;

public class Asteroid extends Touchable{
	
	private IUpdateHandler updateHandler;
	
	private float width = 200;
	private float height = 200;
	
	private float initialAngle;
	
	private float minResources = 100;
	private float maxResources = 500 - minResources;
	
	private float minSpeed = 0.25f;
	private float maxSpeed = 1 - minSpeed;
	
	private float rotationSpeed = -0.75f + (float) Math.random() * 1.5f;
	
	public Asteroid(float x, float y){
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();
		
		type = Type.ASTEROID;
		
		position = new Vector2(x,y);
		
		initialAngle =  MainActivity.getAngleToPoint(position.x, position.y, scene.playerMothership.getX(), scene.playerMothership.getY()) - 0.6f + (float) Math.random() * 1.2f;
		
		if(initialAngle < 0 && initialAngle > -0.2f) initialAngle -= 0.2f;
		if(initialAngle > 0 && initialAngle < 0.2f) initialAngle += 0.2f;
		
		
		velocity = MainActivity.getUnitVector(initialAngle).mul(minSpeed + (float) Math.random() * maxSpeed);
		
		init();
		
		hullHealth = maxHullHealth;
		shieldHealth = maxShieldHealth;
		
		scene.attachChild(face);				
		scene.addAsteroidTouchable(this);
		
		updateHandler = new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				//if the asteroid is out of bounds, kill it
				if (position.x < -scene.gameplayWidth/2 - 200 || 
					position.x > scene.gameplayHeight/2 + 200 ||
					position.y < -scene.gameplayHeight/2 - 200 ||
					position.y > scene.gameplayHeight/2 + 200)
				{
						destroy();
				}
				
				//perform calculations for hull and shield health
				if (hullHealth <= 0){
					zeroHealth = true;
					hullHealth = 0;
					destroy();
				}
				
				position.add(velocity);
				
				face.setPosition(position.x - width/2, position.y - height/2);
				face.setRotation(face.getRotation() + rotationSpeed);
				face.setScale(hullHealth / maxResources);	
			}	
		};
		scene.registerUpdateHandler(updateHandler);
		
	}

	public void init(){
		
		//hullHealth becomes our resource amount
		maxHullHealth = minResources + (float) Math.random() * maxResources;
		//automatically has no health because yeah
		maxShieldHealth = 1;
		
		face = new Sprite(position.x - width/2, position.y - height/2, width, height, activity.mTexture_asteroid1, activity.vertexBufferObjectManager);
		
		hullHealth = maxHullHealth;
		shieldHealth = 0;
	}
	
	public void mine(int amount, float efficiency){
		scene.addResources((int) (amount * efficiency));
		hullHealth -= amount;
	}
	
	@Override
	public void destroy() {
		zeroHealth = true;
		
		face.setIgnoreUpdate(true);
		face.setVisible(false);
		face.clearEntityModifiers();
		face.clearUpdateHandlers();
		
		scene.detachChild(face);
		face.detachSelf();
		
		scene.unregisterUpdateHandler(updateHandler);
		
		if (this == scene.getSelected()){
			scene.setNullTouchState();
		}

		for (int i = 0; i < scene.touchableList.size(); i++){
			if (scene.touchableList.get(i) == this){
				scene.touchableList.remove(i);
			}
		}
		for (int i = 0; i < scene.touchableListAsteroid.size(); i++){
			if (scene.touchableListAsteroid.get(i) == this){
				scene.touchableListAsteroid.remove(i);
			}
		}
		
	}

	@Override
	public void move(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attack(Touchable target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void damage(int hullDamage, int ShieldDamage) {
		// TODO Auto-generated method stub
	}
}
