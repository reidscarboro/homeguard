package com.homeguard;

import org.andengine.entity.primitive.Rectangle;

public class ClickController {
	
	MainActivity activity;
	GameScene scene;
	
	private int sensitivity = 128;
	
	private Rectangle face;
	
	
	public ClickController(int pX, int pY){
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();
		
		face = new Rectangle(pX-sensitivity/2, pY-sensitivity/2, sensitivity, sensitivity, activity.getVertexBufferObjectManager());
		face.setColor(0, 1, 0);
		face.setVisible(false);
		
		scene.attachChild(face);

		
	}
	
	
	public float getX(){
		return face.getX()+face.getWidth()/2;
	}
	
	public float getY(){
		return face.getY()+face.getHeight()/2;
	}
	
	public void setX(float pX){
		face.setX(pX-sensitivity/2);
	}
	
	public void setY(float pY){
		face.setY(pY-sensitivity/2);
	}
	
	public Rectangle getFace(){
		return face;
	}

}