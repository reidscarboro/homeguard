package com.homeguard;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.math.Vector2;

public class EnemyFighter1 extends Touchable{
	
	
	private float speed;
	private float maxVelocity = 2;
	private float acceleration = .004f;
	private Vector2 toTarget = new Vector2();
	
	
	private float shieldRegenRate = 0.1f;
	
	private int barWidth = 100;
	private int barHeight = 6;
	
	private Rectangle hullHealthBar;
	private Rectangle shieldHealthBar;
	
	private float width;
	private float height;
	
	private State state;
	
	private IUpdateHandler updateHandler;
	
	private Touchable target;
	
	public EnemyFighter1(float x, float y){
		
		type = Type.ENEMY;
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();
		
		//set type to movable and state to stationary
		isMovable = true;
		state = State.STATIONARY;
		
		//get size of ship
		width = 128;
		height = 128;
		
		//set position and initial velocity
		position = new Vector2(x,y);
		//velocity = new Vector2(0,0);
		velocity = new Vector2((float) Math.random() * 1 - .5f, (float) Math.random() * 1 - .5f);
		
		maxHullHealth = 100;
		maxShieldHealth = 200;
		
		hullHealth = maxHullHealth;
		shieldHealth = maxShieldHealth;
		
		//create body and face
		face = new Sprite(position.x - width/2, position.y - height/2, width, height, activity.mTexture_enemy_fighter1, activity.vertexBufferObjectManager);
		hullHealthBar = new Rectangle(position.x-width/2, position.y+height/1.5f, barWidth, barHeight, activity.vertexBufferObjectManager);
		shieldHealthBar = new Rectangle(position.x-width/2, position.y+height/1.5f+barHeight, barWidth, barHeight, activity.vertexBufferObjectManager);
		
		hullHealthBar.setColor(0, 0.8f, 0);
		hullHealthBar.setAlpha(0.5f);
		shieldHealthBar.setColor(0.5f, 0.5f, 1);
		shieldHealthBar.setAlpha(0.5f);
		
		//add to the world
		scene.attachChild(hullHealthBar);
		scene.attachChild(shieldHealthBar);
		scene.attachChild(face);
		scene.addEnemyTouchable(this);
		
		attack(scene.playerMothership);
				
		updateHandler = new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				//perform calculations for hull and shield health
				if (hullHealth <= 0){
					zeroHealth = true;
					hullHealth = 0;
					destroy();
				}
				if (shieldHealth < 0){
					shieldHealth = 0;
				}
				if (shieldHealth > maxShieldHealth){
					shieldHealth = maxShieldHealth;
				}
				if(shieldHealth > 0 && shieldHealth < maxShieldHealth){
					shieldHealth += shieldRegenRate;
				}
				
				hullHealthBar.setWidth(barWidth * (hullHealth / maxHullHealth));
				shieldHealthBar.setWidth(barWidth * (shieldHealth / maxShieldHealth));
				
				if (state == State.ATTACKING){
					
					
					toTarget = new Vector2(target.getX(), target.getY()).sub(position);
					
					//accelerate towards point
					velocity.add(toTarget.nor().mul(acceleration));				
					
					//get the magnitude of the velocity vector
					speed = velocity.len();

					//slow down if we have exceeded max speed
					if(speed > maxVelocity){
						velocity = velocity.mul(maxVelocity / speed);
					}
					
					position.add(velocity);
					
					face.setPosition(position.x - width/2, position.y - height/2);
					face.setRotation((MainActivity.getVectorAngle(toTarget) * (180 / (float) Math.PI)) + 90);
					
					hullHealthBar.setPosition(position.x-barWidth/2, position.y+height/1.5f);
					shieldHealthBar.setPosition(position.x-barWidth/2, position.y+height/1.5f+barHeight);
				}

				if (state == State.SLOWING){
					
				}
				if (state == State.STATIONARY){
					velocity = MainActivity.ZERO_VECTOR;
				}
					
			}	
		};
	
		scene.registerUpdateHandler(updateHandler);
	}
	
	
	@Override
	public void damage(int hullDamage, int shieldDamage){
		if (shieldHealth > 0){
			shieldHealth -= shieldDamage;
		}
		else{
			hullHealth -= hullDamage;
		}
	}
	
	@Override
	public void attack(Touchable target){
		this.target = target;
		state = State.ATTACKING;
	}

	@Override
	public void move(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		
		scene.addScore(100);
		
		face.setIgnoreUpdate(true);
		face.setVisible(false);
		face.clearEntityModifiers();
		face.clearUpdateHandlers();
		
		hullHealthBar.setIgnoreUpdate(true);
		hullHealthBar.setVisible(false);
		hullHealthBar.clearEntityModifiers();
		hullHealthBar.clearUpdateHandlers();
		
		shieldHealthBar.setIgnoreUpdate(true);
		shieldHealthBar.setVisible(false);
		shieldHealthBar.clearEntityModifiers();
		shieldHealthBar.clearUpdateHandlers();
		
		scene.detachChild(face);
		scene.detachChild(hullHealthBar);
		scene.detachChild(shieldHealthBar);

		face.detachSelf();
		hullHealthBar.detachSelf();
		shieldHealthBar.detachSelf();
		
		scene.unregisterUpdateHandler(updateHandler);
		
		if (this == scene.getSelected()){
			scene.setNullTouchState();
		}
		
		for (int i = 0; i < scene.touchableListEnemy.size(); i++){
			if (scene.touchableListEnemy.get(i) == this){
				scene.touchableListEnemy.remove(i);
			}
		}
		for (int i = 0; i < scene.touchableList.size(); i++){
			if (scene.touchableList.get(i) == this){
				scene.touchableList.remove(i);
			}
		}

	}


	@Override
	public void mine(int amount, float efficiency) {
		// TODO Auto-generated method stub
		
	}
}
