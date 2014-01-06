package team.hdcheese;

/**
 * @author Mike
 *
 */
public class GdxGameConstants {
	
	// if we're forcing android app to stay awake
	public static final boolean PREVENT_SLEEP = true;
	
	/**
	 * Fixed time step for world advancement
	 */
	public static final int TIME_STEPS_PER_SECOND = 30;
	public static final float TIME_STEP = 1.0f/TIME_STEPS_PER_SECOND;
	
	// value to scale graphics by for proper sizing from base resolution
	public static final float GRAPHICS_SCALE = 0.0073f; // about 1/137
	
	// the name of the preferences file where score is stored
	public static final String PREF_NAME = "GdxGamePrefs";
	
	public static final float TRANSITION_TIME = 0.6f;
	public static final float MIN_TRANSITION_DELTA = GdxGameConstants.TIME_STEP * 0.75f;
	
}
