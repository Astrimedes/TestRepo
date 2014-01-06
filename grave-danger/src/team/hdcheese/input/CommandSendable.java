package team.hdcheese.input;

/**
 * Used by any object that takes actions based on received commands - like world entities
 * @author Mike
 *
 */
public interface CommandSendable {
	
	public abstract void addCommand(Command command);
	
	public abstract void addRepeatCommand(Command command);
	
	public abstract void removeCommand(Command command);
	
	public abstract void removeRepeatCommand(Command command);
	
	public abstract void clearAllCommands();
	
}
