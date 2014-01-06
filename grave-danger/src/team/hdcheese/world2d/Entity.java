package team.hdcheese.world2d;

import team.hdcheese.input.Command;
import team.hdcheese.input.CommandSendable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

public class Entity implements Poolable, CommandSendable {

	public Color color = new Color();

	public Vector2 lastCornerPosition = new Vector2();

	World world;

	public Vector2 impulse = new Vector2();
	public Vector2 velocity = new Vector2();

	Array<Command> commands = new Array<Command>();
	Array<Command> repeatCommands = new Array<Command>();

	public Circle circle = new Circle();

	float moveRate = 200;

	// tile indices that have been registered with for collision
	public TileCollection registeredTiles = new TileCollection();

	public Vector2 position = new Vector2();

	public Rectangle bounds = new Rectangle();

	float width = 0;
	float height = 0;

	/**
	 * setup this entity.  will add itself to the world.
	 * @param world
	 * @param posX
	 * @param posY
	 * @param width
	 * @param height
	 */
	public void set(World world, int posX, int posY, int width, int height) {

		color.set(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1.0f);

		this.world = world;

		world.addEntity(this);

		impulse.set(0,0);
		velocity.set(0,0);

		position.set(posX, posY);

		this.width = width;
		this.height = height;

		// circle radius
		circle.set(posX, posY, Math.max(width/2.0f, height/2.0f));

		bounds.set(position.x - (width/2.0f), position.y - (height/2.0f),
				width, height);
		lastCornerPosition.set(bounds.x, bounds.y);

		registerPosition();
	}

	@Override
	public void reset() {

		for(Tile t : registeredTiles) {
			// unregister from world!
			t.unregisterEntity(this);
		}
		registeredTiles.reset();

		if (world != null) {
			if (world.entities.contains(this, true)) {
				world.entities.removeValue(this, true);
			}
		}

		world = null;
	}

	@Override
	public void addCommand(Command command) {
		if (!commands.contains(command, true)) {
			commands.add(command);
		}
	}

	@Override
	public void removeCommand(Command command) {
		commands.removeValue(command, true);
	}

	@Override
	public void clearAllCommands() {
		commands.clear();
		repeatCommands.clear();
	}

	/**
	 * process commands, figure velocity
	 * @param dt
	 */
	public void update(float dt) {

		// add any repeated commands
		for(Command c : repeatCommands) {
			addCommand(c);
		}

		// add to velocity for commands
		for(Command c : commands) {
			switch(c) {
			case MOVE_DOWN:
				impulse.y -= moveRate;
				break;
			case MOVE_LEFT:
				impulse.x -= moveRate;
				break;
			case MOVE_RIGHT:
				impulse.x += moveRate;
				break;
			case MOVE_UP:
				impulse.y += moveRate;
				break;
			default:
				break;
			}
		}
		commands.clear();

		if (impulse.x != 0 || impulse.y != 0) {

			// scale for time
			impulse.scl(dt);

			// only add whole number portion to velocity
			velocity.set(((int)impulse.x - (impulse.x % 1)), 
					((int)impulse.y - (impulse.y % 1)));

			//moved = velocity.x != 0 || velocity.y != 0;

			impulse.x -= velocity.x;
			impulse.y -= velocity.y;
		}
//
//		if (moved) {
//			// check tiles in next position...
//			TileCollection tc = Pools.obtain(TileCollection.class);
//			Rectangle nextBounds = Pools.obtain(Rectangle.class);
//			nextBounds.set(bounds);
//			//			float halfWidth = bounds.width / 2.0f;
//			//			float halfHeight = bounds.height / 2.0f;
//
//			Circle nextCircle = Pools.obtain(Circle.class);
//			nextCircle.set(circle.center.x, circle.center.y, circle.radius);
//
//			// X Axis
//			if (Math.abs(velocity.x) > 0) {
//				//nextBounds.x += velocity.x;
//				nextCircle.center.x += velocity.x;
//				world.getOverlappingTiles(nextCircle, tc);
//				for (Tile t : tc) {
//					if (t.blocked) {
//						velocity.x = 0;
//						impulse.x = 0;
//					}
//					else if (t.occupiers.size > 0) {
//						for(Entity e : t.occupiers) {
//							if (e != this) {
//								float diff = e.circle.center.x - nextCircle.center.x;
//								if (Math.abs(diff) <= (e.circle.radius + circle.radius) &&
//										Math.signum(velocity.x) == Math.signum(diff) ) {
//									velocity.x = 0;
//									impulse.x = 0;
//								}
//							}
//						}
//					}
//				}
//			}
//
//			// Y Axis
//			if (Math.abs(velocity.y) > 0) {
//				//nextBounds.y += velocity.y;
//				nextCircle.center.x = circle.center.x + velocity.x;
//				nextCircle.center.y += velocity.y;
//				world.getOverlappingTiles(nextCircle, tc);
//				for (Tile t : tc) {
//					if (t.blocked) {
//						velocity.y = 0;
//						impulse.y = 0;
//					}
//					else if (t.occupiers.size > 0) {
//						for(Entity e : t.occupiers) {
//							if (e != this) {
//								float diff = e.circle.center.y - nextCircle.center.y;
//								if (Math.abs(diff) <= (e.circle.radius + circle.radius) &&
//										Math.signum(velocity.y) == Math.signum(diff) ) {
//									velocity.y = 0;
//									impulse.y = 0;
//								}
//							}
//						}
//					}
//				}
//			}
//
//			// add to position
//			position.add(velocity);
//
//			// remove velocity
//			velocity.set(0,0);
//
//			// update boundaries
//			updateBoundaries();
//
//			// register with overlappping tiles
//			registerPosition();
//
//			Pools.free(nextCircle);
//			Pools.free(nextBounds);
//			Pools.free(tc);
//		}
	}

	/**
	 * register with currently overlapping tiles
	 */
	public void registerPosition() {
		// clear old ones
		for(Tile t : registeredTiles) {
			// remove registration from old tile
			t.unregisterEntity(this);
		}
		registeredTiles.reset();
		// get new ones
		world.getOverlappingTiles(circle, registeredTiles);
		for(Tile t : registeredTiles) {
			// register with new tile
			t.registerEntity(this);
		}
	}

	void updateBoundaries() {
		//		lastCornerPosition.set(bounds.x, bounds.y);
		//		bounds.set(position.x - (width/2.0f), position.y - (height/2.0f),
		//				width, height);
		//lastCornerPosition.set(circle.center.x, circle.center.y);
		circle.set(position.x, position.y, circle.radius);
	}

	public void draw(ShapeRenderer shapeBatch, float delta) {
		float posX = Interpolation.linear.apply(lastCornerPosition.x, circle.center.x, world.alpha);
		float posY = Interpolation.linear.apply(lastCornerPosition.y, circle.center.y, world.alpha);
		shapeBatch.setColor(color);
		//shapeBatch.rect(posX,posY,bounds.width, bounds.height);
		shapeBatch.circle(posX, posY, circle.radius);
		lastCornerPosition.set(posX, posY);
	}

	@Override
	public void addRepeatCommand(Command command) {
		repeatCommands.add(command);
	}

	@Override
	public void removeRepeatCommand(Command command) {
		repeatCommands.removeValue(command, true);
	}
}
