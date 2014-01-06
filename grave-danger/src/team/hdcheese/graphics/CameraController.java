package team.hdcheese.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import team.hdcheese.input.Command;
import team.hdcheese.input.CommandSendable;

public class CameraController implements CommandSendable{

	public OrthographicCamera camera;

	public boolean isMoving = false;

	public boolean needsUpdate = false;

	public float moveRate = 500;

	public Vector2 target = new Vector2();
	public boolean seeking = false;

	public Vector2 velocity = new Vector2();

	private int defaultZoomIndex;

	private int zoomIndex;
	private int zoomTargetIdx;

	private float zoomFudge;

	private Array<Command> commands = new Array<Command>();

	Vector2 lastPosition = new Vector2();

	private Float[] zoomLevels;

	public float getTargetZoom() {
		return zoomLevels[zoomTargetIdx];
	}

	public CameraController(float moveRate) {
		zoomLevels = new Float[] {
				0.25f, 0.5f, 0.75f,
				1.0f,
				1.25f, 1.5f, 1.75f,
				2.0f
		};

		this.moveRate = moveRate;

		defaultZoomIndex = 3;
		zoomIndex = defaultZoomIndex;
		zoomTargetIdx = defaultZoomIndex;

		zoomFudge = 0.01f;
	}

	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
		lastPosition.set(camera.position.x, camera.position.y);
	}

	@Override
	public void addCommand(Command command) {
		if (!commands.contains(command, false)) {
			commands.add(command);
		}
	}

	@Override
	public void removeCommand(Command command) {
		commands.removeValue(command, false);
	}

	@Override
	public void clearAllCommands() {
		commands.clear();
	}


	public void setTarget(float x, float y) {
		if (Math.abs(x - camera.position.x) + Math.abs(y - camera.position.y) > 100) {
			target.x = Math.round(x);
			target.y = Math.round(y);
			seeking = true;
		}
	}

	public void update(float delta) {

		needsUpdate = false;
		isMoving = false;

		float newX = camera.position.x;
		float newY = camera.position.y;

		if (seeking) {
			isMoving = true;

			float diffX = target.x - camera.position.x;
			float diffY = target.y - camera.position.y;

			float total = Math.abs(diffX) + Math.abs(diffY);

			if (total > 1) {
				newX += Math.signum(diffX) * delta * Math.max(moveRate * (Math.abs(diffX)/500), 0.1f);
				newY += Math.signum(diffY) * delta * Math.max(moveRate * (Math.abs(diffY)/500), 0.1f);
			} else {
				seeking = false;
				newX = target.x;
				newY = target.y;
			}

		}

		for(Command c : commands) {
			switch(c) {
			//			case MOVE_DOWN:
			//				isMoving = true;
			//				newY -= delta * moveRate;
			//				break;
			//			case MOVE_LEFT:
			//				isMoving = true;
			//				newX -= delta * moveRate;
			//				break;
			//			case MOVE_RIGHT:
			//				isMoving = true;
			//				newX += delta * moveRate;
			//				break;
			//			case MOVE_UP:
			//				isMoving = true;
			//				newY += delta * moveRate;
			//				break;
			case ZOOM_UP:
				if (zoomIndex != zoomTargetIdx) {
					zoomTargetIdx = Math.max(zoomIndex - 1, zoomTargetIdx);
				} else {
					zoomTargetIdx = zoomIndex - 1;
				}
				break;
			case ZOOM_DOWN:
				if (zoomIndex != zoomTargetIdx) {
					zoomTargetIdx = Math.min(zoomIndex + 1, zoomTargetIdx);
				} else {
					zoomTargetIdx = zoomIndex + 1;
				}
				break;
			default:
				break;
			}
		}
		// clear commands
		commands.clear();

		// process zooming
		if (zoomIndex != zoomTargetIdx) {

			needsUpdate = true;

			// lock to upper/lower bounds
			if (zoomTargetIdx < 0) {
				zoomTargetIdx = 0;
			} else if (zoomTargetIdx > zoomLevels.length-1) {
				zoomTargetIdx = zoomLevels.length-1;
			}

			// interpolate towards new position
			camera.zoom = Interpolation.linear.apply(camera.zoom, zoomLevels[zoomTargetIdx], 
					MathUtils.clamp(delta * 10, 0.001f, 0.1f));
			// lock to the target if near enough
			if (Math.abs(camera.zoom - zoomLevels[zoomTargetIdx]) < zoomFudge) {
				camera.zoom = zoomLevels[zoomTargetIdx];
				zoomIndex = zoomTargetIdx;
			}
		}

		// position update
		if (isMoving) {
			needsUpdate = true;

			// last position
			lastPosition.set(camera.position.x, camera.position.y);

			// set camera position
			if (Math.abs(newX - camera.position.x) + Math.abs(newY - camera.position.y) > 10) {
				camera.position.x = Interpolation.linear.apply(lastPosition.x, newX, 0.4f);
				camera.position.y = Interpolation.linear.apply(lastPosition.y, newY, 0.4f);
			} else {
				camera.position.x = newX;
				camera.position.y = newY;
			}
		} else {
			velocity.set(0,0);
		}

		// update camera if necessary
		if (needsUpdate) {
			camera.position.x = (float)Math.floor(camera.position.x);
			camera.position.y = (float)Math.floor(camera.position.y);
			camera.update();
		}
	}

	public void setPosition(float x, float y) {
		lastPosition.set(x, y);
		camera.position.set(x, y, 0);
		camera.update();
	}

	@Override
	public void addRepeatCommand(Command command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeRepeatCommand(Command command) {
		// TODO Auto-generated method stub
		
	}



}
