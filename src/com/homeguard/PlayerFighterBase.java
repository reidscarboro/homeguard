package com.homeguard;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import com.badlogic.gdx.math.Vector2;

public class PlayerFighterBase extends Touchable{
	
	
	protected State state;
	
	protected IUpdateHandler updateHandler;
	
	//set some initial values, these are subject to change
	protected float speed;
	protected float maxVelocity;
	protected float acceleration;
	protected float slowRate = 0.98f;
	
	//used to calculate movement and also shooting
	protected Vector2 toTarget;
	
	//shooting calculations
	protected float angleTemp;
	protected float accuracy; //smaller value = more accurate
	
	//shoots every bulletTimerLength number of updates
	protected int bulletTimer = 0;
	protected int bulletTimerLength;

	protected float shieldRegenRate;
	
	protected float barWidth = 100;
	protected float barHeight = 6;
	
	protected Rectangle hullHealthBar;
	protected Rectangle shieldHealthBar;
	
	protected float width;
	protected float height;
	
	protected float toX;
	protected float toY;
	
	protected float distanceToMove;
	
	protected int aggroRange2;
	protected int indexOfClosest = 0;
	protected float distanceToClosest2 = 0;
	protected boolean foundTarget = false;
	
	protected Touchable target;
	
	public PlayerFighterBase(float x, float y){
		
		type = Type.FRIENDLY_FIGHTER;
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();

		//set initial type to movable and state to stationary
		isMovable = true;
		state = State.STATIONARY;
		
		//define initial velocity and position
		position = new Vector2(x,y);
		velocity = new Vector2(0,0);
		
		//set initial values
		init();
		
		hullHealth = maxHullHealth;
		shieldHealth = maxShieldHealth;
		
		//create graphics	
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
		
		scene.addFriendlyTouchable(this);
		
		updateHandler = new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				//calculate gun location every frame to see where to spawn bullets
				final float[] gunCoordinates = face.convertLocalToSceneCoordinates(face.getWidth()/2, -face.getHeight()/6);
				final float gunX = gunCoordinates[0];
				final float gunY = gunCoordinates[1];
				
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
				
				if (bulletTimer > 0)
					bulletTimer--;
				
				//turn off attacking if I kill the target
				if (state == State.ATTACKING && target.zeroHealth == true){
					state = State.SLOWING;
				}
				
				//if I am attacking and the target is still alive
				if (state == State.ATTACKING && target.zeroHealth == false){
					
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

					if (bulletTimer <= 0 && MainActivity.getDistance2(getX(), getY(), target.getX(), target.getY()) < aggroRange2){
						
						//we will change this value based on what type of fighter we have
						//will eventually have the option of shooting bullets, lasers, missles, and whatever else
						shoot(gunX, gunY, target, toTarget.nor());
					}
					
				}
				if (state == State.MOVING){
					
					toTarget = new Vector2(toX, toY).sub(position);
					
					//distance formula to find distance between current location and final location
					distanceToMove = toTarget.len();

					//if the object has less than 2 units to move set object stationary, toLocation has been reached
					if (distanceToMove < 10){
						state = State.STATIONARY;
						velocity  = MainActivity.ZERO_VECTOR;
					}
					
					//accelerate towards point
					velocity.add(toTarget.nor().mul(acceleration));				
					
					//get the magnitude of the velocity vector
					speed = velocity.len();
					
					//decelerate based on distance to point, divided by a factor of (1/x)
					velocity.mul(1 / ((3/distanceToMove) + 1));
					
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
					
					velocity.mul(slowRate);
					if (velocity.len2() < 0.2f){
						state = State.STATIONARY;
					}
					hullHealthBar.setPosition(position.x-barWidth/2, position.y+height/1.5f);
					shieldHealthBar.setPosition(position.x-barWidth/2, position.y+height/1.5f+barHeight);
					
					position.add(velocity);
					face.setPosition(position.x - width/2, position.y - height/2);
					
					findTarget();
				}
				if (state == State.STATIONARY){
					velocity = new Vector2(0,0);					
					findTarget();
				}
					
			}	
		};
		scene.registerUpdateHandler(updateHandler);
	}
	
	//shoot, passing the bullet x and y origin, the target, and the angle to the target
	public void shoot(float x, float y, Touchable enemyTarget, Vector2 bulletAngleVector){
		FriendlyBullet bullet = FriendlyBulletPool.getBullet();

		angleTemp = MainActivity.getVectorAngle(bulletAngleVector) + (float)Math.random() * accuracy - accuracy/2;
		
		bullet.set(new Vector2(x, y), new Vector2((float) Math.cos(angleTemp), (float) Math.sin(angleTemp)));	
		bulletTimer = bulletTimerLength;
	}
	
	//these values are subject to change based on the type of ship we have
	//when we upgrade, these are the things that will change
	//speed, aggro range, shoot speed, health, etc.
	public void init(){

		//get size of ship
		width = 128;
		height = 128;
		
		face = new Sprite(position.x - width/2, position.y - height/2, width, height, activity.mTexture_friendly_fighter1, activity.vertexBufferObjectManager);

		upgradeClass = UpgradeClass.FRIENDLY_FIGHTER_1;
		
		aggroRange2 = 1000 * 1000; //aggro range squared
		bulletTimerLength = 12; //bullet cooldown
		accuracy = 0.5f; //smaller value = more accurate
		shieldRegenRate = 0.1f;
		maxHullHealth = 100;
		maxShieldHealth = 100;
		maxVelocity = 4;
		acceleration = .06f;
	}
	
	public void findTarget(){
		
		//we have not found a target yet, and the range to "beat" is that of the aggro range
		foundTarget = false;
		distanceToClosest2 = aggroRange2;
		indexOfClosest = 0;
		
		//loop through enemies on screen, if a closer target is found, set found target to true and set appropriate
		//index of found target
		for (int i = 0; i < scene.touchableListEnemy.size(); i++){
			if (MainActivity.getDistance2(getX(), getY(), scene.touchableListEnemy.get(i).getX(), scene.touchableListEnemy.get(i).getY()) < distanceToClosest2){
				distanceToClosest2 = MainActivity.getDistance2(getX(), getY(), scene.touchableListEnemy.get(i).getX(), scene.touchableListEnemy.get(i).getY());
				indexOfClosest = i;
				foundTarget = true;
			}
		}//
		
		//if we found a target, set it as target and set to attacking state
		if (foundTarget){
			target = scene.touchableListEnemy.get(indexOfClosest);
			state = State.ATTACKING;
		}
	}
	
	@Override
	public void move(float x, float y) {
		
		toX = x;
		toY = y;
		
		state = State.MOVING;

		deselect();
		
	}

	@Override
	public void attack(Touchable target) {
		this.target = target;
		state = State.ATTACKING;
		
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
	public void destroy() {
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
		
		for (int i = 0; i < scene.touchableListFriendly.size(); i++){
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
