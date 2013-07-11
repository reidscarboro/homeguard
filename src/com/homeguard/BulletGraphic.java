package com.homeguard;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class BulletGraphic extends Rectangle{
	
	private FriendlyBullet bullet;
	
	public BulletGraphic(FriendlyBullet bullet, float x, float y, float width, float height, VertexBufferObjectManager vertexBufferObjectManager){
		super(x, y, width, height, vertexBufferObjectManager);
		
		this.bullet = bullet;
	}
	
	public FriendlyBullet getBullet(){
		return this.bullet;
	}
}
