package team.hdcheese.world2d;

import com.badlogic.gdx.utils.Pool.Poolable;

public class Coordinate implements Poolable {
	
	int x;
	int y;
	
	public Coordinate(int x,int y) {
		set(x,y);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Object o) {
		if (o.getClass() == this.getClass()) {
			Coordinate c = (Coordinate)o;
			return c.x == this.x && c.y == this.y;
		}
		return false;
		
	}
}
