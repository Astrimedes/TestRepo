package team.hdcheese.world2d;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class World {

	public float TIME_STEP = 1.0f/30.0f;
	public float timeAccumulator = 0;

	TiledMap tiledMap;
	TiledMapTileLayer[] layers;

	public Tile[][] tiles;

	float alpha = 0;

	Array<Entity> unbornEntities;
	public Array<Entity> entities;
	Array<Entity> deadEntities;

	public float tileWidth = 0;
	public float tileHeight = tileWidth;

	public int widthInTiles = 0;
	public int heightInTiles = 0;

	/**
	 * Create world definition from tiled map
	 * @param map
	 */
	public void load(TiledMap map) {
		tiledMap = map;

		layers = new TiledMapTileLayer[1];
		layers[0] = (TiledMapTileLayer) map.getLayers().get(0);

		unbornEntities = new Array<Entity>();
		entities = new Array<Entity>();
		deadEntities  = new Array<Entity>();

		tileWidth = (int) layers[0].getTileWidth();
		tileHeight = (int) layers[0].getTileHeight();

		widthInTiles = layers[0].getWidth();
		heightInTiles = layers[0].getHeight();

		tiles = new Tile[widthInTiles][heightInTiles];
		for(int x = 0; x < layers[0].getWidth(); ++x) {
			for(int y = 0; y < layers[0].getHeight(); ++y) {
				tiles[x][y] = Pools.obtain(Tile.class);
				tiles[x][y].set(this, x, y);
			}
		}
	}

	public Tile getTile(float xWorldPosition, float yWorldPosition) {
		Tile t = null;
		int x = (int)Math.floor(xWorldPosition/tileWidth);
		int y = (int)Math.floor(yWorldPosition/tileHeight);
		if (x >= 0 && x < widthInTiles && y >= 0 && y < heightInTiles) {
			t = tiles[x][y];
		}
		return t;
	}

	/**
	 * will set TileCollection with the tiles that the given rectangle is overlapping
	 * @param boundaries
	 * @param collection
	 * @return
	 */
	public TileCollection getOverlappingTiles(Rectangle boundaries, TileCollection collection) {
		collection.reset();
		float halfWidth = tileWidth / 2.0f;
		float halfHeight = tileHeight / 2.0f;

		int xLimit = (int)Math.ceil(boundaries.width / halfWidth);
		int yLimit = (int)Math.ceil(boundaries.height / halfHeight);

		float right = boundaries.x + boundaries.width;
		float top = boundaries.y + boundaries.height;

		Tile t = null;
		for(int xChecks = 0; xChecks <= xLimit; ++xChecks) {
			for (int yChecks = 0; yChecks <= yLimit; ++yChecks) {
				t = getTile(MathUtils.clamp(boundaries.x + (halfWidth * xChecks), boundaries.x, right),
						MathUtils.clamp(boundaries.y + (halfHeight * yChecks), boundaries.y, top));
				if (t != null) {
					collection.addTile(t);
				}
			}
		}
		return collection;
	}

	/**
	 * will set TileCollection with the tiles that the given rectangle is overlapping
	 * @param boundaries
	 * @param collection
	 * @return
	 */
	public TileCollection getOverlappingTiles(Circle circle, TileCollection collection) {
		collection.reset();

		float halfWidth = tileWidth / 2.0f;
		float halfHeight = tileHeight / 2.0f;

		int halfTilesWide = (int)Math.ceil(circle.radius / halfWidth);
		int halfTilesTall = (int)Math.ceil(circle.radius / halfHeight);

		float tmpX = 0;
		float tmpY = 0;

		for(int x = -halfTilesWide; x <= halfTilesWide; ++x) {
			for(int y = -halfTilesTall; y <= halfTilesTall; ++y) {
				tmpX = MathUtils.clamp((x * halfWidth) + circle.center.x, 
						circle.center.x - circle.radius, circle.center.x + circle.radius);
				tmpY = MathUtils.clamp((y * halfHeight) + circle.center.y, 
						circle.center.y - circle.radius, circle.center.y + circle.radius);
				collection.addTile(getTile(tmpX, tmpY));
			}
		}

		return collection;
	}

	@Deprecated
	public TileCollection getOverlappingTiles2(Circle circle, TileCollection collection) {
		collection.reset();

		float halfWidth = tileWidth / 2.0f;
		float halfHeight = tileHeight / 2.0f;

		int halfTilesWide = (int)Math.ceil(circle.radius / halfWidth);
		int halfTilesTall = (int)Math.ceil(circle.radius / halfHeight);

		float tmpX = 0;
		float tmpY = 0;

		Vector2 tmpVec = Pools.obtain(Vector2.class);
		float rad2 = (circle.radius * circle.radius) + (tileWidth * tileWidth);

		for(int x = -halfTilesWide; x <= halfTilesWide; ++x) {
			for(int y = -halfTilesTall; y <= halfTilesTall; ++y) {

				tmpVec.set(circle.center);

				tmpX = MathUtils.clamp((x * halfWidth) + circle.center.x, 
						circle.center.x - circle.radius, circle.center.x + circle.radius);
				tmpY = MathUtils.clamp((y * halfHeight) + circle.center.y, 
						circle.center.y - circle.radius, circle.center.y + circle.radius);
				Tile t = getTile(tmpX, tmpY);
				if (t != null) {
					if (Math.abs(tmpVec.sub(t.center).len2()) < rad2) {
						collection.addTile(t);
					} else {
						// test for corners

						// circle to the left
						if (circle.center.x + circle.radius < t.bounds.x) {
							// circle above
							if (circle.center.y + circle.radius < t.bounds.y) {
								// test left top point - center distance is less than radius
								if (Math.abs(tmpVec.set(t.bounds.x, t.bounds.y + t.bounds.height).sub(circle.center).len2()) < rad2) {
									collection.addTile(t);
								}
							} 
							// circle below
							else {
								// test left bottom point - center distance is less than radius
								if (Math.abs(tmpVec.set(t.bounds.x, t.bounds.y).sub(circle.center).len2()) < rad2) {
									collection.addTile(t);
								}
							}							
						} 
						// cirlce to the right
						else {
							// circle above
							if (circle.center.y + circle.radius < t.bounds.y) {
								// test right top point - center distance is less than radius
								if (Math.abs(tmpVec.set(t.bounds.x + t.bounds.width, t.bounds.y + t.bounds.height).sub(circle.center).len2()) < rad2) {
									collection.addTile(t);
								}
							} 
							// circle below
							else {
								// test right bottom point - center distance is less than radius
								if (Math.abs(tmpVec.set(t.bounds.x + t.bounds.width, t.bounds.y).sub(circle.center).len2()) < rad2) {
									collection.addTile(t);
								}
							}		
						}
					}
				}
			}
		}

		Pools.free(tmpVec);

		return collection;
	}

	public void addEntity(Entity e) {
		unbornEntities.add(e);
	}

	public void removeEntity(Entity e) {
		deadEntities.removeValue(e, true);
	}

	public void update(float delta) {

		timeAccumulator += delta;
		int updates = 0;
		while (timeAccumulator > TIME_STEP) {
			updates++;
			if (updates < 3) {

				// add unborn
				for (Entity e : unbornEntities) {
					entities.add(e);
				}
				unbornEntities.clear();

				// do main entity updates
				for(Entity e : entities) {
					e.update(TIME_STEP);
				}

				// move along X
				for(Entity e : entities) {
					moveX(e, false);
				}

				// move along Y
				for(Entity e : entities) {
					moveY(e, true);
				}

				// remove dead
				for (Entity e : deadEntities) {
					entities.removeValue(e, true);
				}
				deadEntities.clear();

				timeAccumulator -= TIME_STEP;
			} else {
				timeAccumulator = 0;
			}
		}

		alpha = timeAccumulator / TIME_STEP;
	}

	/**
	 * Move along X Axis
	 * @param entity - the entity in question
	 * @param registerPosition - if at the end of movement along this Axis, entity should register with overlapping tiles
	 */
	private void moveX(Entity entity, boolean registerPosition) {

		if (entity.velocity.x != 0) {

			// check tiles in next position...
			TileCollection tc = Pools.obtain(TileCollection.class);
			Rectangle nextBounds = Pools.obtain(Rectangle.class);
			nextBounds.set(entity.bounds);

			Circle nextCircle = Pools.obtain(Circle.class);
			nextCircle.set(entity.circle.center.x, entity.circle.center.y, entity.circle.radius);
			
			float adjustment = 0;

			// X Axis
			//if (Math.abs(entity.velocity.x) > 0) {
			//nextBounds.x += velocity.x;
			nextCircle.center.x += entity.velocity.x;
			getOverlappingTiles(nextCircle, tc);
			for (Tile t : tc) {
				if (t.blocked) {
					entity.velocity.x = 0;
					entity.impulse.x = 0;
				}
				else if (t.occupiers.size > 0) {
					for(Entity e : t.occupiers) {
						if (e != entity) {
							float diff = e.circle.center.x - nextCircle.center.x;
							if (Math.abs(diff) <= (e.circle.radius + entity.circle.radius) &&
									Math.signum(entity.velocity.x) == Math.signum(diff) ) {
								entity.velocity.x = 0;
								entity.impulse.x = 0;
							}
						}
					}
				}
			}
			//}
			
			// add to position
			entity.position.x += entity.velocity.x;

			// remove velocity
			entity.velocity.x = 0;

			// update boundaries
			entity.updateBoundaries();

			Pools.free(nextCircle);
			Pools.free(nextBounds);
			Pools.free(tc);
		}

		if (registerPosition) {
			// register with overlappping tiles
			entity.registerPosition();
		}
	}

	/**
	 * Move along Y Axis
	 * @param entity - the entity in question
	 * @param registerPosition - if at the end of movement along this Axis, entity should register with overlapping tiles
	 */
	private void moveY(Entity entity, boolean registerPosition) {

		if (entity.velocity.y != 0) {

			// check tiles in next position...
			TileCollection tc = Pools.obtain(TileCollection.class);
			Rectangle nextBounds = Pools.obtain(Rectangle.class);
			nextBounds.set(entity.bounds);

			Circle nextCircle = Pools.obtain(Circle.class);
			nextCircle.set(entity.circle.center.x, entity.circle.center.y, entity.circle.radius);

			// Y Axis
			//if (Math.abs(entity.velocity.y) > 0) {
			//nextBounds.y += velocity.y;
			nextCircle.center.y += entity.velocity.y;
			getOverlappingTiles(nextCircle, tc);
			for (Tile t : tc) {
				if (t.blocked) {
					entity.velocity.y = 0;
					entity.impulse.y = 0;
				}
				else if (t.occupiers.size > 0) {
					for(Entity e : t.occupiers) {
						if (e != entity) {
							float diff = e.circle.center.y - nextCircle.center.y;
							if (Math.abs(diff) <= (e.circle.radius + entity.circle.radius) &&
									Math.signum(entity.velocity.y) == Math.signum(diff) ) {
								entity.velocity.y = 0;
								entity.impulse.y = 0;
							}
						}
					}
				}
			}
			//}

			// add to position
			entity.position.y += entity.velocity.y;

			// remove velocity
			entity.velocity.y = 0;

			// update boundaries
			entity.updateBoundaries();

			Pools.free(nextCircle);
			Pools.free(nextBounds);
			Pools.free(tc);
		}

		if (registerPosition) {
			// register with overlappping tiles
			entity.registerPosition();
		}
	}

	public float getAlpha() {
		return alpha;
	}
}
