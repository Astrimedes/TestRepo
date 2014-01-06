package team.hdcheese.events;

/**
 * Suggested on StackOverflow by Laurent Simon
 * http://stackoverflow.com/questions/937302/simple-java-message-dispatching-system
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Informs registered listeners of events
 * @author Mike & originally Laurent Simon on StackOverflow
 */
public final class EventService {

	/** mapping of class events to active listeners **/
	@SuppressWarnings("rawtypes")
	private final HashMap<Class,ArrayList> map = new HashMap<Class,ArrayList >( 20 );

	/**
	 * Add a listener to an event class
	 * @param evtClass Event Class
	 * @param listener Listener Object
	 */
	public <L> void listen( Class<? extends GameEvent<L>> evtClass, L listener) {
		final ArrayList<L> listeners = listenersOf( evtClass );
		synchronized( listeners ) {
			if ( !listeners.contains( listener ) ) {
				listeners.add( listener );
			}
		}
	}

	/** Stop sending an event class to a given listener **/
	public <L> void mute( Class<? extends GameEvent<L>> evtClass, L listener) {
		final ArrayList<L> listeners = listenersOf( evtClass );
		synchronized( listeners ) {
			listeners.remove( listener );
		}
	}

	/** Gets listeners for a given event class **/
	private <L> ArrayList<L> listenersOf(Class<? extends GameEvent<L>> evtClass) {
		synchronized ( map ) {
			@SuppressWarnings("unchecked")
			final ArrayList<L> existing = map.get( evtClass );
			if (existing != null) {
				return existing;
			}

			final ArrayList<L> emptyList = new ArrayList<L>(5);
			map.put(evtClass, emptyList);
			return emptyList;
		}
	}

	/** Notify a new event to registered listeners of this event class **/
	public <L> void notify( final GameEvent<L> evt) {
		@SuppressWarnings("unchecked")
		Class<GameEvent<L>> evtClass = (Class<GameEvent<L>>) evt.getClass();

		for ( L listener : listenersOf(  evtClass ) ) {
			evt.notify(listener);
		}
	}

}
