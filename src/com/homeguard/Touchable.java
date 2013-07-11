package com.homeguard;

import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.math.Vector2;

public abstract class Touchable {
	
	protected Sprite face;
	protected Type type;
	
	protected Vector2 position;
	protected Vector2 velocity;
	
	public float hullHealth;
	public float shieldHealth;
	
	public float maxHullHealth;
	public float maxShieldHealth;
	
	public boolean zeroHealth = false;
	
	boolean friendly = true;
	boolean isSelected;
	boolean isMovable = false; 
	
	public UpgradeClass upgradeClass = UpgradeClass.NULL;
	
	
	public MainActivity activity;
	public GameScene scene;
	
	public enum State{
		STATIONARY,
		MOVING,
		SLOWING,
		ATTACKING
	}
	
	public enum Type{
		MOTHERSHIP,
		ENEMY,
		FRIENDLY_FIGHTER,
		FRIENDLY_MINER,
		FRIENDLY_REPAIR_BOT,
		FRIENDLY_TURRET,
		ASTEROID
	}
	
	public enum UpgradeClass{
		NULL,
		FRIENDLY_FIGHTER_1,
		FRIENDLY_FIGHTER_2,
		FRIENDLY_FIGHTER_3,
		FRIENDLY_TURRET_1,
		FRIENDLY_TURRET_2,
		FRIENDLY_TURRET_3,
		FRIENDLY_MINER_1,
		FRIENDLY_MINER_2,
		FRIENLDY_MINER_3
	}
	
	
	public Sprite getFace() {
		return face;
	}
	
	public float getX(){
		return face.getX() + face.getWidth()/2;
	}
	
	public float getY(){
		return face.getY() + face.getHeight()/2;
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public Vector2 getVelocity(){
		return velocity;
	}
	
	public void select(){
		isSelected = true;
		face.setAlpha(0.5f);
		
	}
	
	public void deselect(){
		isSelected = false;
		face.setAlpha(1.0f);
	}
	
	public Type getType(){
		return type;
	}
	
	public float getHealth(){
		return hullHealth;
	}
	
	public abstract void destroy();
	public abstract void move(float x, float y);
	public abstract void attack(Touchable target);
	public abstract void damage(int hullDamage, int ShieldDamage);
	public abstract void mine(int amount, float efficiency);
}
