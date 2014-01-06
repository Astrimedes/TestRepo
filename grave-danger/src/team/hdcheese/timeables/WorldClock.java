package team.hdcheese.timeables;

import com.badlogic.gdx.utils.Array;


/**
 * A timer that can be used to fire events after given seconds, or at absolute times
 * It expects to be informed of all time increases by a game world in update(deltaTime)
 * @author mturano
 *
 */
public final class WorldClock {
	
	TimeSorter sorter = new TimeSorter();

	// current world time
	float currTime = 0;

	// live running list
	Array<Timeable> tasks = new Array<Timeable>();

	// waiting to be added
	Array<Timeable> nextTasks = new Array<Timeable>();

	// to be removed
	Array<Timeable> deadTasks = new Array<Timeable>();

	//float nextEventTime = Float.MAX_VALUE;

	/**
	 * creation: you should only need one to hold all the Timeables
	 * pass the current world time at begin, then calls to update will add to it
	 * @param world
	 */
	public WorldClock(float startingAbsoluteTime) {
		this.currTime = startingAbsoluteTime;
		//this.world = world;
	}

	/**
	 * add a task to the "next" list, evaluated and added next update
	 * @param tasks
	 */
	public void addTimeable(Timeable t) {
		nextTasks.add(t);
	}

	// this is meant to be used only internally!
	private void addTask(Timeable t) {
		t.setAbsoluteTime(currTime + t.getTimeInterval());
		this.tasks.add(t);
		// set next event time (add extra so we can always check for less than)
		//compareAndSetNextTime(t);
	}

	public void removeTimeable(Timeable t) {
		deadTasks.add(t);
	}
	
	public int getTaskCount() {
		return tasks.size;
	}
	
	public int getDeadTaskCount() {
		return deadTasks.size;
	}

	/**
	 * Add to passed time - this is the only way the clock's time is updated!
	 * @param deltaTime
	 */
	public void update(float deltaTime) {
		// update timer
		currTime += deltaTime;

		// remove dead from live list, add any follow-up tasks on cancel
		Timeable f = null;
		for(Timeable d : deadTasks) {
			// remove from live
			if (tasks.removeValue(d, true)) {
				// check for new tasks to be added on cancellation, if it was found and removed
				f = d.getNextTask();
				if (f != null) {
					this.addTimeable(f);
				}
			}
			
			// reset it!
			d.cleanUp();
		}

		// clear dead list
		deadTasks.clear();

		// add any waiting tasks...
		if (nextTasks.size > 0) {
			for (Timeable n : nextTasks) {
				addTask(n);
			}

			nextTasks.clear();
		}
		
		// sort!
		//Collections.sort(tasks, sorter);
		tasks.sort();

		int updates = 0;

		// if we're at a scheduled time...
//		if (currTime > nextEventTime) {
			// check live ones

			for (Timeable t : tasks) {
				
				if (t.getAbsoluteTime() > this.currTime) {
					return;
				}

				updates = 0;

				// check if this Timeable's time has come...
//				if (!t.isCancelled() && t.getAbsoluteTime() < currTime) {
				if (!t.isCancelled()) {

					updates = (int)Math.floor((currTime - t.getAbsoluteTime()) / t.getTimeInterval());

					while (updates >= 1 && !t.isCancelled()) {
						// Do the event! - doing it here means the event itself could modify it's own parameters
						// 	and have them affect the timed execution logic below...
						t.run();

						// if repeating, set next time
						if (t.isRepeating()) {
							t.setAbsoluteTime(currTime + t.getTimeInterval());
						} else {
							// set to cancel if it doesn't repeat
							t.setCancelled(true);
						}

						updates--;
					}
				}

				// remove if canceled
				if (t.isCancelled()) {
					// add to dead
					deadTasks.add(t);
				} 
//				else {
//					// find next event time
//					compareAndSetNextTime(t);
//				}
				
//			}
		}
		
	}

	// sets the next time to execute if this tasks' scheduled time is low enough
//	private void compareAndSetNextTime(Timeable t) {
//		// check for next event (add extra so we can always check for less than)
//		
//		//nextEventTime = Math.min(nextEventTime, t.getAbsoluteTime() + Float.MIN_VALUE);
//	}

}
