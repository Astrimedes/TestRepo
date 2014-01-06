package team.hdcheese.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Frame extends TextureRegion {
	
	protected TextureRegion region = null;
	
	// texel offset from lower left of graphic
	public final Vector2 offset = new Vector2();
	
	public Frame(TextureRegion region) {
		super(region);
	}
	
}
