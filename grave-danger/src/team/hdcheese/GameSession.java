package team.hdcheese;

import team.hdcheese.audio.MusicBank;
import team.hdcheese.audio.SoundBank;
import team.hdcheese.events.EventService;
import team.hdcheese.graphics.MenuTool;
import team.hdcheese.input.Command;
import team.hdcheese.input.CommandSendEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Pools;

/**
 * Static access to things like player scoring, audio, and hardware
 * @author Mike
 *
 */
public final class GameSession {
	
	private static StringBuilder builder = new StringBuilder("");
	
	private GameSession() {	
	}
	
	/**
	 * Public access to event service
	 */
	public static EventService eventService;
	
	private static GdxGame game;
	private static MenuTool menuTool;
	
	public static void setup(GdxGame game) {
		GameSession.game = game;
		
		// init other static things
		eventService = new EventService();
		
		// create the UI Skin
		menuTool = new MenuTool();
		menuTool.initializeSkin(true);
	}
	
	public static void disposeAll() {
		menuTool.disposeSkin();
	}
	
	public static GdxGame getGame() {
		return game;
	}
	
	public static Preferences getPreferences() {
		return Gdx.app.getPreferences(GdxGameConstants.PREF_NAME);
	}
	
	public static SoundBank getSound() {
		return game.getAudio().sound;
	}
	
	public static MusicBank getMusic() {
		return game.getAudio().music;
	}

	public static MenuTool getMenuTool() {
		return menuTool;
	}
	
	public static void sendInputEvent(int index, Command command) {
		CommandSendEvent event = Pools.obtain(CommandSendEvent.class);
		event.setup(0, command);
		GameSession.eventService.notify(event);
		Pools.free(event);
	}
	

}
