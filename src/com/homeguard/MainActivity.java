package com.homeguard;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.IBackground;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.svg.opengl.texture.atlas.bitmap.SVGBitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.graphics.Typeface;
import android.view.Display;
import com.badlogic.gdx.math.Vector2;


public class MainActivity extends SimpleBaseGameActivity{

	static final boolean DEV_MODE = false;
	static final Vector2 ZERO_VECTOR = new Vector2(0,0);
	
	//default font to use
	public Font mFont;
	
	//our camera width and height, will later be calculated to be equal to the screen resolution
	public int CAMERA_WIDTH;
	public int CAMERA_HEIGHT;
	
	public int SCREEN_WIDTH_NATIVE;
	public int SCREEN_HEIGHT_NATIVE;
	
	public float SCALAR = 0;
	
	//our camera
	public ZoomCamera mCamera;
	
	//the current scene
	public Scene mCurrentScene;
	
	//singleton instance for our activity
	public static MainActivity instance;
	
	//complete list of texture atlas's used for building various textures
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasMothership;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasSize1;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasSize2;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasSize3;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasHud;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasHudButtons;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasBackground1;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasBackground2;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasBackground3;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasBackground4;
	public BuildableBitmapTextureAtlas mBuildableBitmapTextureAtlasMisc;
	
	//workable textures
	public ITextureRegion mTexture_friendly_mothership;
	public ITextureRegion mTexture_friendly_fighter1;
	public ITextureRegion mTexture_friendly_fighter2;
	public ITextureRegion mTexture_friendly_fighter3;
	public ITextureRegion mTexture_enemy_fighter1;
	public ITextureRegion mTexture_enemy_fighter2;
	public ITextureRegion mTexture_enemy_fighter3;
	public ITextureRegion mTexture_turret1;
	public ITextureRegion mTexture_asteroid1;
	public ITextureRegion mParticleTextureRegion;	
	public ITextureRegion mTexture_background1;
	public ITextureRegion mTexture_background2;
	public ITextureRegion mTexture_background3;
	public ITextureRegion mTexture_background4;
	
	//workable hud textures
	public ITextureRegion mTexture_bud_bottomBack;
	public ITextureRegion mTexture_hud_topBack;
	
	//buttons for the HUD
	public ITextureRegion mTexture_hud_button_deselect;
	public ITextureRegion mTexture_hud_button_buildMiner;
	public ITextureRegion mTexture_hud_button_buildFighter;
	public ITextureRegion mTexture_hud_button_buildTurret;
	public ITextureRegion mTexture_hud_button_buildHealer;
	
	
	public VertexBufferObjectManager vertexBufferObjectManager;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		instance = this;
		
		final Display display = getWindowManager().getDefaultDisplay();
		
		SCREEN_WIDTH_NATIVE = display.getWidth();
		SCREEN_HEIGHT_NATIVE = display.getHeight();
		
		//SCREEN_WIDTH_NATIVE = 1080;
		//SCREEN_HEIGHT_NATIVE = 1920;
		
		CAMERA_WIDTH = 1080;
		CAMERA_HEIGHT = 1080 * SCREEN_HEIGHT_NATIVE / SCREEN_WIDTH_NATIVE;
		
		SCALAR = CAMERA_WIDTH / SCREEN_WIDTH_NATIVE;
		
		mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	protected void onCreateResources() {
		
		mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(), "fnt/akashi.ttf", 60, true, android.graphics.Color.WHITE);
		mFont.load();
		
		vertexBufferObjectManager = this.getVertexBufferObjectManager();
		
		this.mBuildableBitmapTextureAtlasMothership = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasSize1 = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasSize2 = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasSize3 = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasBackground1 = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasBackground2 = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasBackground3 = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasBackground4 = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasHud = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasHudButtons = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		this.mBuildableBitmapTextureAtlasMisc = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		
		SVGBitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mParticleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasMisc, this, "particle_point.png");
		this.mTexture_friendly_mothership = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasMothership, this, "mothership.png");
		
		this.mTexture_friendly_fighter1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "friendlyFighter1.png");
		this.mTexture_friendly_fighter2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "friendlyFighter2.png");
		this.mTexture_friendly_fighter3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "friendlyFighter3.png");
		
		this.mTexture_turret1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "turret1.png");
		
		this.mTexture_enemy_fighter1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "enemyFighter1.png");
		this.mTexture_enemy_fighter2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "enemyFighter2.png");
		this.mTexture_enemy_fighter3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "enemyFighter3.png");
		
		this.mTexture_asteroid1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize3, this, "asteroid1.png");
		
		this.mTexture_background1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasBackground1, this, "background2.png");
		this.mTexture_background2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasBackground2, this, "backgroundLayer1.png");
		this.mTexture_background3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasBackground3, this, "backgroundLayer2.png");
		this.mTexture_background4 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasBackground4, this, "backgroundLayer3.png");
		
		this.mTexture_bud_bottomBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "hudBottomBack.png");
		this.mTexture_hud_topBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "hudTopBack.png");
		
		this.mTexture_hud_button_deselect = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasSize2, this, "hudButtonDeselect.png");
		this.mTexture_hud_button_buildMiner = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasHudButtons, this, "hudButtonBuildMiner.png");
		this.mTexture_hud_button_buildFighter = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasHudButtons, this, "hudButtonBuildFighter.png");
		this.mTexture_hud_button_buildTurret = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasHudButtons, this, "hudButtonBuildTurret.png");
		this.mTexture_hud_button_buildHealer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBuildableBitmapTextureAtlasHudButtons, this, "hudButtonBuildHealer.png");

		
		try {
			this.mBuildableBitmapTextureAtlasMothership.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasMothership.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasSize1.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasSize1.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasSize2.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasSize2.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasSize3.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasSize3.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasHud.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasHud.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasHudButtons.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasHudButtons.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasBackground1.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasBackground1.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasBackground2.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasBackground2.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasBackground3.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasBackground3.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasBackground4.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasBackground4.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		try {
			this.mBuildableBitmapTextureAtlasMisc.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mBuildableBitmapTextureAtlasMisc.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
	}

	@Override
	protected Scene onCreateScene() {
		
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.setCurrentScene(new TopMenuScene());
		return mCurrentScene;
	}
	
	public void setCurrentScene(Scene scene) {
		mCurrentScene = null;
		mCurrentScene = scene;
		getEngine().setScene(mCurrentScene);
	}
	
	public static MainActivity getSharedInstance() {
		return instance;
	}
	
	public int getCameraWidth(){
		return CAMERA_WIDTH;
	}
	
	public int getCameraHeight(){
		return CAMERA_HEIGHT;
	}
	
	public static float getAngleToPoint(float x1, float y1, float x2, float y2){
		return (float) (Math.atan2(y2 - y1, x2 - x1));
	}
	
	public static Vector2 getUnitVector(float angle){
		return new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
	}
	
	public static float getVectorAngle(Vector2 vector){
		return (float) (Math.atan2(vector.y, vector.x));
	}
	
	public static float getDistance(float x1, float y1, float x2, float y2){
		return (float) Math.sqrt((Math.pow(x2-x1, 2)+ Math.pow(y2-y1,2)));
	}
	
	public static float getDistance2(float x1, float y1, float x2, float y2){
		return (float) (Math.pow(x2-x1, 2)+ Math.pow(y2-y1,2));
	}
	
}
