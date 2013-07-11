package com.homeguard;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Line;

import com.badlogic.gdx.math.Vector2;

public class FriendlyFighterLaser {
	
	MainActivity activity;
	GameScene scene;
	
	public boolean isActive;
	
	private Line face;
	
	private float alpha = 1.0f;
	
	private float width = 1;
	
	private int lifeSpan = 40;
	private int lifeSpanInitial = 40;

	public FriendlyFighterLaser(){
		
		activity = MainActivity.getSharedInstance();
		scene = GameScene.getSharedInstance();
		
		isActive = false;
		
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				
				if (isActive){
					if (lifeSpan <= 0){
						deactivate();
						
					}
					else {
						lifeSpan--;
						alpha -= 0.03f;
						if (alpha <= 0){
							alpha = 0;
						}
						face.setAlpha(alpha);
					}
					
				}
				
			}
		});
		
	}
	
	public void set(float x1, float y1, float x2, float y2){
		
		face = new Line(x1, y1, x2, y2, activity.vertexBufferObjectManager);
		face.setColor(0.3f, 1, 0.8f);
		face.setLineWidth(width);

		face.setIgnoreUpdate(false);
		face.setVisible(true);
		
		alpha = 1;
		
		scene.attachChild(face);
		
		isActive = true;
	}
	public void deactivate(){
		
		lifeSpan = lifeSpanInitial;
		
		face.setVisible(false);
		face.setIgnoreUpdate(true);
		
		face.clearEntityModifiers();
		face.clearUpdateHandlers();
		
		scene.detachChild(face); 
		face.detachSelf();
		
		isActive = false;
		
		FriendlyFighterLaserPool.putLaser(this);
	}
}
