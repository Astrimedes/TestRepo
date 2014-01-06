package team.hdcheese.timeables;

/**
 * Generic class that has the underlying fields for very basic Timeable implementation
 * @author Mike
 *
 */
public class AnyTimeable implements Timeable {
	
	protected float absoluteTime = Float.MAX_VALUE;
	protected float timeInterval = 999;
	protected boolean repeats = false;
	protected boolean cancelled = false;
	protected AnyTimeable nextTimeable = null;

	public AnyTimeable() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isRepeating() {
		return repeats;
	}

	@Override
	public float getAbsoluteTime() {
		return absoluteTime;
	}

	@Override
	public void setAbsoluteTime(float time) {
		absoluteTime = time;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public float getTimeInterval() {
		return timeInterval;
	}

	@Override
	public void run() {
		// TODO DO SOMETHING HERE
	}

	@Override
	public Timeable getNextTask() {
		return nextTimeable;
	}

	@Override
	public void cleanUp() {
		// TODO RESET VALUES IF POOLING NEEDED
	}
	
	@Override
	public int compareTo(Timeable o) {
		float otherTime = o.getAbsoluteTime();
		if (absoluteTime < otherTime) {
			return -1;
		}
		if (absoluteTime > otherTime) {
			return 1;
		}
		return 0;
	}

}
