package team.hdcheese;

import java.util.ArrayList;

import team.hdcheese.graphics.CameraController;
import team.hdcheese.input.Command;
import team.hdcheese.input.CommandListener;
import team.hdcheese.input.CommandSendEvent;
import team.hdcheese.world2d.Entity;
import team.hdcheese.world2d.World;
import team.hdcheese.world2d.Tile;
import team.hdcheese.world2d.TileCollection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class TiledScreen extends GameScreen {
	
	boolean loaded = false;

	OrthographicCamera camera;
	Matrix4 screenProjection;

	CameraController camControl;
	Entity player;

	ArrayList<Entity> enemies = new ArrayList<Entity>();

	CommandListener cameraInputListener;
	CommandListener playerMoveListener;

	float viewWidth = 750;
	float viewHeight = 750;

	BitmapFont font;

	float zoomAccumulator = 0;

	World world;

	TiledMap map;
	TiledMapTileLayer layer;

	ShapeRenderer shapeBatch;

	StringBuilder output = new StringBuilder("");

	OrthogonalTiledMapRenderer mapRenderer;

	public TiledScreen() {
		super(true);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean loadAssets() {
		
		if (loaded) {
			return true;
		}

		shapeBatch = new ShapeRenderer();

		font = GameSession.getMenuTool().getSkin().get("small", BitmapFont.class);
		font.scale(0.01f);

		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();

		setScreenProjection();

		camera.setToOrtho(false, (width/height) * viewWidth, viewHeight);
		camera.update();

		camControl = new CameraController(400);
		camControl.setCamera(camera);

		TmxMapLoader loader = new TmxMapLoader();
		map = loader.load("maps/untitled.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);

		layer = (TiledMapTileLayer) map.getLayers().get(0);

		// create world
		world = new World();
		world.load(map);

		// create player (adds itself to world)
		player = Pools.obtain(Entity.class);
		player.set(world, (int)((world.widthInTiles*world.tileWidth)/2.0f), 
				(int)((world.heightInTiles*world.tileHeight)/2.0f), 
				(int)Math.round(world.tileWidth * 0.75f), (int)Math.round(world.tileHeight * 0.75f));
		player.color.set(Color.YELLOW);

		// create enemies
		ArrayList<Tile> usedTiles = new ArrayList<Tile>();
		Tile t = null;
		for(int i = 0; i < 5; ++i) {
			Entity enemy = Pools.obtain(Entity.class);
			
			// set to random color
			enemy.color.set(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
			
			// look for non-blocked tile to place enemy in
			while(t == null) {
				t = world.tiles[MathUtils.random(0,world.widthInTiles-1)]
						[MathUtils.random(0,world.heightInTiles-1)];
				if (!t.blocked && !usedTiles.contains(t)) { 
					usedTiles.add(t);
				} else {
					t = null;
				}
			}
			
			enemy.set(world, (int)t.center.x, (int)t.center.y,
					(int)Math.round(world.tileHeight * 0.75f),
					(int)Math.round(world.tileHeight * 0.75f));
			enemies.add(enemy);
			
			t = null;
		}		

		// set camera to player
		camControl.setPosition(player.position.x, player.position.y);

		// camera zooming
		cameraInputListener = Pools.obtain(CommandListener.class);
		cameraInputListener.setup(0, camControl);
		GameSession.eventService.listen(CommandSendEvent.class, cameraInputListener);

		// player movement
		playerMoveListener = Pools.obtain(CommandListener.class);
		playerMoveListener.setup(0, player);
		GameSession.eventService.listen(CommandSendEvent.class, playerMoveListener);
		
		loaded = true;

		return true;
	}

	private void setScreenProjection() {
		// copy base screen projection matrix by copying from blank camera
		OrthographicCamera tmpCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		tmpCam.position.x = tmpCam.viewportWidth/2.0f;
		tmpCam.position.y = tmpCam.viewportHeight/2.0f;
		tmpCam.update();
		screenProjection = tmpCam.combined.cpy();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unloadAssets() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		// process input
		doInputEvents(delta);
		
		// enemies
		moveEnemies();

		// now update camera
		camControl.setTarget(player.position.x, player.position.y);
		camControl.update(delta);

		// update world
		world.update(delta);

		// figure debug text
		writeDebugText();

		// draw map
		mapRenderer.setView(camera);
		mapRenderer.render();

		// draw player-occupied tiles
		//TileCollection tiles = Pools.obtain(TileCollection.class);
		//		world.getOverlappingTiles(player.bounds, tiles);
		//world.getOverlappingTiles(player.circle, tiles);
		shapeBatch.setProjectionMatrix(camera.combined);
		shapeBatch.begin(ShapeType.Filled);
		for(Tile t : player.registeredTiles) {
			shapeBatch.setColor(Color.PINK);
			shapeBatch.rect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height);
		}
		//Pools.free(tiles);

		// draw enemy occupied tiles
		//tiles = Pools.obtain(TileCollection.class);
		for(Entity e : enemies) {
			//world.getOverlappingTiles(e.circle, tiles);
			for(Tile t : e.registeredTiles) {
				shapeBatch.setColor(Color.BLACK);
				shapeBatch.rect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height);
			}
		}
		//Pools.free(tiles);

		// draw entities
		for(Entity e : world.entities) {
			e.draw(shapeBatch, delta);
		}
		shapeBatch.end();

		mapRenderer.getSpriteBatch().setProjectionMatrix(screenProjection);
		mapRenderer.getSpriteBatch().begin();
		font.setColor(Color.YELLOW);
		font.drawMultiLine(mapRenderer.getSpriteBatch(), output, font.getSpaceWidth(), font.getLineHeight()*4);
		mapRenderer.getSpriteBatch().end();
	}
	
	private void moveEnemies() {
		for(Entity e : enemies) {
			if (MathUtils.randomBoolean(0.05f)) {
				final int MAX = 6;
				// decide to move or not
				int randMove = MathUtils.random(MAX);
				switch(randMove) {
				case 0:
					e.addRepeatCommand(Command.MOVE_DOWN);
					e.removeRepeatCommand(Command.MOVE_UP);
					break;
				case 1:
					e.addRepeatCommand(Command.MOVE_UP);
					e.removeRepeatCommand(Command.MOVE_DOWN);
					break;
				case 2:
					e.addRepeatCommand(Command.MOVE_LEFT);
					e.removeRepeatCommand(Command.MOVE_RIGHT);
					break;
				case 3:
					e.addRepeatCommand(Command.MOVE_RIGHT);
					e.removeRepeatCommand(Command.MOVE_LEFT);
					break;
				case MAX:
					e.clearAllCommands();
					break;
				}
			}
		}
	}

	private void writeDebugText() {
		// build a string describing current cell
		//		output.replace(0, output.length(), "CELL ID: ");
		//		// check current tile position
		//		Cell cell = layer.getCell((int)Math.floor(camera.position.x / layer.getTileWidth()), 
		//				(int)Math.floor(camera.position.y / layer.getTileHeight()));
		//		if (cell != null) {
		//			output.append(cell.getTile().getId());
		//		} else {
		//			output.append("NULL");
		//		}
		//		// camera position
		//		output.append(" at ");
		//		output.append((int)camera.position.x);
		//		output.append(" , ");
		//		output.append((int)camera.position.y);

		// fps
		//		output.replace(0, output.length(), "FPS: ");
		//		output.append(Gdx.graphics.getFramesPerSecond());
		// entity position
		output.delete(0, output.length());
		output.append("FPS: ");
		output.append(Gdx.graphics.getFramesPerSecond());
		output.append("\nCamera X: ");
		output.append(Math.round(camera.position.x * 1000.0f)/1000.0f);
		output.append(" Y: ");
		output.append(Math.round(camera.position.y * 1000.0f)/1000.0f);
		output.append("\nPlayer X: ");
		output.append(Math.round(player.lastCornerPosition.x * 1000.0f)/1000.0f);
		output.append(" Y: ");
		output.append(Math.round(player.lastCornerPosition.y * 1000.0f)/1000.0f);



	}

	@Deprecated
	void doCameraInput(float delta) {
		// exit
		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			game.setScreen(new MainMenuScreen(), false);
		}

		// set camera target
		if (Gdx.input.isTouched()) {
			// find touched position, camera seeks to that
			Vector3 projection = Pools.obtain(Vector3.class);
			projection.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			// translate touch position to world space
			camera.unproject(projection);
			// seek now
			camControl.setTarget(projection.x, projection.y);
			Pools.free(projection);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camControl.addCommand(Command.MOVE_RIGHT);
		} else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camControl.addCommand(Command.MOVE_LEFT);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camControl.addCommand(Command.MOVE_UP);
		} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			camControl.addCommand(Command.MOVE_DOWN);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
			camControl.addCommand(Command.ZOOM_DOWN);
		} else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
			camControl.addCommand(Command.ZOOM_UP);
		}

		// now update
		camControl.update(delta);
	}

	public void doInputEvents(float dt) {

		// exit
		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			game.setScreen(new MainMenuScreen(), false);
		}

		// set movement target
		if (Gdx.input.isTouched()) {
			// find touched position, camera seeks to that
			Vector3 projection = Pools.obtain(Vector3.class);
			projection.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			// translate touch position to world space
			camera.unproject(projection);

			// determine direction
			float diffX = projection.x - player.position.x;
			float diffY = projection.y - player.position.y;
			if (Math.abs(diffX - (player.circle.radius/2)) > 0) {
				if (diffX > 0) {
					GameSession.sendInputEvent(0, Command.MOVE_RIGHT);
				} else if (diffX < 0) {
					GameSession.sendInputEvent(0, Command.MOVE_LEFT);
				}
			}
			if (Math.abs(diffY - (player.circle.radius/2)) > 0) {
				if (diffY > 0) {
					GameSession.sendInputEvent(0, Command.MOVE_UP);
				} else if (diffY < 0) {
					GameSession.sendInputEvent(0, Command.MOVE_DOWN);
				}
			}

			Pools.free(projection);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			GameSession.sendInputEvent(0, Command.MOVE_RIGHT);
		} else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			GameSession.sendInputEvent(0, Command.MOVE_LEFT);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			GameSession.sendInputEvent(0, Command.MOVE_UP);
		} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			GameSession.sendInputEvent(0, Command.MOVE_DOWN);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
			GameSession.sendInputEvent(0, Command.ZOOM_DOWN);
		} else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
			GameSession.sendInputEvent(0, Command.ZOOM_UP);
		}
	}


	@Override
	public void resize(int width, int height) {
		float aspect = ((float)width) / ((float)height);
		camera.setToOrtho(false, aspect * viewWidth, viewHeight);
		camera.update();

		setScreenProjection();
	}

}


