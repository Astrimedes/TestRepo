package team.hdcheese;
import team.hdcheese.GameScreen;
import team.hdcheese.GameSession;
import team.hdcheese.LoadingScreen;
import team.hdcheese.MainMenuScreen;
import team.hdcheese.audio.Audio;
import team.hdcheese.audio.MusicBank;
import team.hdcheese.audio.SoundBank;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class GdxGame implements ApplicationListener{

	private GameScreen screen;

	private GameScreen nextScreen;
	
	boolean autoDisplaySize = false;

	// holds a screen that needs to wait for transition screen before it can show itself
	private GameScreen delayedScreen = null;

	private boolean switching = false;
	
	// nothing special
	public GdxGame() {
		autoDisplaySize = false;
	}
	
	// set display params
	public GdxGame(boolean autoDisplaySize) {
		this.autoDisplaySize = autoDisplaySize;
	}
	
	@Override
	public void dispose () {
		if (screen != null) screen.dispose();
		GameSession.disposeAll();
	}

	@Override
	public void render () {
		try {
			float delta = Gdx.graphics.getDeltaTime();

			// normal-case rendering
			if (screen != null) {
				screen.render(delta);

				// do any transitioning
				screen.transition(delta);
			}

			// change screens!
			if (switching) {
				if (screen == null || screen.transitionDone) {
					changeScreens();
				}
			}

			// if screen is now null...
			if (screen == null) {
				// exit
				Gdx.app.exit();
				return;
			} else {
				if (screen == delayedScreen && screen.getTotalScreenTime() > 0) {
					delayedScreen = null;
				}
			}

		} catch(Exception e) {
			Gdx.app.log("HoardLord:Error", e.getMessage(), e);
		}
	}

	private void changeScreens() {

		switching = false;
		if (screen != null) {
			screen.unloadAssets();
			screen.dispose();
		}

		screen = nextScreen;

		if (screen != null) {
			screen.loadAssets();
			screen.show();

			// debug logging
			Gdx.app.log("Screens", "changeScreens To: " + screen.getClass().toString());
		} else {
			Gdx.app.log("Screens", "changeScreens To: null");
		}

	}

	@Override
	public void resize (int width, int height) {
		if (screen != null) screen.resize(width, height);
	}

	public boolean loadDelayedAssets() {
		if (delayedScreen != null) {
			return delayedScreen.loadAssets();
		}
		return true;
	}

	public void gotoDelayedScreen() {
		this.setScreen(delayedScreen, false);
	}

//	public void setScreen(ScreenType screenType, boolean useDelayScreen) {
//		GameScreen screen = null;
//		switch(screenType) {
//		case MAINMENU:
//			screen = new MainMenuScreen(screenType);
//			break;
//		case GAMEPLAY:
//			screen = new TileGameplayScreen(screenType, false);
//			break;
//		case OPTIONS:
//			screen = new OptionsScreen(screenType);
//			break;
////		case MISC:
////			screen = new ListScreen(screenType);
////			break;
//		default:
//			break;
//		}
//
//		setScreen(screen, useDelayScreen);
//	}

	/** Sets the current screen. {@link Screen#hide()} is called on any old screen, and {@link Screen#show()} is called on the new
	 * screen. */
	public void setScreen (GameScreen newScreen, boolean useDelayScreen) {

		// don't allow if already switching
		if (switching) {
			return;
		}

		switching = true;

		if (useDelayScreen) {
			this.delayedScreen = newScreen;
			this.nextScreen = new LoadingScreen();
		} else {
			this.nextScreen = newScreen;
		}

		if (this.screen != null) {
			this.screen.hide();
		}

		// debug logging
		if (newScreen != null) {
			Gdx.app.log("Screens", "setScreen To: " + newScreen.getClass().toString());
		} else {
			Gdx.app.log("Screens", "setScreen To: null");
		}

	}

	/** @return the currently active {@link Screen}. */
	public GameScreen getScreen () {
		return screen;
	}

	/**
	 * Game initial creation
	 */
	@Override
	public void create() {
		
		if (autoDisplaySize) {
			
			int height = Math.round(Gdx.graphics.getDesktopDisplayMode().height * 0.9f);
			int width = Math.round(Gdx.graphics.getDesktopDisplayMode().width * 0.75f);
			
			Gdx.graphics.setDisplayMode(width, height, false);
		}

		// static universal access...
		GameSession.setup(this);

		// prevent normal function of Back
		Gdx.input.setCatchBackKey(true);

		// set to main menu
		setScreen(new MainMenuScreen(), false);
	}
	/**
	 * Pause response
	 */
	@Override
	public void pause() {
		if (screen != null) {
			// make underlying screen pause
			getScreen().pause();
		}
	}

	/**
	 * Resume from Pause response
	 */
	@Override
	public void resume() {
		// recreate objects disposed during pause
		//		batch = new SpriteBatch();
		//		shapeRenderer = new ShapeRenderer();

		// make underlying screen resume
		getScreen().resume();
	}

	public GameScreen getDelayedScreen() {
		return this.delayedScreen;
	}
	
	private Audio audio = new Audio(new MusicBank(), new SoundBank());
	public Audio getAudio() {
		return audio;
	}

	public SoundBank getSound() {
		return audio.sound;
	}

	public MusicBank getMusic() {
		return audio.music;
	}
}

