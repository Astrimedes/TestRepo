package team.hdcheese.world2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Circle implements Poolable {
	
	public float radius;
	public float radius2;
	
	public Vector2 center = new Vector2();
	
	public void set(float x, float y, float radius) {
		this.center.set(x, y);
		this.radius = radius;
		this.radius2 = radius * radius;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	
}
