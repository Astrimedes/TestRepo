package team.hdcheese.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class FancyColor extends Color {
	
	public boolean isShimmering() { return shimmer; }
	boolean shimmer = false;
	float shimmerSpeed = 1; // multiply time by this number
	
	public boolean isFading() { return fading; }
	boolean fading = false;
	boolean fadeRepeats = false;
	float fadeTime = 1; // default 1 second to fade
	float fadeTimeLeft = 0;
	
	public boolean isFlashing() { return flashing; }
	boolean flashing = false;
	float flashSpeed = 1; // multiply time by this number
	int flashSign = -1;
	
	float rOrig = r;
	float gOrig = g;
	float bOrig = b;
	float aOrig = a;
	
	private int rSign = 1;
	private int gSign = 1;
	private int bSign = 1;
	
	private int aSign = -1; // alpha mod assumed negative to start

	public FancyColor() {
		super();
		recordColors();
	}

	public FancyColor(Color color) {
		super(color);
		recordColors();
	}

	public FancyColor(float r, float g, float b, float a) {
		super(r, g, b, a);
		recordColors();
	}
	
	/**
	 * Set to given color, and disable special effects
	 */
	@Override
	public Color set(Color newColor) {
		disableEffects();
		super.set(newColor);
		recordColors();
		
		return this;
	}
	
	/**
	 * Set to given color values, and disable special effects
	 */
	@Override
	public Color set(float r, float g, float b, float a) {
		disableEffects();
		super.set(r, g, b, a);
		recordColors();
		return (Color)this;
	}
	
	/**
	 * Turn off fading, flashing, etc
	 */
	public void disableEffects() {
		resetColors();
		
		shimmer = false;
		shimmerSpeed = 1; // multiply time by this number
		
		fading = false;
		fadeRepeats = false;
		fadeTime = 1; // default 1 second to fade
		fadeTimeLeft = 0;
		
		flashing = false;
		flashSpeed = 1; // multiply time by this number
		flashSign = -1;
		
		rSign = 1;
		gSign = 1;
		bSign = 1;
		
		aSign = -1; // alpha mod assumed negative to start
		
	}
	
	private void resetColors() {
		r = rOrig;
		g = gOrig;
		b = bOrig;
		a = aOrig;
	}
	
	private void doShimmer(float dt) {
		int rand = MathUtils.random(1, 3);
		float newClr = 0;
		if (rand == 1) {
			newClr = r + (dt * rSign * shimmerSpeed);
			if (newClr > 1 | newClr < 0) {
				rSign = -rSign;
				newClr += ((dt * 2 * shimmerSpeed) * rSign);
			}
			r = newClr;
		} else if (rand == 2) {
			newClr = g + (dt * gSign * shimmerSpeed);
			if (newClr > 1 | newClr < 0) {
				gSign = -gSign;
				newClr += ((dt * 2 * shimmerSpeed) * gSign);
			}
			g = newClr;
		} else if (rand == 3) {
			newClr = b + (dt * bSign * shimmerSpeed);
			if (newClr > 1 | newClr < 0) {
				bSign = -bSign;
				newClr += ((dt * 2 * shimmerSpeed) * bSign);
			}
			b = newClr;
		}
	}

	private void doFlashing(float dt) {
		
		int changed = 0;
		
		final float max = 0.9f;

		float newClr = 0;
		newClr = r + (dt * flashSign * flashSpeed);
		if (newClr > max | newClr < rOrig) {
			newClr = MathUtils.clamp(newClr, rOrig, Math.max(rOrig, max));
			changed++;
		}
		r = newClr;

		newClr = g + (dt * flashSign * flashSpeed);
		if (newClr > max | newClr < gOrig) {
			newClr = MathUtils.clamp(newClr, gOrig, Math.max(gOrig, max));
			changed++;
		}
		g = newClr;

		newClr = b + (dt * flashSign * flashSpeed);
		if (newClr > max | newClr < bOrig) {
			newClr = MathUtils.clamp(newClr, bOrig, Math.max(bOrig, max));
			changed++;
		}
		b = newClr;
		
		// switch flash direction, all are changed over
		if (changed == 3) {
			flashSign = -flashSign;
		}
	}
	
	private void doFadingA(float dt) {
		
		if (!fadeRepeats && (fadeTimeLeft < Float.MIN_VALUE)) {
			return;
		}
		fadeTimeLeft -= dt;
		
		if (aSign < 0) {
			a = fadeTimeLeft / fadeTime;
		} else {
			a = 1.0f - (fadeTimeLeft / fadeTime);
		}
		boolean fadeDone = a < 0.0001f || a > 0.9999f;
		if (fadeDone && fadeRepeats) {
			aSign = -aSign;
			fadeTimeLeft = fadeTime;
		}
		
		if (a > 0.99f | a < 0.0001f) {
			aSign = -aSign;
			a += ((dt*2) * aSign);
		}
	}
	
	public void update(float dt) {
		
		if (shimmer) {
			doShimmer(dt);
		}
		
		if (flashing) {
			doFlashing(dt);
		}
		
		if (fading) {
			doFadingA(dt);
		}
	}
	
	/**
	 * keep track or color component's current values
	 */
	private void recordColors() {
		rOrig = r;
		gOrig = g;
		bOrig = b;
		aOrig = a;
	}
	
	/**
	 * Set Fading effect.  If repeats, it flashes between visible and invisible
	 * @param fading fading or not
	 * @param timeToFadeOut the time it takes to transition alpha from 1->0
	 * @param repeats whether the fade will reverse once complete, from 0->1 then 1->0
	 */
	public void setFading(boolean fading, float timeToFadeOut, boolean repeats) {
		resetColors();
		
		this.fading = fading;
		this.fadeTime = timeToFadeOut;
		this.fadeTimeLeft = timeToFadeOut;
		this.fadeRepeats = repeats;
	}
	
	/**
	 * Set Shimmer (randomly select a color component to cycle towards/away from 0 every update) effect
	 * @param shimmer shimmering or not
	 * @param shimmerSpeed value to multiply passed time by - should be positive
	 */
	public void setShimmer(boolean shimmer, float shimmerSpeed) {
		resetColors();
		
		this.shimmer = shimmer;
		this.shimmerSpeed = shimmerSpeed;
	}
	
	/**
	 * Set the speed multiplier for shimmer effect
	 * @param shimmerSpeed value to multiply passed time by - should be positive
	 */
	public void setShimmerSpeed(float shimmerSpeed) {
		this.shimmerSpeed = shimmerSpeed;
	}
	
	/**
	 * Set Flashing (all color components repeatedly cycle to white and back in unison)
	 * @param flashing
	 * @param flashSpeed
	 */
	public void setFlashing(boolean flashing, float flashSpeed) {
		resetColors();
				
		this.flashing = flashing;
		this.flashSpeed = flashSpeed;
	}
	
	/**
	 * Set the speed multiplier for flash effect
	 * @param flashSpeed value to multiply passed time by - should be positive
	 */
	public void setFlashSpeed(float flashSpeed) {
		this.flashSpeed = flashSpeed;
	}

}
