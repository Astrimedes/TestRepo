package team.hdcheese.world2d;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Tile implements Poolable {
	
	World world;
	Array<Entity> occupiers = new Array<Entity>();
	
	TileType type;
	
	int idxX = -1;
	int idxY = -1;
	
	public final Vector2 center = new Vector2();
	
	public boolean blocked = false;
	
	public final Rectangle bounds = new Rectangle(-1, -1, 0, 0);

	@Override
	public void reset() {
		world = null;
		occupiers.clear();
		
		blocked = false;
		
		type = null;
		
		idxX = -1;
		idxY = -1;
		
		bounds.set(-1, -1, 0, 0);
		
		center.set(-1, -1);
	}
	
	public void set(World world, int xIndex, int yIndex) {
		this.world = world;
		idxX = xIndex;
		idxY = yIndex;
		
		center.set((idxX * world.tileWidth) + (world.tileWidth/2.0f), 
				(idxY * world.tileHeight) + (world.tileHeight/2.0f));
		
		TiledMapTileLayer layer = (TiledMapTileLayer) world.tiledMap.getLayers().get(0);
		Cell cell = layer.getCell(idxX, idxY);
		MapProperties mp = cell.getTile().getProperties();
		
		// check for map key for blocked
		blocked = mp.containsKey("blocked");
		
		bounds.set(idxX * world.tileWidth, idxY * world.tileHeight, world.tileWidth, world.tileHeight);
	}
	
	public void registerEntity(Entity e) {
		occupiers.add(e);
	}
	
	public void unregisterEntity(Entity e) {
		occupiers.removeValue(e, true);
	}

}
