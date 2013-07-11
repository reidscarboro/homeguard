package com.homeguard;

import java.util.concurrent.LinkedBlockingQueue;

public class FriendlyFighterLaserPool {
	
	public static FriendlyFighterLaserPool instance;
	public static LinkedBlockingQueue<FriendlyFighterLaser> nullLasers = new LinkedBlockingQueue<FriendlyFighterLaser>();
	
	public FriendlyFighterLaserPool(){
		instance = this;
	}
	
	public static FriendlyFighterLaser getLaser(){
		
		if (nullLasers.size() > 0)
			return nullLasers.poll();
		else 
			return new FriendlyFighterLaser();
	}
	
	public static void putLaser(FriendlyFighterLaser laser){
		nullLasers.add(laser);
	}

	public static FriendlyFighterLaserPool getSharedInstance(){
		return instance;
	}
}
