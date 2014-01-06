package team.hdcheese.input;

import team.hdcheese.events.GameEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CommandSendEvent implements Poolable, GameEvent<CommandListener>{
	
	public Command command = null;
	public int index = 0;
	
	public void setup(int inputIndex, Command command) {
		index = inputIndex;
		this.command = command;
	}
	
	@Override
	public void reset() {
		index = 0;
		this.command = null;
	}

	@Override
	public void notify(CommandListener listener) {
		listener.handleCommandSent(this);
		Gdx.app.log("start event", command.toString());
	}

}
