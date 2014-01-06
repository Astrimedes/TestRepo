package team.hdcheese.graphics;

public class Animation {
	
	boolean looping = false;
	
	static final int RIGHT = 1;
	static final int LEFT = -1;
	
	//final TextureRegion[] regions;
	protected Frame[] keyFrames;
	protected float frameDuration;
	protected float totalDuration;
	
	public Animation (float frameDuration, boolean looping, Frame... keyFrames) {
		this.looping = looping;
		this.frameDuration = frameDuration;
		//this.regions = regions.clone();
//		this.keyFrames = new Frame[regions.length];
//		for(int i = 0; i < keyFrames.length; i++) {
//			keyFrames[i] = new Frame(regions[i]);
//		}
		
		this.keyFrames = keyFrames.clone();
		
		totalDuration = (keyFrames.length) * frameDuration;
	}
	
	public void setOffsets(AnimationState targetFrame, int index, float xOffset, float yOffset) {
		
	}
	
	/**
	 * find the current frame
	 * @param deltaTime
	 * @param facing
	 * @param looping
	 * @return
	 */
	public Frame getKeyFrame (float stateTime, int facing) {
		//stateTime += deltaTime;

		if (looping) {
			while(stateTime > totalDuration) {
				stateTime -= totalDuration;
			}
		}
		
		if (stateTime < 0) {
			stateTime = 0;
		}
		
		int frameNumber = (int)Math.floor((stateTime / totalDuration) * keyFrames.length);
		
		// limit to max frame #
		frameNumber = Math.min(keyFrames.length - 1, frameNumber);
		
		Frame frame = keyFrames[frameNumber];
		
		// flip frame if necessary
//		if ((frame.isFlipX() && facing == RIGHT) || (!frame.isFlipX() && facing == LEFT)) {
//			frame.flip(true, false);
//		}
		
		return frame;
	}
}
