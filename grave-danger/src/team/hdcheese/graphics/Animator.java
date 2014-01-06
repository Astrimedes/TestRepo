package team.hdcheese.graphics;
import java.util.HashMap;

// FSM to handle animation
public class Animator {
	
	protected float stateTime = 0;
	
	protected AnimationState state = AnimationState.IDLE_DOWN;
	
	// here's where we keep our animations
	public HashMap<AnimationState, Animation> animationMap = new HashMap<AnimationState, Animation>();
	
	// get state
	public AnimationState getState() {
		return state;
	}

	// Try to change state!
	public void setState(AnimationState nextState) {
		if (nextState != state) {
			stateTime = 0;
			state = nextState;
		}
	}
	
	public void addStateTime(float dt) {
		stateTime += dt;
	}

//	public void setGraphics(CreatureType creature) {
//		// just in case...
//		animationMap.clear();
//
//		switch(creature) {
//		case CAT:
//			animationMap.put(AnimationState.IDLE, GraphicsWarehouse.animationMap.get("cat"));
//			animationMap.put(AnimationState.JUMP_UP, GraphicsWarehouse.animationMap.get("catjump"));
//			// copy for now until graphics exists
//			animationMap.put(AnimationState.JUMP_DOWN, GraphicsWarehouse.animationMap.get("catjump"));
//			animationMap.put(AnimationState.WALK, GraphicsWarehouse.animationMap.get("cat"));
//			break;
//		case RAT:
//			animationMap.put(AnimationState.IDLE, GraphicsWarehouse.animationMap.get("rat"));
//			animationMap.put(AnimationState.JUMP_UP, GraphicsWarehouse.animationMap.get("ratjumpup"));
//			animationMap.put(AnimationState.JUMP_DOWN, GraphicsWarehouse.animationMap.get("ratjumpdown"));
//			animationMap.put(AnimationState.WALK, GraphicsWarehouse.animationMap.get("ratwalk"));
//			break;
//		case FATGUY:
//			animationMap.put(AnimationState.IDLE, GraphicsWarehouse.animationMap.get("characterstill"));
//			animationMap.put(AnimationState.JUMP_UP, GraphicsWarehouse.animationMap.get("character3jump"));
//			// copy for now until graphics exists
//			animationMap.put(AnimationState.JUMP_DOWN, GraphicsWarehouse.animationMap.get("character3jump"));
//			animationMap.put(AnimationState.WALK, GraphicsWarehouse.animationMap.get("characterwalk"));
//			break;
//		default:
//			break;
//		}
//	}
//	
//	public void setGraphics(JunkType junk) {
//		switch(junk) {
//		case BOMB:
//		case ANVIL:
//		case RATS_NEST:
//		case GENERIC:
//			animationMap.put(AnimationState.IDLE, GraphicsWarehouse.animationMap.get("junk"));
//			break;
//		case TV:
//			animationMap.put(AnimationState.IDLE, GraphicsWarehouse.animationMap.get("tv"));
//			break;
//		case TROPHY:
//			animationMap.put(AnimationState.IDLE, GraphicsWarehouse.animationMap.get("trophy"));
//			break;
//		case BOOT:
//			animationMap.put(AnimationState.IDLE, GraphicsWarehouse.animationMap.get("boot"));
//			break;
//		default:
//			break;
//		}
//	}
	
	public Frame getCurrentFrame(int facing) {
		return animationMap.get(state).getKeyFrame(stateTime, facing);
	}
	
	public void reset() {
		animationMap.clear();
		state = AnimationState.IDLE_DOWN;
		stateTime = 0;
	}
	
	
}
