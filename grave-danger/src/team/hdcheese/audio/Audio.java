package team.hdcheese.audio;

public class Audio {
	
	public MusicBank music;
	public SoundBank sound;
	
	public void setEnabled(boolean enabled) {
		this.music.setEnabled(enabled);
		this.sound.setEnabled(enabled);
	}
	
	public boolean getEnabled() {
		return music.isEnabled() || sound.isEnabled();
	}

	public Audio(MusicBank music, SoundBank sound) {
		this.music = music;
		this.sound = sound;
	}

}
