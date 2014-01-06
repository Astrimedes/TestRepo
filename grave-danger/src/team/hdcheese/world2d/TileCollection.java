package team.hdcheese.world2d;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TileCollection implements Iterable<Tile>, Poolable {
	
	Array<Tile> tiles = new Array<Tile>();
	
	@Override
	public Iterator<Tile> iterator() {
		return tiles.iterator();
	}

	@Override
	public void reset() {
		tiles.clear();
	}
	
	public void addTile(Tile t) {
		if (t != null) {
			tiles.add(t);
		}
	}
	
	public void removeTile(Tile t) {
		tiles.removeValue(t, true);
	}

}
