package team.hdcheese.timeables;

/**
 * Allows attaching to "world clock" in order to execute actions at world time intervals
 * @author Mike
 *
 */
public interface Timeable extends Comparable<Timeable>{
	
	/**
	 * if the task repeats
	 */	
	boolean isRepeating();
	
	/**
	 *  absolute world time until next interval passes
	 * @return
	 */
	float getAbsoluteTime();
	
	/**
	 * Allow WorldClock to set the time this will fire
	 * @param time
	 */
	void setAbsoluteTime(float time);
	
	/**
	 * The clock will check this before executing run(),
	 * only removing this Timeable but not calling run() if cancelled
	 * @return
	 */
	public boolean isCancelled();
	
	/**
	 * Allows the WorldClock to set this
	 * @param cancelled
	 * @return
	 */
	void setCancelled(boolean cancelled);
	
	/**
	 * Interval before next run()
	 * @return
	 */
	public float getTimeInterval();
	
	/**
	 * Execute the action at indicated time
	 */
	public void run();
	
	/**
	 * Specify a new task that should be generated at end of this one,
	 * or return null if none needed
	 * @return
	 */
	public Timeable getNextTask();
	
	/**
	 * Do things like return to appropriate pools, etc - if needed
	 */
	public void cleanUp();
	
	
}
