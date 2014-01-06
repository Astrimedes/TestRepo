package team.hdcheese;

import team.hdcheese.graphics.FancyColor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class OptionsScreen extends GameScreen {
	
	Stage stage;
	Skin skin;
	Table table;
	
	TextureRegionDrawable bg;
	
	Label titleLabel;
	
//	Label soundLabel;
//	Label musicLabel;
	TextButton soundButton;
	TextButton musicButton;

	FancyColor flashColor = new FancyColor(0.85f, 0.1f, 0.1f, 0.9f);
	
	int minWidth = Gdx.graphics.getWidth() / 6;
	int maxWidth = Gdx.graphics.getWidth() / 2;
	int prefWidth = (int)(Gdx.graphics.getWidth() / 2.7);
	
	int minHeight = 30;
	int maxHeight = Gdx.graphics.getHeight() / 6;
	int prefHeight = Gdx.graphics.getHeight() / 10;

	public OptionsScreen() {
		super(true);
	}
	
	private String getSoundLabelText() {
		return "Sound: " + (game.getSound().isEnabled() ? "On" : "Off");
	}
	
	private String getMusicLabelText() {
		return "Music: " + (game.getMusic().isEnabled() ? "On" : "Off");
	}
	
	// adds thin empty rows
	private void addEmptyRow(int count) {
		for(int i = 0; i < count; i++) {
			table.add().prefHeight(prefHeight/4).minHeight(minHeight/4).maxHeight(maxHeight/4);
			table.row();
		}
	}

	private void setupTable() {
		
		stage = new Stage();
		stage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		skin = GameSession.getMenuTool().getSkin();
		table = new Table();
		
		//table.setBackground(bg);
		//table.setColor(1, 0.9f, 0.7f, 1);
		table.setFillParent(true);

		// turn on debugging
		//table.debug();

		stage.addActor(table);

		// set default cell measurements
		table.defaults().prefWidth(prefWidth).minWidth(minWidth).maxWidth(maxWidth).prefHeight(prefHeight).minHeight(minHeight).maxHeight(maxHeight);

		// title text label
		titleLabel = new Label("OPTIONS", skin, "large");
		// set shimmer
		titleLabel.setColor(flashColor); // uses the "FancyColor"
		flashColor.setShimmer(true, 2.0f);
		// add to table
		titleLabel.setAlignment(Align.center);
		table.add(titleLabel).fill();
		table.row();

		// empty row
		addEmptyRow(2);
		
		// sound toggle button
		soundButton = new TextButton(getSoundLabelText(), skin);
		soundButton.getLabel().setAlignment(Align.left, Align.left);
		soundButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				
				// toggle sound enabled
				game.getSound().setEnabled(!game.getSound().isEnabled());
				
				if (game.getSound().isEnabled()) {
					// play sound!
					//game.getSound().playSound(SoundName.SELECT, false);
				}
				
				// set new text value
				soundButton.setText(getSoundLabelText());
				
				return;
			}
		});
		table.add(soundButton);
		table.row();

		// empty row
		addEmptyRow(1);
		
		// music toggle button
		musicButton = new TextButton(getMusicLabelText(), skin);
		musicButton.getLabel().setAlignment(Align.left, Align.left);
		musicButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				
				// toggle music enabled
				game.getMusic().setEnabled(!game.getMusic().isEnabled());
				
				if (game.getMusic().isEnabled()) {
					// play music!
					//game.getMusic().playMusic(MusicName.GAMEPLAY);
				} else {
					//game.getMusic().stopMusic();
				}
				
				// set new text value
				musicButton.setText(getMusicLabelText());
				
				return;
			}
		});
		table.add(musicButton);
		table.row();

		// empty row
		addEmptyRow(3);

		// Exit button
		TextButton exitButton = new TextButton("Done", skin);
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				
				// stop music
				game.getMusic().stopMusic();
				
				game.setScreen(new MainMenuScreen(), false);
				
				return;
			}
		});
		table.add(exitButton);
		table.row();

		// List Screen button
//		TextButton listButton = new TextButton("List Screen", skin);
//		listButton.getLabel().setStyle(skin.get("small", LabelStyle.class));
//		listButton.addListener(new ClickListener() {
//			@Override
//			public void clicked (InputEvent event, float x, float y) {
//
//				// SWITCH TO LIST SCREEN
//				game.setScreen(ScreenType.MISC, false);
//
//				return;
//			}
//		});
//		table.add(listButton);
//		table.row();

		

		// set as input
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public boolean loadAssets() {
		
		GameSession.getMenuTool().initializeSkin(false);
		
		bg = new TextureRegionDrawable(
				new TextureRegion(new Texture(Gdx.files.internal("skin/square.png"))));

		// main menu setup
		setupTable();

		return loadAudio();
	}

	private boolean loadAudio() {
		
		//game.getSound().loadSound(SoundName.SELECT, "audio/pickup_1.wav");
		//game.getSound().loadSound(SoundName.CANCEL, "audio/powerup_1.wav");
		
		//game.getMusic().loadMusic(MusicName.GAMEPLAY, "audio/sashimi.mp3");
		
		return true;
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		stage.dispose();
		
	}

	@Override
	protected void unloadAssets() {
		game.getSound().unloadAssets();
		game.getMusic().unloadAssets();
	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(float dt){
		super.render(dt);

		// shimmer title color
		//shimmerColor(dt);
		flashColor.update(dt);
		titleLabel.setColor(flashColor);

		// don't allow input while transitioning
		if (!this.fadingOut && !this.fadingIn) {
			// exit when Back is pressed
			if (getTotalScreenTime() > 0.75f && 
					(Gdx.input.isKeyPressed(Input.Keys.BACK) | Gdx.input.isKeyPressed(Input.Keys.ESCAPE))) {
				game.setScreen(new MainMenuScreen(), false);
				return;
			}
			stage.act(dt);
		}

		stage.draw();

		//Table.drawDebug(stage);

	}
}
