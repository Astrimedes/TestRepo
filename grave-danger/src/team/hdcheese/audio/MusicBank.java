package team.hdcheese.audio;
import java.util.HashMap;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicBank {
	
	private boolean enabled = true;	
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	private boolean musicPlaying = false;
	
	public boolean isMusicPlaying() {
		return musicPlaying;
	}
	
	public enum MusicName {
		GAMEPLAY
	}
			
	private HashMap<MusicName, Music> songs = new HashMap<MusicName, Music>();

	public MusicBank() {
		// TODO Auto-generated constructor stub
	}

	public boolean loadMusic(MusicName name, String internalFilePath) {
		
		Music m = Gdx.audio.newMusic(
				Gdx.files.getFileHandle(internalFilePath, FileType.Internal));
		songs.put(name, m);
		
		//songFilePaths.put(name, internalFilePath);
		
		return m != null;
	}
	
	public void playMusic(MusicName name) {
		
		if (!enabled) {return;}
		
		Music music = songs.get(name);
		if (music.isPlaying()) return;
		
		if (musicPlaying) {
			for(Music m : songs.values()) {
				m.stop();
			}
		}
		
		music.play();
		music.setLooping(true);

		musicPlaying = true;
	}
	
	public void stopMusic() {
		if (musicPlaying) {
			for(Music m : songs.values()) {
				m.stop();
			}
		}
	}
	
	public void pause() {
		if (musicPlaying) {
			for(Music m : songs.values()) {
				if (m.isPlaying()) {
					m.pause();
				}
			}
		}
	}
	
	public void unloadAssets() {
		for(Music m : songs.values()) {
			if (m != null) {
				m.stop();
				m.dispose();
			}
		}
		songs.clear();
		
		musicPlaying = false;
	}
	
	
}
