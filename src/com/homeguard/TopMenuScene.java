package com.homeguard;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;


//placeHolder scene class for the main menu, currently only includes a start menu item 
public class TopMenuScene extends MenuScene implements IOnMenuItemClickListener {
	MainActivity activity;
	final int MENU_START = 0;
 
	public TopMenuScene() {
		super(MainActivity.getSharedInstance().mCamera);
		activity = MainActivity.getSharedInstance();
		activity.setCurrentScene(this);

		setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		IMenuItem startButton = new TextMenuItem(MENU_START, activity.mFont, "Start", activity.getVertexBufferObjectManager());
		startButton.setScale(2);
		startButton.setPosition(mCamera.getWidth() / 2 - startButton.getWidth() / 2, mCamera.getHeight() / 2 - startButton.getHeight() / 2);

		addMenuItem(startButton);
		
		setOnMenuItemClickListener(this);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene arg0, IMenuItem arg1, float arg2, float arg3) {
		switch (arg1.getID()) {
		case MENU_START:
			activity.setCurrentScene(new GameScene());
			return true;
		default:
			break;
		}
		return false;
	}

}
