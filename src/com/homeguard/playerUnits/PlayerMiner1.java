package com.homeguard.playerUnits;

import org.andengine.entity.sprite.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.homeguard.PlayerMinerBase;
import com.homeguard.Touchable;

public class PlayerMiner1 extends PlayerMinerBase{

	public PlayerMiner1(float x, float y) {
		super(x, y);
	}
	
	@Override 
	public void init(){

		//get size of ship
		width = 128;
		height = 128;
		
		face = new Sprite(position.x - width/2, position.y - height/2, width, height, activity.mTexture_friendly_fighter1, activity.vertexBufferObjectManager);

		upgradeClass = UpgradeClass.FRIENDLY_MINER_1;
		
		aggroRange2 = 1000 * 1000; //aggro range squared
		bulletTimerLength = 10; //bullet cooldown
		accuracy = 0.5f; //smaller value = more accurate
		shieldRegenRate = 0.1f;
		maxHullHealth = 100;
		maxShieldHealth = 100;
		maxVelocity = 4;
		acceleration = .03f;//
		mineAmount = 2;
		mineEfficiency = 0.5f;
	}
	
	@Override//
	public void shoot(float x, float y, Touchable asteroid, Vector2 bulletAngleVector){
		asteroid.mine(mineAmount, mineEfficiency);
		bulletTimer = bulletTimerLength;
	}

}
