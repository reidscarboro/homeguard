package com.homeguard;

import java.util.ArrayList;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.IBackground;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.homeguard.playerUnits.*;

import android.util.Log;

public class GameScene extends Scene implements IOnSceneTouchListener  {
	
	public enum TouchState{
		
		NULL,				//nothing selected
		FRIENDLY_FIGHTER,	//friendly something selected
		ENEMY,				//enemy unit selected
		MOTHERSHIP,			//mothership selected
		FRIENDLY_MULTIPLE,	//multiple friendlies are selected
		FRIENDLY_MINER,		//friendly miner
		ASTEROID,			//asteroid selected
		BUILD_MINER,		//option to create a miner was selected
		BUILD_TURRET,		//option to create a turret was selected
		BUILD_FIGHTER,		//option to create a fighter was selected
		BUILD_HEALER		//option to create a healer was selected
	}
	
	public static GameScene instance; //instance of the singleton
	private MainActivity activity;
	
	public boolean isTouched = false; //test to see if the scene is touched
	public boolean touchHandled = false;
	
	HudController hudController;
	
	public ZoomCamera mCamera;
	public float zoomScalar = 1;
	
	private TouchState touchState = TouchState.NULL;
	
	public PlayerMothership playerMothership;
	private CameraController cameraController;
	private ClickController clickController;
	
	public float touchXDown;
	public float touchYDown;
	
	public float cameraXP;
	public float cameraYP;
	
	private float touchX;
	private float touchY;
	
	private Touchable selection;
	
	public ArrayList<Touchable> touchableList = new ArrayList<Touchable>(); 
	public ArrayList<Touchable> touchableListFriendly = new ArrayList<Touchable>();
	public ArrayList<Touchable> touchableListEnemy = new ArrayList<Touchable>();
	public ArrayList<Touchable> touchableListAsteroid = new ArrayList<Touchable>();
	private ArrayList<Integer> indexOfTouched = new ArrayList<Integer>();
	private int indexOfClosestTouched;
	private int indexOfSelecting;
	
	private boolean handlingTouch;
	
	public int gameplayWidth = 4000;
	public int gameplayHeight = 4000;
	
	public float waveCooldown;
	public float waveCooldownLength = 1800;
	public int waveNumber = 3;
	public float waveDifficulty = 1;
	
	private float tempRandom = 0.5f;
	private float waveSpawnX = 0;
	private float waveSpawnY = 0;
	private float enemySpawnX = 0;
	private float enemySpawnY = 0;
	private int waveSpawnWall = 1; //1 top, 2 right, 3 bottom, 4 left
	
	public int resources = 80;
	public int score = 0;
	
	public int buildMinerCost = 60;
	public int buildFighterCost = 100;
	public int buildTurretCost = 150;
	public int buildHealerCost = 300;
	
	//the cost of updgrading units TO the one listed, it is assumed what they are being upgraded from through tracing the upgrade trees
	public int upgradeFighter2 = 100;
	public int upgradeFighter3 = 100;
	
	public int tempNumSpawned;
	
	public GameScene(){
		
		instance = this;
		activity = MainActivity.getSharedInstance();
		activity.setCurrentScene(this);
		setOnSceneTouchListener(this);
		
		initialSetup();
		
		spawnAsteroid();
		spawnAsteroid();
		
		
		this.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {	
				
				//Log.d("touch state", touchState.toString());
				
				//spawns an asteroid 1.8 times a wave, theoretically
				//the value * 60 * 30 should equal the number of asteroids per wave
				if ((float) Math.random() < 0.0005f){
					spawnAsteroid();//
				}
				
				if (waveCooldown <= 0){
					waveTick();
					waveCooldown = waveCooldownLength;
				}
				else{
					waveCooldown -= 1;
				}

				for(int i=0; i<instance.getChildCount();i++){
					if (instance.getChildByIndex(i) instanceof BulletGraphic){
						
						BulletGraphic bullet = (BulletGraphic) instance.getChildByIndex(i);
						if (!bullet.getBullet().isActive){
							instance.detachChild(instance.getChildByIndex(i));

						}
					}
				}
			}
		});
		
	} 
	
	public void initialSetup(){
		
		mCamera = activity.mCamera;
		mCamera.setCenter(0,0);
		
		mCamera.setZoomFactor(zoomScalar);
		
		//build background
		SpriteBackground bg = new SpriteBackground(new Sprite(0, 80, 1080, 2048, activity.mTexture_background1, activity.vertexBufferObjectManager));
        this.setBackground(bg);
		
		//create some objects
		
		cameraController = new CameraController(0,0);
		
		///drawGrid();
		
		clickController = new ClickController(0,0);
		hudController = new HudController();
		playerMothership = new PlayerMothership(0,0);
		
		waveCooldown = waveCooldownLength;

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent event) {
		
		switch(event.getAction()){
		case android.view.MotionEvent.ACTION_DOWN:
			
			if (!touchHandled){
				isTouched = true;
	
				cameraXP = cameraController.getX();
				cameraYP = cameraController.getY();
					
				touchXDown = event.getMotionEvent().getRawX();
				touchYDown = event.getMotionEvent().getRawY();
			
				cameraController.stopCamera(); 
			}	
			return true;
			
		case android.view.MotionEvent.ACTION_UP:
			
			if (!touchHandled){
				//if click registered
				if (Math.pow(event.getMotionEvent().getRawX() - touchXDown, 2) + Math.pow(event.getMotionEvent().getRawY() - touchYDown, 2) < 1024){
					clickController.setX(event.getX());
					clickController.setY(event.getY());
					
					///clear index of touched object in preparation to receive new list
					indexOfTouched.clear();

					//loop through touchables, if it is touched, add it to the list
					for (int i = 0; i < touchableList.size(); i++) {
						if(clickController.getFace().collidesWith(touchableList.get(i).getFace())){
							indexOfTouched.add(i);
						}
					}

					//if you didnt touch anything
					if (indexOfTouched.isEmpty()){

						switch (touchState){
						////
						//if the selection is friendly
						//move the friendly, and deselect everything
						//// 
						case FRIENDLY_FIGHTER:
							selection.move(event.getX(), event.getY());
							selection.deselect();
							setNullTouchState();
							break;
						case FRIENDLY_MINER:
							selection.move(event.getX(), event.getY());
							selection.deselect();
							setNullTouchState();
							break;
						case BUILD_FIGHTER:
							subResources(buildFighterCost);
							new PlayerFighter1(event.getX(), event.getY());
							setNullTouchState();
							break;
						case BUILD_MINER:
							subResources(buildMinerCost);
							new PlayerMiner1(event.getX(), event.getY());
							setNullTouchState();
							break;
						case BUILD_TURRET:
							subResources(buildTurretCost);
							new Turret1(event.getX(), event.getY()); 
							setNullTouchState();
							break;
						case BUILD_HEALER:
							subResources(buildHealerCost);
							new Turret2(event.getX(), event.getY()); 
							setNullTouchState();
							break;
						}
					}
					
					//if an object is touched
					else {
						
						//if more than one object is touched
						if (indexOfTouched.size() > 1){
							for (int i = 0; i < indexOfTouched.size(); i++){

								//if it is the first object in the list, it is automatically the closest
								if (i == 0){
									indexOfClosestTouched = 0;
								}
								else{
								
									//while looping, if one of the objects is closer than the current closest object, update accordingly
									if ((Math.pow(clickController.getX() - touchableList.get(indexOfTouched.get(i)).getX(), 2) + 
										 Math.pow(clickController.getY() - touchableList.get(indexOfTouched.get(i)).getY(), 2)) < 
										(Math.pow(clickController.getX() - touchableList.get(indexOfTouched.get(indexOfClosestTouched)).getX(), 2) + 
										 Math.pow(clickController.getY() - touchableList.get(indexOfTouched.get(indexOfClosestTouched)).getY(), 2)))
									{
										indexOfClosestTouched = i;
									}
								}
								
							}
						
							indexOfSelecting = indexOfTouched.get(indexOfClosestTouched);
							
						}
					
						//only one object touched
						else {
							indexOfSelecting = indexOfTouched.get(0);
						}
						
						//for all of the touchable objects
						for (int i = 0; i < touchableList.size(); i++) {
							touchableList.get(i).deselect();
						}

						//******************************************************************************************************************
						//
						//we now have the object that is touched, it is saved in the index "indexOfSelecting" in the indexOfTouched arraylist
						//switch to see what the current touchstate is, and behave accordingly
						//yeah
						//
						//******************************************************************************************************************
						switch (touchState) {
						
						//if nothing is currently selected, a null touch state, we will select the object
						case NULL:
							selection = touchableList.get(indexOfSelecting);
							selection.select();
							setTouchState();
							break;
						
						//if we have a friendly selected, check to see if an enemy is clicked on, then attack that enemy
						case FRIENDLY_FIGHTER:
							
							//if the new selection is an enemy, attack it
							if (touchableList.get(indexOfSelecting).getType() == Touchable.Type.ENEMY){
								selection.attack(touchableList.get(indexOfSelecting));
								setNullTouchState();
							}
							
							//otherwise just select the new touched
							else {
								selection = touchableList.get(indexOfSelecting);
								selection.select();
								setTouchState();
							}
							break;
						
						//since we have no control over the enemies, when we click on
						//something while an enemy is selected, we will simply deselect the enemy and select the new target
						case ENEMY:
							selection.deselect();
							selection = touchableList.get(indexOfSelecting);
							selection.select();
							setTouchState();
							
							break;
						case FRIENDLY_MINER:
							
							//if the new selection is an enemy, attack it
							if (touchableList.get(indexOfSelecting).getType() == Touchable.Type.ASTEROID){
								selection.attack(touchableList.get(indexOfSelecting));
								setNullTouchState();
							}
							
							//otherwise just select the new touched
							else {
								selection = touchableList.get(indexOfSelecting);
								selection.select();
								setTouchState();
							}
							break;
						case MOTHERSHIP:
							selection.deselect();
							selection = touchableList.get(indexOfSelecting);
							 selection.select();
							setTouchState();
							break;
						case FRIENDLY_MULTIPLE:
							break;
						case ASTEROID:
							selection.deselect();
							selection = touchableList.get(indexOfSelecting);
							selection.select();
							setTouchState();
							
							break;
						case BUILD_TURRET:
							break;
						case BUILD_FIGHTER:
							break;
						case BUILD_HEALER:
							break;
						case BUILD_MINER:
							break;
						}
						//******************************************************************************************************************
						//
						//
						//******************************************************************************************************************
					}
				}
				
				isTouched = false;
			}
			else{
				touchHandled = false;
			}
			return true;
		case android.view.MotionEvent.ACTION_MOVE:
			
			if (!touchHandled){
				touchX = event.getMotionEvent().getRawX();
				touchY = event.getMotionEvent().getRawY();
				
				cameraController.moveCamera(-(touchX - touchXDown) * activity.SCALAR / zoomScalar, -(touchY - touchYDown) * activity.SCALAR / zoomScalar, cameraXP, cameraYP);
			}
			return true; 
		}
		
		return false;
	}
	
	public void spawnAsteroid(){
		getSpawnPoint();
		new Asteroid(waveSpawnX, waveSpawnY);
	}
	
	public void waveTick(){
		
		tempNumSpawned = 0;
		
		waveNumber += 1;
		waveDifficulty = 0.1f * waveNumber * waveNumber;
		
		getSpawnPoint();
		
		for (int i = 0; i < 2 + waveDifficulty / 2 + (float)Math.random() * waveDifficulty / 2; i++){
			
			enemySpawnX = waveSpawnX;
			enemySpawnY = waveSpawnY;
			
			new EnemyFighter1(enemySpawnX,enemySpawnY);
			
			
			//theres a 10% chance that we will get a new spawn point to create multiple spawn locations
			if (Math.random() < 0.1f){
				getSpawnPoint();
			}
			
			tempNumSpawned +=1;
		}
		//Log.d("waveDifficulty", Float.toString(waveDifficulty));
		Log.d(Integer.toString(waveNumber), Integer.toString(tempNumSpawned));
		
	}
	
	public void getSpawnPoint(){
		
		tempRandom = (float) Math.random();
		
		//we take a random variable to see which wall to spawn the enemies on
		if (tempRandom < 0.25f){
			waveSpawnWall = 1;
		}
		else if (tempRandom >= 0.25f && tempRandom < 0.5f){
			waveSpawnWall = 2;
		}
		else if (tempRandom >= 0.5f && tempRandom < 0.75f){
			waveSpawnWall = 3;
		}
		else if (tempRandom >= 0.75f){
			waveSpawnWall = 4;
		}
		
		//creates the x and y location of our spawn origin
		switch (waveSpawnWall){
		case 1:
			waveSpawnX = -gameplayWidth/2 + (float)Math.random() * gameplayWidth;
			waveSpawnY = -gameplayHeight/2;
			break;
		case 2:
			waveSpawnX = gameplayWidth/2;
			waveSpawnY = -gameplayHeight/2 + (float)Math.random() * gameplayHeight;
			break;
		case 3:
			waveSpawnX = -gameplayWidth/2 + (float)Math.random() * gameplayWidth;
			waveSpawnY = gameplayHeight/2;
			break;
		case 4:
			waveSpawnX = -gameplayWidth/2;
			waveSpawnY = -gameplayHeight/2 + (float)Math.random() * gameplayHeight;
			break;
		}
	}
	
	//method used to set the correct touch state based on selection
	//we will call this whenever we make changes to the touchstate
	//
	//refer to method "setCustomTouchState" for defining a touchstate of our own
	public void setTouchState(){
		//switch to see what the new selection type is
		switch (selection.getType()){
		case MOTHERSHIP:
			touchState = TouchState.MOTHERSHIP;
			break;
		case ENEMY:
			touchState = TouchState.ENEMY;
			break;
		case FRIENDLY_FIGHTER:
			touchState = TouchState.FRIENDLY_FIGHTER;
			break;
		case FRIENDLY_MINER:
			touchState = TouchState.FRIENDLY_MINER;
			break;
		case FRIENDLY_TURRET:
			touchState = TouchState.FRIENDLY_FIGHTER;
			break;
		case ASTEROID:
			touchState = TouchState.ASTEROID;
			break;
		}
		
		//send the new touchstate to the hudcontroller so we can update the hud
		//eg add upgrade buttons and health bars etc etc
		hudController.setState(touchState);
	}
	
	public void setCustomTouchState(TouchState newState){
		touchState = newState;
		hudController.setState(touchState);
	}
	
	public void setNullTouchState(){
		touchState = TouchState.NULL;
		
		if (selection != null){
			selection.deselect();
		}
		
		//send the new touchstate to the hudcontroller so we can update the hud
		//eg add upgrade buttons and health bars etc etc
		hudController.setState(touchState);
	}
	public void addAsteroidTouchable(Touchable touchable){
		touchableList.add(touchable);
		touchableListAsteroid.add(touchable);
	}
	
	public void addFriendlyTouchable(Touchable touchable){
		touchableList.add(touchable);
		touchableListFriendly.add(touchable);
	}
	
	public void addEnemyTouchable(Touchable touchable){
		touchableList.add(touchable);
		touchableListEnemy.add(touchable);
	}
	public void subResources(int resourceAmount){
		resources -= resourceAmount;
	}
	public void addResources(int resourceAmount){
		resources += resourceAmount;
	}
	public int getResources(){
		return resources;
	}
	public void addScore(float scoreAmount){
		score += scoreAmount;
	}
	public int getScore(){
		return score;
	}
	
	public static GameScene getSharedInstance() {
		return instance;
	}
	public float getCameraX(){
		return cameraController.getX();
	}
	public CameraController getCameraController(){
		return cameraController;
	}
	public float getCameraY(){
		return cameraController.getY();
	}
	public Touchable getSelected(){
		return selection;
	}

	public void drawGrid(){
		for (int i = -2000; i<=2000; i+=200){
			final Line line = new Line(i, -2000, i, 2000, 1, activity.vertexBufferObjectManager);
			if (i==0){
				line.setColor(1,1,1);
				line.setAlpha(0.6f);}
			else{
				line.setColor(1,1,1);
				line.setAlpha(0.2f);}
			this.attachChild(line);
		}
		for (int i = -2000; i<=2000; i+=200){
			final Line line = new Line(-2000, i, 2000, i, 1, activity.vertexBufferObjectManager);
			if (i==0){
				line.setColor(1,1,1);
				line.setAlpha(0.6f);}
			else{
				line.setColor(1,1,1);
				line.setAlpha(0.2f);}
			this.attachChild(line);
		}
	}
}
