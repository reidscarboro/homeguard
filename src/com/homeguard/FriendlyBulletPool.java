package com.homeguard;

import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class FriendlyBulletPool {
	
	public static FriendlyBulletPool instance;
	public static LinkedBlockingQueue<FriendlyBullet> nullBullets = new LinkedBlockingQueue<FriendlyBullet>();
	
	public FriendlyBulletPool(){
		instance = this;
	}
	
	public static FriendlyBullet getBullet(){
		
		if (nullBullets.size() > 0)
			return nullBullets.poll();
		else 
			return new FriendlyBullet();
	}
	
	public static void putBullet(FriendlyBullet bullet){
		nullBullets.add(bullet);
	}

	public static FriendlyBulletPool getSharedInstance(){
		return instance;
	}
}
