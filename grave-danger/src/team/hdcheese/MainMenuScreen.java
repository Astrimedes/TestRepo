package team.hdcheese;

import team.hdcheese.graphics.FancyColor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MainMenuScreen extends GameScreen {
	
	TextureRegionDrawable bg;
	
	Stage stage;
	Skin skin;
	Table table;

	Label titleLabel;

	float highScore = 0;

	FancyColor flashColor = new FancyColor(0.85f, 0.1f, 0.1f, 0.9f);
	
	int minWidth = Gdx.graphics.getWidth() / 6;
	int maxWidth = Gdx.graphics.getWidth() / 2;
	int prefWidth = Gdx.graphics.getWidth() / 3;
	
	int minHeight = 30;
	int maxHeight = Gdx.graphics.getHeight() / 6;
	int prefHeight = Gdx.graphics.getHeight() / 10;

	public MainMenuScreen() {
		super(true);
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
		//table.setColor(0.2f, 0.2f, 0.4f, 1);
		// set to fill stage
		table.setFillParent(true);

		// turn on debugging
		//table.debug();

		stage.addActor(table);

		// set default cell measurements
		table.defaults().prefWidth(prefWidth).minWidth(minWidth).maxWidth(maxWidth).prefHeight(prefHeight).minHeight(minHeight).maxHeight(maxHeight);

		// title text label
		titleLabel = new Label("GAME!", skin.get("large", Label.LabelStyle.class));

		titleLabel.setColor(flashColor); // uses the "FancyColor"
		flashColor.setShimmer(true, 2.0f);

		titleLabel.setAlignment(Align.center);
		table.add(titleLabel).fill();
		table.row();

		// empty row
		addEmptyRow(1);
		
		// Gameplay Screen
		TextButton playButton = new TextButton("Play", skin);
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				game.setScreen(new TiledScreen(), true);
			}
		});
		table.add(playButton);
		table.row();

		// empty row
		addEmptyRow(1);
		
		// Options Screen
		TextButton optionsButton = new TextButton("Options", skin);
		optionsButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				game.setScreen(new OptionsScreen(), false);
			}
		});
		table.add(optionsButton);
		table.row();

		// empty row
		addEmptyRow(3);
		
		// Exit game
		TextButton quitButton = new TextButton("Exit", skin);
		quitButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				game.setScreen(null, false);
				return;
			}
		});
		table.add(quitButton);
		table.row();

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
		flashColor.update(dt);
		titleLabel.setColor(flashColor);

		// don't allow input while transitioning
		if (!this.fadingOut && !this.fadingIn) {
			// exit when Back is pressed
			if (getTotalScreenTime() > 0.75f && 
					(Gdx.input.isKeyPressed(Input.Keys.BACK) | Gdx.input.isKeyPressed(Input.Keys.ESCAPE))) {
				game.setScreen(null, false);
				return;
			}
			stage.act(dt);
		}

		stage.draw();

		//Table.drawDebug(stage);
	}

}
