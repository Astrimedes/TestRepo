package team.hdcheese.input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CommandListener implements Poolable{
	
	int inputIndex = 0;
	private CommandSendable entity;
	
	public CommandListener() {
	}
	
	public CommandListener(int inputIndex, CommandSendable entity) {
		setup(inputIndex, entity);
	}
	
	public void setup(int inputIndex, CommandSendable entity) {
		this.entity = entity;
		this.inputIndex = inputIndex;
	}
	
	public void handleCommandSent(CommandSendEvent start) {
		if (start.index == inputIndex) {
			Gdx.app.log("Start Command", "index: " + inputIndex + ", Command: " + start.command);
			entity.addCommand(start.command);
		}
	}

	@Override
	public void reset() {
		inputIndex = 0;
		entity = null;
	}
	
}
