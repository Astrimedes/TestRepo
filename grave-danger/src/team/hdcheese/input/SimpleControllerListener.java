package team.hdcheese.input;

import team.hdcheese.GameSession;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;

public class SimpleControllerListener implements ControllerListener {

	// currently uses xbox360 controller values

	private int index = -1;

	public static final int VERTICAL_AXIS = 0;
	public static final int HORIZONTAL_AXIS = 1;
	
	public static final int POV_CODE = 0;

	public SimpleControllerListener(int index) {
		this.index = index;
	}

	@Override
	public void connected(Controller controller) {

		//GameSession.eventService.notify(new ControllerConnectEvent(controller));

		Gdx.app.log("controller" + index, "Connected: " + controller.getName());
	}

	@Override
	public void disconnected(Controller controller) {

		//GameSession.eventService.notify(new ControllerDisconnectEvent(controller));

		Gdx.app.log("controller" + index, "Dis-connected " + controller.getName() + "!");
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		Gdx.app.log("controller" + index, buttonCode + " button down");
		return true;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		Gdx.app.log("controller" + index, buttonCode + " button up");
		return false;
	}

	public void startEvent(Command command) {
		CommandSendEvent start = Pools.obtain(CommandSendEvent.class);
		start.setup(index, command);
		GameSession.eventService.notify(start);
		Pools.free(start);
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		
		Gdx.app.log("controller" + index, "Axis " + axisCode + " to " + value);
		
//		if (Math.abs(value) > InputConstants.DEAD_ZONE) {
//			
//			// Horizontal
//			if (axisCode == HORIZONTAL_AXIS) {
//				if (value < -InputConstants.DEAD_ZONE) {
//					// left
//					startEvent(Command.MOVE_LEFT);
//				} else if (value > InputConstants.DEAD_ZONE) {
//					// right
//					startEvent(Command.MOVE_RIGHT);
//				} else {
//					// center of X
//					endEvent(Command.MOVE_RIGHT);
//					endEvent(Command.MOVE_LEFT);
//				}
//			}
//			// Vertical
//			if (axisCode == VERTICAL_AXIS) {
//				if (value < -InputConstants.DEAD_ZONE) {
//					// up
//					startEvent(Command.MOVE_UP);
//				} else if (value > InputConstants.DEAD_ZONE) {
//					// down
//					startEvent(Command.MOVE_DOWN);
//				} else {
//					// center of Y
//					endEvent(Command.MOVE_UP);
//					endEvent(Command.MOVE_DOWN);
//				}
//			}
//		} else {
//			// center
//			endEvent(Command.MOVE_RIGHT);
//			endEvent(Command.MOVE_LEFT);
//			endEvent(Command.MOVE_UP);
//			endEvent(Command.MOVE_DOWN);
//		}
		return true;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode,
			PovDirection value) {
		Gdx.app.log("controller" + index, "Pov " + povCode + " to " + value);
				
//				if (value == PovDirection.west) {
//					//left
//					startEvent(Command.MOVE_LEFT);
//				} else if (value == PovDirection.east) {
//					//right
//					startEvent(Command.MOVE_RIGHT);
//				}
//				
//				if (value == PovDirection.northEast) {
//					startEvent(Command.MOVE_RIGHT);
//					startEvent(Command.MOVE_UP);
//				} else if (value == PovDirection.northWest) {
//					startEvent(Command.MOVE_LEFT);
//					startEvent(Command.MOVE_UP);
//				} else {
//					endEvent(Command.MOVE_RIGHT);
//					endEvent(Command.MOVE_UP);
//							endEvent(Command.MOVE_LEFT);
//				}
//				
//				if (value == PovDirection.north) {
//					//up
//					startEvent(Command.MOVE_UP);
//				} else if (value == PovDirection.south) {
//					//up
//					startEvent(Command.MOVE_DOWN);
//				} else {
//					
//				}
//				
//				if (value == PovDirection.center) {
//					endEvent(Command.MOVE_RIGHT);
//					endEvent(Command.MOVE_LEFT);
//					endEvent(Command.MOVE_UP);
//					endEvent(Command.MOVE_DOWN);
//				}
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode,
			boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode,
			boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller,
			int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}




}
