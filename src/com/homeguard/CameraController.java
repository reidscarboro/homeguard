package com.homeguard;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class CameraController {
	
	MainActivity activity;
	GameScene scene;
	
	public static final float CAMERA_SLOW = 0.95f;
	
	private Rectangle face;
	
	private Vector2 position;
	private Vector2 velocity;
	
	private int maxSpeed = 200;
	
	private float distanceToMove2;
	private int slowDistance2 = 1000*1000;
	
	private BackgroundLayer backgroundLayer1;
	private BackgroundLayer backgroundLayer2;
	private BackgroundLayer backgroundLayer3;
	
	public CameraController(int pX, int pY){
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();
		
		position = new Vector2(pX, pY);
		velocity = new Vector2(0,0);
		
		face = new Rectangle(0, 0, 0, 0, activity.getVertexBufferObjectManager());
		face.setColor(0, 1, 0);
		face.setVisible(false);
		
		backgroundLayer2 = new BackgroundLayer(activity.mTexture_background3, 1.25f);
		backgroundLayer3 = new BackgroundLayer(activity.mTexture_background4, 1.5f);
		backgroundLayer1 = new BackgroundLayer(activity.mTexture_background2, 2);		
		
		scene.attachChild(face);
		scene.mCamera.setChaseEntity(face);
		
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				if (!scene.isTouched){
					velocity.mul(CAMERA_SLOW);
				}
				
				if (velocity.len() > maxSpeed){
					velocity.mul(maxSpeed/velocity.len());
				}
				
				position.add(velocity);
				
				if (position.x + activity.getCameraWidth()/2 > scene.gameplayWidth/2){
					position.set(scene.gameplayWidth/2 - activity.getCameraWidth()/2, position.y);
				}
				else if (position.x - activity.getCameraWidth()/2< -scene.gameplayWidth/2){
					position.set(-scene.gameplayWidth/2 + activity.getCameraWidth()/2, position.y);
				}
				if (position.y + activity.getCameraHeight()/2 > scene.gameplayHeight/2){
					position.set(position.x, scene.gameplayHeight/2 - activity.getCameraHeight()/2);
				}
				else if (position.y - activity.getCameraHeight()/2< -scene.gameplayHeight/2){
					position.set(position.x, -scene.gameplayHeight/2 + activity.getCameraHeight()/2);
				}
				
				face.setPosition(position.x, position.y);
				
				backgroundLayer1.move(scene.mCamera.getCenterX(), scene.mCamera.getCenterY());
				backgroundLayer2.move(scene.mCamera.getCenterX(), scene.mCamera.getCenterY());
				backgroundLayer3.move(scene.mCamera.getCenterX(), scene.mCamera.getCenterY());

			}	
		});
		
	}

	
	public void moveCamera(float dX, float dY, float touchStartX, float touchStartY){
		
		distanceToMove2 = (float) Math.pow(dX + touchStartX - face.getX(),2) + (float) Math.pow(dY + touchStartY - face.getY(),2);

		velocity = new Vector2((dX + touchStartX - face.getX())/(slowDistance2/distanceToMove2), (dY + touchStartY - face.getY())/(slowDistance2/distanceToMove2));
	}
	
	public void stopCamera(){
		velocity = MainActivity.ZERO_VECTOR;
	}
	
	public float getX(){
		return face.getX();
	}
	
	public float getY(){
		return face.getY();
	}

}