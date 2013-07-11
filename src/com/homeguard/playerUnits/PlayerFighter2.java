package com.homeguard.playerUnits;

import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.homeguard.FriendlyFighterLaser;
import com.homeguard.FriendlyFighterLaserPool;
import com.homeguard.PlayerFighterBase;
import com.homeguard.Touchable;

public class PlayerFighter2 extends PlayerFighterBase{

	public PlayerFighter2(float x, float y) {
		super(x, y);
	}
	
	@Override 
	public void init(){
		
		//get size of ship
		width = 128;
		height = 128;
		
		face = new Sprite(position.x - width/2, position.y - height/2, width, height, activity.mTexture_friendly_fighter2, activity.vertexBufferObjectManager);
		
		upgradeClass = UpgradeClass.FRIENDLY_FIGHTER_2;
		
		aggroRange2 = 1000 * 1000; //aggro range squared
		bulletTimerLength = 40; //bullet cooldown
		accuracy = 0.5f; //smaller value = more accurate
		shieldRegenRate = 0.1f;//
		maxHullHealth = 100;
		maxShieldHealth = 100;
		maxVelocity = 6;
		acceleration = .08f;
	}
	
	@Override
	public void shoot(float x, float y, Touchable enemyTarget, Vector2 bulletAngleVector){
		FriendlyFighterLaser laser = FriendlyFighterLaserPool.getLaser();
		
		laser.set(x, y, enemyTarget.getX(), enemyTarget.getY());
		enemyTarget.damage(20, 40);
		bulletTimer = bulletTimerLength;
	}

}
