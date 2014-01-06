package team.hdcheese;

import team.hdcheese.graphics.Renderer2D;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public abstract class GameScreen {

	/**  the main game object */
	protected GdxGame game = null;

	protected double totalScreenTime = 0;
	
	protected OrthographicCamera screenCamera = null;

	protected ShapeRenderer fadeRenderer;
	protected Color fadeColor = Color.BLACK.cpy();

	public boolean transitions = false;
	protected boolean fadingIn = false;	
	protected boolean fadingOut = false;
	
	protected float transitionTime = 0;

	public boolean transitionDone = true;
	
	/**
	 * If the screen has it's own 
	 * @return
	 */
	public SpriteBatch getSpriteBatch() {
		return null;
	}
	
	/**
	 * If the screen has it's own 
	 * @return
	 */
	public ShapeRenderer getShapeRenderer() {
		return null;
	}
	
	/**
	 * If the screen has it's own
	 * @return
	 */
	public OrthographicCamera getScreenCamera() {
		return null;
	}

	/** running total of elapsed seconds in this screen */
	public double getTotalScreenTime() {
		return totalScreenTime;
	}

	/** super constructor gets reference to game */
	public GameScreen(boolean useTransitions) {
		game = GameSession.getGame();
		transitions = useTransitions;
		transitionDone = !transitions;
	}

	/** perform asset loading, return true if successful */
	public abstract boolean loadAssets();

	/** Called when the screen should render itself. Accumulates time and clears screen.
	 * @param delta The time in seconds since the last render. */
	public void render (float delta) {
		totalScreenTime += delta;
		
		if (fadingIn && transitionTime > 0) {
			transitionDone = false;
		}

		// clear screen w/ black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	public void transition(float delta) {
		
		if (fadingIn && fadeRenderer == null) {
			fadeRenderer = getShapeRenderer();
			if (fadeRenderer == null) {
				fadeRenderer = new ShapeRenderer();
			}
		}
		
		if (transitions) {
			
			if (!transitionDone) {
				
				delta = Math.min(delta, GdxGameConstants.MIN_TRANSITION_DELTA);
				
				transitionTime -= delta;
				transitionDone = transitionTime < 0;
				if (fadingIn) {
					fadeColor.a = (transitionTime / GdxGameConstants.TRANSITION_TIME);
				}
				else if (fadingOut) {
					fadeColor.a = 1.0f - (transitionTime / GdxGameConstants.TRANSITION_TIME);
				}
				drawOverlay();
			}
			else {
				if (fadingOut) {
					// keep drawing black after transition out completes
					drawOverlay();
				} else {
					fadingIn = false;
					fadingOut = false;
				}
			}
		}
	}
	
	private void drawOverlay() {
		Renderer2D.setTransparency(true);
		fadeRenderer.setProjectionMatrix(screenCamera.combined);
		fadeRenderer.setColor(fadeColor);
		fadeRenderer.begin(ShapeType.Filled);
		fadeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		fadeRenderer.end();
		Renderer2D.setTransparency(false);
	}

	/** Called when this screen becomes the current screen for a {@link GdxGame}. */
	public void show () {
		
		if (screenCamera == null) {
			screenCamera = getScreenCamera();
			if (screenCamera == null) {
				screenCamera = new OrthographicCamera();
				screenCamera.setToOrtho(false);
			}
		}
		
		if (transitions) {
			fadingIn = true;
			fadingOut = false;
			fadeColor.a = 1.0f;
			transitionTime = GdxGameConstants.TRANSITION_TIME;
			transitionDone = false;
		} else {
			transitionTime = 0;
			fadingIn = false;
			fadingOut = false;
			transitionDone = true;
		}
	}

	/** Called when this screen is no longer the current screen for a {@link GdxGame}. */
	public void hide () {
		if (transitions) {
			fadingOut = true;
			fadingIn = false;
			fadeColor.a = 0;
			transitionTime = GdxGameConstants.TRANSITION_TIME;
			transitionDone = false;
		} else {
			transitionTime = 0;
			fadingOut = false;
			fadingIn = false;
			transitionDone = true;
		}
	}

	/** @see ApplicationListener#pause() */
	public abstract void pause ();

	/** @see ApplicationListener#resume() */
	public abstract void resume ();

	/** Called when this screen should release all resources. */
	public abstract void dispose ();	

	/** @see ApplicationListender#resize(int width, int height) */
	public void resize(int width, int height) {
		// this will grab current screen size and adjust accordingly
		Gdx.graphics.setDisplayMode(width, height, false);
	}

	/** Unload loaded assets */
	protected abstract void unloadAssets();

	public abstract boolean isPaused();

}
