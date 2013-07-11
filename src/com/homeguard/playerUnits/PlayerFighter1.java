package com.homeguard.playerUnits;

import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.homeguard.FriendlyBullet;
import com.homeguard.FriendlyBulletPool;
import com.homeguard.MainActivity;
import com.homeguard.PlayerFighterBase;
import com.homeguard.Touchable;

public class PlayerFighter1 extends PlayerFighterBase{

	public PlayerFighter1(float x, float y) {
		super(x, y);
	}
	
	@Override 
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
		acceleration = .04f;
	}
	
	@Override
	public void shoot(float x, float y, Touchable enemyTarget, Vector2 bulletAngleVector){
		FriendlyBullet bullet = FriendlyBulletPool.getBullet();

		angleTemp = MainActivity.getVectorAngle(bulletAngleVector) + (float)Math.random() * accuracy - accuracy/2;
		
		bullet.set(new Vector2(x, y), new Vector2((float) Math.cos(angleTemp), (float) Math.sin(angleTemp)));	
		bulletTimer = bulletTimerLength;
	}

}
