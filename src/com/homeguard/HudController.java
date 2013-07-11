package com.homeguard;

import java.util.ArrayList;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.homeguard.GameScene.TouchState;

public class HudController {
	
	MainActivity activity;
	GameScene scene;
	
	private float touchX;
	private float touchY;
	
	private HUD hud;
	
	private IUpdateHandler updateHandler;
	
	//These 3 rects are premanent
	private Rectangle topBar;
	private Rectangle bottomBar;
	private Rectangle waveCooldownBar;
	
	//these are dynamic
	private Rectangle healthBarBack;
	private Rectangle healthBarFront;
	private Rectangle shieldBarBack;
	private Rectangle shieldBarFront;
	
	private float waveCooldownBarWidth;
	private float healthBarWidth;
	private float shieldBarWidth;
	
	private Text hudTextResources;
	private Text hudTextScore;
	private Text hudTextWave;
	
	private Sprite hudBottomBack;
	private Sprite hudTopBack;
	private Sprite hudButtonDeselect;
	
	//will eventually change all of these to sprites
	private Rectangle hudButtonInfo;
	private Rectangle hudButtonRecycle;
	private Sprite hudButtonBuildMiner;
	private Sprite hudButtonBuildFighter;
	private Sprite hudButtonBuildTurret;
	private Sprite hudButtonBuildHealer;
	
	private TouchState state;
	
	private ArrayList<Entity> hudElements = new ArrayList<Entity>();
	
	/**********************************************************************************************************************************
	 * 
	 * 0 - healthBarBack
	 * 1 - shieldBarBack
	 * 2 - healthBarFront
	 * 3 - shieldBarFront
	 * 4 - hudBottomBack
	 * 5 - hudButtonDeselect
	 * 6 - hudButtonInfo
	 * 7 - hudTopBack
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Each of our HUD elements will have a tag:
	 * 0 Tag is inactive, so no touches will be registered and it will fade from screen
	 * 1 Tag is active, so it will register touches
	 * 2 Tag is visible but not active, dont activate touch but do display at a lesser alpha
	 * 
	 */
	
	public HudController(){
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();
		
		hud = new HUD();
		
		//create top bar, on top of which we will display mineral amount and score, and maybe a pause button
		//add text for score, graphic for mineral count, and text for mineral count
		topBar = new Rectangle(0, 0, 1080, 80, activity.vertexBufferObjectManager);
	    topBar.setColor(0.2f,0.2f,0.2f);
	    hud.attachChild(topBar);
	    
		//create bottom bar, which will show the wave countdown
		//also add the actual bar that will count the cooldown until next wave
	    bottomBar = new Rectangle(0, activity.CAMERA_HEIGHT - 40, 1080, 40, activity.vertexBufferObjectManager);
	    bottomBar.setColor(0.2f,0.2f,0.2f);
	    hud.attachChild(bottomBar);
	    
	    waveCooldownBar = new Rectangle(0, activity.CAMERA_HEIGHT - 40, 1080, 40, activity.vertexBufferObjectManager);
	    waveCooldownBar.setColor(0.4f,0,0);
	    hud.attachChild(waveCooldownBar);
	    
	    hudTextResources = new Text(20, 20, activity.mFont, Integer.toString(scene.getResources()), 9, new TextOptions(HorizontalAlign.LEFT), activity.vertexBufferObjectManager);
	    hudTextResources.setColor(.7f, .7f, .7f);
	    hud.attachChild(hudTextResources);
	    
	    hudTextScore = new Text(20, 20, activity.mFont, Integer.toString(scene.getScore()), 9, new TextOptions(HorizontalAlign.RIGHT), activity.getVertexBufferObjectManager());
	    hudTextScore.setPosition(1080 - hudTextScore.getWidth() - 20, 20);
	    hudTextScore.setColor(.7f, .7f, .7f);
	    hud.attachChild(hudTextScore);
		
	    //dynamic elements
	    healthBarBack = new Rectangle(0, activity.CAMERA_HEIGHT - 60, 1080, 20, activity.vertexBufferObjectManager);
	    healthBarBack.setColor(0.2f,0.2f,0.2f);
	    healthBarBack.setAlpha(0);
	    healthBarBack.setTag(0);
	    hudElements.add(healthBarBack);
	    hud.attachChild(healthBarBack);
	    
	    shieldBarBack = new Rectangle(0, activity.CAMERA_HEIGHT - 80, 1080, 20, activity.vertexBufferObjectManager);
	    shieldBarBack.setColor(0.2f,0.2f,0.2f);
	    shieldBarBack.setAlpha(0);
	    shieldBarBack.setTag(0);
	    hudElements.add(shieldBarBack);
	    hud.attachChild(shieldBarBack);
	    
	    healthBarFront = new Rectangle(0, activity.CAMERA_HEIGHT - 60, 1080, 20, activity.vertexBufferObjectManager);
	    healthBarFront.setColor(0, 0.8f, 0);
	    healthBarFront.setAlpha(0);
	    healthBarFront.setTag(0);
	    hudElements.add(healthBarFront);
	    hud.attachChild(healthBarFront);
	    
	    shieldBarFront = new Rectangle(0, activity.CAMERA_HEIGHT - 80, 1080, 20, activity.vertexBufferObjectManager);
	    shieldBarFront.setColor(0.5f, 0.5f, 1);
	    shieldBarFront.setAlpha(0);
	    shieldBarFront.setTag(0);
	    hudElements.add(shieldBarFront);
	    hud.attachChild(shieldBarFront);
	    
	    hudBottomBack = new Sprite(0, activity.CAMERA_HEIGHT - 480, 1080, 400, activity.mTexture_bud_bottomBack, activity.vertexBufferObjectManager);
	    hudBottomBack.setAlpha(0);
	    hudBottomBack.setTag(0);
	    hudElements.add(hudBottomBack);
	    hud.attachChild(hudBottomBack);
	    
	    hudButtonDeselect = new Sprite(325, activity.CAMERA_HEIGHT - 355, 430, 175, activity.mTexture_hud_button_deselect, activity.vertexBufferObjectManager)
	    	{
	        	public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
	        	{
	        		if (!scene.isTouched){
	        			if (touchEvent.isActionDown())
	        			{
	        				scene.touchHandled = true;
	        				scene.isTouched = false;
	        			}
	        			else if (touchEvent.isActionUp())
	        			{
	        				scene.setNullTouchState();
	        				scene.touchHandled = false;;
	        			}
	        		}
	        		else{
        			    if (touchEvent.isActionMove())
	        			{

	        				touchX = touchEvent.getMotionEvent().getRawX();
	        				touchY = touchEvent.getMotionEvent().getRawY();
	        					
	        				scene.getCameraController().moveCamera(-(touchX - scene.touchXDown) * activity.SCALAR / scene.zoomScalar, -(touchY - scene.touchYDown) * activity.SCALAR / scene.zoomScalar, scene.cameraXP, scene.cameraYP);
	        			}
        			    if (touchEvent.isActionUp())
        			    {
        			    	scene.isTouched = false;
        			    }
        			}
	        		return true;
	        	};
	    	}; 	
	    hudButtonDeselect.setAlpha(0);
	    hudButtonDeselect.setTag(0);
	    hudElements.add(hudButtonDeselect);
	    hud.attachChild(hudButtonDeselect);
	    
	    hudButtonInfo = new Rectangle(820, activity.CAMERA_HEIGHT - 340, 220, 220,activity.vertexBufferObjectManager)
    		{
        		public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
        		{
        			if (!scene.isTouched){
	        			if (touchEvent.isActionDown())
	        			{
	        				scene.touchHandled = true;
	        			}
	        			else if (touchEvent.isActionUp())
	        			{
	        				//do some stuff
	        				scene.touchHandled = false;;
	        			}
	        		}
        			else{
        			    if (touchEvent.isActionMove())
	        			{

	        				touchX = touchEvent.getMotionEvent().getRawX();
	        				touchY = touchEvent.getMotionEvent().getRawY();
	        					
	        				scene.getCameraController().moveCamera(-(touchX - scene.touchXDown) * activity.SCALAR / scene.zoomScalar, -(touchY - scene.touchYDown) * activity.SCALAR / scene.zoomScalar, scene.cameraXP, scene.cameraYP);
	        			}
        			    if (touchEvent.isActionUp())
        			    {
        			    	scene.isTouched = false;
        			    }
        			}
        			return true;
        		};
    		}; 	
    	hudButtonInfo.setColor(0,0,0);
    	hudButtonInfo.setAlpha(0);
    	hudButtonInfo.setTag(0);
    	hudElements.add(hudButtonInfo);
    	hud.attachChild(hudButtonInfo);
	    
    	
	    hudTopBack = new Sprite(0, 80, 1080, 400, activity.mTexture_hud_topBack, activity.vertexBufferObjectManager);
	    hudTopBack.setAlpha(0); 
	    hudTopBack.setTag(0);
	    hudElements.add(hudTopBack);
	    hud.attachChild(hudTopBack);
	    
	    hudButtonBuildMiner = new Sprite(40, 120, 220, 220, activity.mTexture_hud_button_buildMiner, activity.vertexBufferObjectManager){
    		public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
    		{
    			if (!scene.isTouched){
        			if (touchEvent.isActionDown())
        			{
        				scene.touchHandled = true;
        			}
        			else if (touchEvent.isActionUp())
        			{
        				
        				if(this.getTag() == 1){
        					scene.setNullTouchState();
        					scene.setCustomTouchState(TouchState.BUILD_MINER);
        				}
        				scene.touchHandled = false;
        			}
        			else if (touchEvent.isActionCancel())
        			{
        				hudButtonInfo.setColor(1,0,0);
        			}
        		}
    			else{
    			    if (touchEvent.isActionMove())
        			{

        				touchX = touchEvent.getMotionEvent().getRawX();
        				touchY = touchEvent.getMotionEvent().getRawY();
        					
        				scene.getCameraController().moveCamera(-(touchX - scene.touchXDown) * activity.SCALAR / scene.zoomScalar, -(touchY - scene.touchYDown) * activity.SCALAR / scene.zoomScalar, scene.cameraXP, scene.cameraYP);
        			}
    			    if (touchEvent.isActionUp())
    			    {
    			    	scene.isTouched = false;
    			    }
    			}
    			return true;
    		};
		}; 	
		hudButtonBuildMiner.setAlpha(0);
	    hudButtonBuildMiner.setTag(0);
	    hudElements.add(hudButtonBuildMiner);
    	hud.attachChild(hudButtonBuildMiner);
    	
    	hudButtonBuildFighter = new Sprite(300, 120, 220, 220, activity.mTexture_hud_button_buildFighter, activity.vertexBufferObjectManager){
    		public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
    		{
    			if (!scene.isTouched){
        			if (touchEvent.isActionDown())
        			{
        				scene.touchHandled = true;
        			}
        			else if (touchEvent.isActionUp())
        			{
        				
        				if(this.getTag() == 1){
        					scene.setNullTouchState();
        					scene.setCustomTouchState(TouchState.BUILD_FIGHTER);
        				}
        				scene.touchHandled = false;
        			}
        			else if (touchEvent.isActionCancel())
        			{
        				hudButtonInfo.setColor(1,0,0);
        			}
        		}
    			else{
    			    if (touchEvent.isActionMove())
        			{

        				touchX = touchEvent.getMotionEvent().getRawX();
        				touchY = touchEvent.getMotionEvent().getRawY();
        					
        				scene.getCameraController().moveCamera(-(touchX - scene.touchXDown) * activity.SCALAR / scene.zoomScalar, -(touchY - scene.touchYDown) * activity.SCALAR / scene.zoomScalar, scene.cameraXP, scene.cameraYP);
        			}
    			    if (touchEvent.isActionUp())
    			    {
    			    	scene.isTouched = false;
    			    }
    			}
    			return true;
    		};
		}; 	
		hudButtonBuildFighter.setAlpha(0);
	    hudButtonBuildFighter.setTag(0);
	    hudElements.add(hudButtonBuildFighter);
    	hud.attachChild(hudButtonBuildFighter);
    	
    	hudButtonBuildTurret = new Sprite(560, 120, 220, 220, activity.mTexture_hud_button_buildTurret, activity.vertexBufferObjectManager){
    		public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
    		{
    			if (!scene.isTouched){
        			if (touchEvent.isActionDown())
        			{
        				scene.touchHandled = true;
        			}
        			else if (touchEvent.isActionUp())
        			{
        				
        				if(this.getTag() == 1){
        					scene.setNullTouchState();
        					scene.setCustomTouchState(TouchState.BUILD_TURRET);
        				}
        				scene.touchHandled = false;
        			}
        			else if (touchEvent.isActionCancel())
        			{
        				hudButtonInfo.setColor(1,0,0);
        			}
        		}
    			else{
    			    if (touchEvent.isActionMove())
        			{

        				touchX = touchEvent.getMotionEvent().getRawX();
        				touchY = touchEvent.getMotionEvent().getRawY();
        					
        				scene.getCameraController().moveCamera(-(touchX - scene.touchXDown) * activity.SCALAR / scene.zoomScalar, -(touchY - scene.touchYDown) * activity.SCALAR / scene.zoomScalar, scene.cameraXP, scene.cameraYP);
        			}
    			    if (touchEvent.isActionUp())
    			    {
    			    	scene.isTouched = false;
    			    }
    			}
    			return true;
    		};
		}; 	
		hudButtonBuildTurret.setAlpha(0);
	    hudButtonBuildTurret.setTag(0);
	    hudElements.add(hudButtonBuildTurret);
    	hud.attachChild(hudButtonBuildTurret);
    	
    	hudButtonBuildHealer = new Sprite(820, 120, 220, 220, activity.mTexture_hud_button_buildHealer, activity.vertexBufferObjectManager){
    		public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
    		{
    			if (!scene.isTouched){
        			if (touchEvent.isActionDown())
        			{
        				scene.touchHandled = true;
        			}
        			else if (touchEvent.isActionUp())
        			{
        				
        				if(this.getTag() == 1){
        					scene.setNullTouchState();
        					scene.setCustomTouchState(TouchState.BUILD_HEALER);
        				}
        				scene.touchHandled = false;
        			}
        			else if (touchEvent.isActionCancel())
        			{
        				hudButtonInfo.setColor(1,0,0);
        			}
        		}
    			else{
    			    if (touchEvent.isActionMove())
        			{

        				touchX = touchEvent.getMotionEvent().getRawX();
        				touchY = touchEvent.getMotionEvent().getRawY();
        					
        				scene.getCameraController().moveCamera(-(touchX - scene.touchXDown) * activity.SCALAR / scene.zoomScalar, -(touchY - scene.touchYDown) * activity.SCALAR / scene.zoomScalar, scene.cameraXP, scene.cameraYP);
        			}
    			    if (touchEvent.isActionUp())
    			    {
    			    	scene.isTouched = false;
    			    }
    			}
    			return true;
    		};
		}; 	
		hudButtonBuildHealer.setAlpha(0);
	    hudButtonBuildHealer.setTag(0);
	    hudElements.add(hudButtonBuildHealer);
    	hud.attachChild(hudButtonBuildHealer);
	   
	    
	    
	    
		activity.mCamera.setHUD(hud);
		
		updateHandler = new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				//this controls our visibility by adjusting the alpha accordingly, has a nice fade effect
				for (int i = 0; i < hudElements.size(); i++){
					
					if(hudElements.get(i).getTag() == 1 || hudElements.get(i).getTag() == 2){
						hudElements.get(i).setAlpha(hudElements.get(i).getAlpha() + 0.1f);
					}
					else if(hudElements.get(i).getTag() == 0){
						hudElements.get(i).setAlpha(hudElements.get(i).getAlpha() - 0.1f);
					}
					
					if(hudElements.get(i).getTag() == 1 && hudElements.get(i).getAlpha() >= 1.0f){
						hudElements.get(i).setAlpha(1.0f);
					}
					else if(hudElements.get(i).getTag() == 2 && hudElements.get(i).getAlpha() >= 0.3f){
						hudElements.get(i).setAlpha(0.3f);
					}
					else if(hudElements.get(i).getTag() == 0 && hudElements.get(i).getAlpha() <= 0){
						hudElements.get(i).setAlpha(0);
					}
				}
				
				//update the functionality of our build buttons based on the resource amount
				//do we have enough to build it? 
				//if yes then enable button
				if (hudButtonBuildMiner.getTag() == 2 && scene.getResources() >= scene.buildMinerCost){
					hudButtonBuildMiner.setTag(1);
				}
				else if (hudButtonBuildMiner.getTag() == 1 && scene.getResources() < scene.buildMinerCost){
					hudButtonBuildMiner.setTag(2);
				}
				
				if (hudButtonBuildFighter.getTag() == 2 && scene.getResources() >= scene.buildFighterCost){
					hudButtonBuildFighter.setTag(1);
				}
				else if (hudButtonBuildFighter.getTag() == 1 && scene.getResources() < scene.buildFighterCost){
					hudButtonBuildFighter.setTag(2);
				}
				
				if (hudButtonBuildTurret.getTag() == 2 && scene.getResources() >= scene.buildTurretCost){
					hudButtonBuildTurret.setTag(1);
				}
				else if (hudButtonBuildTurret.getTag() == 1 && scene.getResources() < scene.buildTurretCost){
					hudButtonBuildTurret.setTag(2);
				}
				
				if (hudButtonBuildHealer.getTag() == 2 && scene.getResources() >= scene.buildHealerCost){
					hudButtonBuildHealer.setTag(1);
				}
				else if (hudButtonBuildHealer.getTag() == 1 && scene.getResources() < scene.buildHealerCost){
					hudButtonBuildHealer.setTag(2);
				}
				
				
				//update our dynamic hud elements
				//text, wave cooldown bar, etc
				hudTextResources.setText(Integer.toString(scene.getResources()));
				
				hudTextScore.setText(Integer.toString(scene.getScore()));
				hudTextScore.setPosition(1080 - hudTextScore.getWidth() - 20, 20);
				
				waveCooldownBarWidth = scene.waveCooldown / scene.waveCooldownLength;
				waveCooldownBar.setWidth(waveCooldownBarWidth * 1080);
				
				if (state == TouchState.ENEMY || state == TouchState.FRIENDLY_FIGHTER || state == TouchState.ASTEROID){
					healthBarWidth = scene.getSelected().hullHealth / scene.getSelected().maxHullHealth;
					healthBarFront.setWidth(healthBarWidth * 1080);
				
					shieldBarWidth = scene.getSelected().shieldHealth / scene.getSelected().maxShieldHealth;
					shieldBarFront.setWidth(shieldBarWidth * 1080);
				}
			}	
		};
	
		scene.registerUpdateHandler(updateHandler);
	}


	public void setState(TouchState touchState) {
		
		state = touchState;
		
		switch(touchState){
		
		case NULL:
			clearHUD();
			break;
		case FRIENDLY_FIGHTER:
			clearHUD();
			for (int i = 0; i <= 7; i++){
				hudElements.get(i).setTag(1);
			}
			hud.registerTouchArea(hudButtonDeselect);
			hud.registerTouchArea(hudButtonInfo);
			break;
		case ENEMY:
			clearHUD();
			for (int i = 0; i <= 6; i++){
				hudElements.get(i).setTag(1);
			}
			hud.registerTouchArea(hudButtonDeselect);
			hud.registerTouchArea(hudButtonInfo);
			break;
		case MOTHERSHIP:
			clearHUD();
			for (int i = 0; i <= 7; i++){
				hudElements.get(i).setTag(1);
			}
			hud.registerTouchArea(hudButtonDeselect);
			hud.registerTouchArea(hudButtonInfo);
			
			
			hud.registerTouchArea(hudButtonBuildMiner);
			hud.registerTouchArea(hudButtonBuildFighter);
			hud.registerTouchArea(hudButtonBuildTurret);
			hud.registerTouchArea(hudButtonBuildHealer);
			
			if (scene.getResources() >= scene.buildMinerCost){
				hudButtonBuildMiner.setTag(1);
			}
			else{
				hudButtonBuildMiner.setTag(2);
			}
			if (scene.getResources() >= scene.buildFighterCost){
				hudButtonBuildFighter.setTag(1);
			}
			else{
				hudButtonBuildFighter.setTag(2);
			}
			if (scene.getResources() >= scene.buildTurretCost){
				hudButtonBuildTurret.setTag(1);
			}
			else{
				hudButtonBuildTurret.setTag(2);
			}
			if (scene.getResources() >= scene.buildHealerCost){
				hudButtonBuildHealer.setTag(1);
			}
			else{
				hudButtonBuildHealer.setTag(2);
			}
			
			
			break;
		case FRIENDLY_MULTIPLE:
			break;
		case FRIENDLY_MINER:
			break;
		case ASTEROID:
			clearHUD();
			for (int i = 0; i <= 6; i++){
				hudElements.get(i).setTag(1);
			}
			hud.registerTouchArea(hudButtonDeselect);
			hud.registerTouchArea(hudButtonInfo);
			break;	
		case BUILD_TURRET:
			break;
		case BUILD_FIGHTER:
			break;
		case BUILD_MINER:
			break;
		case BUILD_HEALER:
			break;
		}
	}
	
	public void clearHUD(){
		for (int i = 0; i < hudElements.size(); i++){
			hudElements.get(i).setTag(0);
		}
		hud.clearTouchAreas();
	}
}

