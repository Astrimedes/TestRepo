package team.hdcheese.audio;

import java.util.HashMap;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

public class SoundBank {
	
	private boolean enabled = true;
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public enum SoundName {
		PLAYER_ATTACK,
		GUARD_ATTACK,
		PLAYER_DIG,
		GUARD_VOICE,
		GUARD_HURT,
		PLAYER_HURT,
		PLAYER_DIE,
		GUARD_DIE, PLAYER_PICKUP, PLOP
	}
	
	private HashMap<SoundName, Sound[]> sounds = new HashMap<SoundName, Sound[]>();
	
	public SoundBank() {
		// TODO Auto-generated constructor stub
	}

	public void unloadAssets() {
		for(Sound[] ss : sounds.values()) {
			if (ss != null) {
				for(Sound s : ss) {
					s.stop();
					s.dispose();
				}
			}
		}
		sounds.clear();		
	}
	
	/**
	 * Play sound with or without a reasonable amount of random volume and pitch modification
	 * @param name
	 * @param randomPitchAndVol
	 */
	public void playSound(SoundName name, boolean randomPitchAndVol) {
		if (randomPitchAndVol) {
			playSound(name, MathUtils.random(0.75f, 1.25f), MathUtils.random(0.75f, 1.25f), 0);
		} else {
			playSound(name, 1, 1, 0);
		}
	}
	
	/**
	 * randomly fetch (if more than 1) a sound with the given name
	 * @param name
	 * @return
	 */
	private Sound getSound(SoundName name) {
		Sound[] ss = sounds.get(name);
		Sound s = null;
		if (ss != null) {
			s = ss[MathUtils.random(0, ss.length-1)];
		}
		return s;
	}
	
	/**
	 * play a sound with given volume, pitch, and pan
	 * @param name
	 * @param volume 0 - 1
	 * @param pitch 0.5 - 2
	 * @param pan -1 - 1
	 */
	public void playSound(SoundName name, float volume, float pitch, float pan) {
		
		if (!enabled) {return;}
		
		getSound(name).play(volume, pitch, pan);
	}
	
	/**
	 * Pass an array of strings to set several different sound files to the same name
	 * @param name
	 * @param fileHandlePathInternal
	 */
	public void loadSound(SoundName name, String... fileHandlePathInternal) {
		int size = fileHandlePathInternal.length;
		Sound[] arrSounds = new Sound[size];
		for (int i = 0; i < size; i++) {
			arrSounds[i] = Gdx.audio.newSound(Gdx.files.getFileHandle(
					fileHandlePathInternal[i],  FileType.Internal));
		}
		
		this.sounds.put(name, arrSounds);
	}

}
