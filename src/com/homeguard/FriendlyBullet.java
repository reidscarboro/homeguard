package com.homeguard;

import org.andengine.engine.handler.IUpdateHandler;
import com.badlogic.gdx.math.Vector2;

public class FriendlyBullet {
	
	MainActivity activity;
	GameScene scene;
	
	public boolean isActive;

	private Vector2 position;
	public Vector2 velocity;
	
	private BulletGraphic face;
	
	private float width = 40;
	private float height = 1;
	
	private int hullDamage = 20;
	private int shieldDamage = 10;
	
	private float speed = 20;
	
	private int lifeSpan = 50;
	private int lifeSpanInitial = 50;

	public FriendlyBullet(){
		
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
					}
					position.add(velocity);
					face.setPosition(position.x - width/2, position.y - height/2);
					
					enemyLoop:
					for (int i = 0; i < scene.touchableListEnemy.size(); i++) {
						if(MainActivity.getDistance2(scene.touchableListEnemy.get(i).getX(), scene.touchableListEnemy.get(i).getY(), position.x, position.y) < 
						   (scene.touchableListEnemy.get(i).getFace().getWidth()/2) * (scene.touchableListEnemy.get(i).getFace().getWidth()/2)){
							deactivate();
							scene.touchableListEnemy.get(i).damage(hullDamage, shieldDamage);
							break enemyLoop;
						}
					}
					
				}
				
			}
		});
		
	}
	
	public void set(Vector2 position, Vector2 velocity){
		
		this.position = position;
		this.velocity = velocity.mul(speed);
		
		face = new BulletGraphic(this, position.x - width/2, position.y - height/2, width, height, activity.vertexBufferObjectManager);
		face.setColor(.4f, 1, .4f);
		face.setRotationCenter(width/2, height/2);
		face.setRotation(MainActivity.getVectorAngle(velocity) * 360 / (2 * (float) Math.PI)); 

		face.setIgnoreUpdate(false);
		face.setVisible(true);
		
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
		
		FriendlyBulletPool.putBullet(this);
	}
}
