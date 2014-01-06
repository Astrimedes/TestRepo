package team.hdcheese.events;

/**
 * Suggested on StackOverflow by Laurent Simon
 * http://stackoverflow.com/questions/937302/simple-java-message-dispatching-system
 */

/** Interface to describe different kinds of events **/
public interface GameEvent<L> {
	public void notify (final L listener);
}
