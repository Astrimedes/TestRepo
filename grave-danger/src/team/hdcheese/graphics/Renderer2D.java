package team.hdcheese.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Typical use is to use setWorldViewport with a square viewport (or a dimension
 * set to 0), and then call either uniformizer according to the side you need to
 * align to.
 * 
 * @author "obli" (forum user at
 *         http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=1940)
 */
public class Renderer2D {

	private static boolean isTransparent = false;

	protected int drawnObjects = 0;
	public int getDrawnObjectCount(){return drawnObjects;}

	protected int culledObjects = 0;
	public int getCulledObjectCount(){return culledObjects;}

	public float graphicsScale = 1; // 1 unit = 1 pixel by default

	protected float renderedTime = 0;
	protected float lastSortedTime = 0;

	protected Color tmpColor = new Color();

	protected boolean initialized = false;

	protected Vector2 screenSizeMeters = new Vector2();
	protected Vector2 screenSizePixels = new Vector2();

	protected Vector2 pixelsPerMeter = new Vector2();
	protected Vector2 metersPerPixel = new Vector2();

	protected OrthographicCamera worldCamera;

	protected OrthographicCamera screenCamera;

	// temp string builder used for text drawing
	//private StringBuilder sb = new StringBuilder("  ");

	// temp vector used for projections
	protected final Vector3 v3 = new Vector3();

	// used to create bounding box for culling
	protected final Vector3 v3Min = new Vector3();
	protected final Vector3 v3Max = new Vector3();

	// spriteBatch
	public SpriteBatch batch = new SpriteBatch();

	// shapeBatch
	public ShapeRenderer shapeRenderer = new ShapeRenderer();

	// conversion
	public float getPixelsPerMeterX() {
		return pixelsPerMeter.x;
	}

	// conversion
	public float getPixelsPerMeterY() {
		return pixelsPerMeter.y;
	}

	// conversion
	public float getMetersPerPixelX() {
		return metersPerPixel.x;
	}

	// conversion
	public float getMetersPerPixelY() {
		return metersPerPixel.y;
	}

	/**
	 * @return the worldCamera
	 */
	public OrthographicCamera getWorldCamera() {
		return worldCamera;
	}

	/**
	 * @param worldCamera
	 *            the worldCamera to set
	 */
	public void setWorldCamera(OrthographicCamera worldCamera) {
		this.worldCamera = worldCamera;
	}

	/**
	 * @return the screenCamera
	 */
	public OrthographicCamera getScreenCamera() {
		return screenCamera;
	}

	/**
	 * @param screenCamera
	 *            the screenCamera to set
	 */
	public void setScreenCamera(OrthographicCamera screenCamera) {
		this.screenCamera = screenCamera;
	}

	// *** Constructor
	public Renderer2D() {
	}

	/**
	 * Load any assets required by the renderer directly - like fonts
	 * Call when the resources have been loaded into GraphicsWarehouse
	 * Does not actually load assets
	 * @return
	 */
	public boolean loadResources() {
		return true;
	}

	/**
	 * De-reference resources used directly by the renderer
	 */
	public void unloadResources() {
	}

	// initializes a renderer where the units : pixels ratio is based on values
	// set here
	// applied to the current resolution
	public void setScale(float graphicsScale, float worldUnitsViewWidth, float worldUnitsViewHeight) {

		// graphics scale
		this.graphicsScale = graphicsScale;

		// world camera
		setWorldCamera(new OrthographicCamera(worldUnitsViewWidth,
				worldUnitsViewHeight));
		// center
		worldCamera.position.x = worldCamera.viewportWidth / 2;
		worldCamera.position.y = worldCamera.viewportHeight / 2;

		// get screen dimensions
		screenSizePixels.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// screen camera (hud)
		setScreenCamera(new OrthographicCamera(screenSizePixels.x,
				screenSizePixels.y));
		// center
		screenCamera.position.x = screenCamera.viewportWidth / 2;
		screenCamera.position.y = screenCamera.viewportHeight / 2;

		// viewable world size (will be adjusted for aspect ratio)
		screenSizeMeters.set(worldUnitsViewWidth, worldUnitsViewHeight);

		// ok to proceed
		initialized = true;

		// tell it to resize to correct sizing for aspect ratio
		setWorldViewportUniform();

		// now apply these changes
		updateViewport();
	}

	/**
	 * Correct for a resolution change. shouldn't need to be called otherwise.
	 */
	public void correct() {

		if (!initialized) {
			return;
		}

		// get screen dimensions
		screenSizePixels.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// tell world camera to resize to correct sizing for aspect ratio
		setWorldViewportUniform();

		// now apply these changes
		updateViewport();
	}

	/**
	 * Sets the renderer viewport to display the specified world size when the
	 * camera zoom is at 1.
	 * 
	 * @param metersX
	 *            The word viewport width.
	 * @param metersY
	 *            The world viewport height.
	 */
	public void setWorldViewport(float metersX, float metersY) {

		if (!initialized) {
			return;
		}

		screenSizeMeters.set(metersX, metersY);
		updateViewport();
	}

	/**
	 * Changes the world viewport with a uniform pixels per meter ratio.
	 */
	public void setWorldViewportUniform() {

		if (!initialized) {
			return;
		}

		float pxRatio = screenSizePixels.x / screenSizePixels.y;
		float mtRatio = screenSizeMeters.x / screenSizeMeters.y;

		if (pxRatio >= mtRatio) {
			screenSizeMeters.y = screenSizeMeters.x / pxRatio;
		} else {
			screenSizeMeters.x = screenSizeMeters.y * pxRatio;
		}

		updateViewport();
	}

	/**
	 * Changes the world viewport with a uniform pixels per meter ratio. Will
	 * try to fill the screen.
	 */
	public void setWorldViewportUniformToFill() {

		if (!initialized) {
			return;
		}

		float pxRatio = screenSizePixels.x / screenSizePixels.y;
		float mtRatio = screenSizeMeters.x / screenSizeMeters.y;

		if (pxRatio >= mtRatio) {
			screenSizeMeters.x = screenSizeMeters.y * pxRatio;
		} else {
			screenSizeMeters.y = screenSizeMeters.x / pxRatio;
		}

		updateViewport();
	}

	private void updateViewport() {

		if (!initialized) {
			return;
		}

		pixelsPerMeter.x = screenSizePixels.x / screenSizeMeters.x;
		pixelsPerMeter.y = screenSizePixels.y / screenSizeMeters.y;
		metersPerPixel.x = screenSizeMeters.x / screenSizePixels.x;
		metersPerPixel.y = screenSizeMeters.y / screenSizePixels.y;

		worldCamera.viewportWidth = screenSizeMeters.x;
		worldCamera.viewportHeight = screenSizeMeters.y;
		worldCamera.zoom = 1.0f;
		worldCamera.update();

		screenCamera.viewportWidth = screenSizePixels.x;
		screenCamera.viewportHeight = screenSizePixels.y;
		screenCamera.zoom = 1.0f;
		screenCamera.update();
	}

	public static void setTransparency(boolean transparent) {
		if (transparent) {
			isTransparent = true;
			Gdx.gl.glEnable(GL10.GL_BLEND);
			Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		} else {
			isTransparent = false;
			Gdx.gl.glDisable(GL10.GL_BLEND);
		}
	}

	public static boolean getTransparency() {
		return isTransparent;
	}

	// pass a Vector3 containing world coordinates to transform to screen coordinates
	// MODIFIES THE VECTOR3 in order to allow it's usage as a return value
	public void projectToScreen(Vector3 projectionVector) {
		getWorldCamera().project(projectionVector);
	}
}
