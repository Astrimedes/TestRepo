package team.hdcheese;

import team.hdcheese.graphics.FancyColor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

/**
 * Plain black screen used to transition between real screens and provoke garbage collector
 * @author Mike
 *
 */
public class LoadingScreen extends GameScreen {

	Stage stage;
	Skin skin;
	Table table;

	Label titleLabel;

	FancyColor flashColor = new FancyColor(MathUtils.random(), MathUtils.random(), 
			MathUtils.random(), 0.9f);

	float highScore = 0;

	private boolean delayedAssetsLoaded = false;
	private boolean garbageCollected = false;

	public LoadingScreen() {
		super(false);
	}

	@Override
	public void render(float dt) {
		super.render(dt);
		
		flashColor.update(dt);
		titleLabel.setColor(flashColor);

		stage.act(dt);
		stage.draw();

		// load delayed assets immediately
		if (!delayedAssetsLoaded ) {
			delayedAssetsLoaded = GameSession.getGame().loadDelayedAssets();
			if (!delayedAssetsLoaded) {
				return;
			}
		}

		// garbage collect after that
		if (delayedAssetsLoaded && !garbageCollected && this.totalScreenTime > dt) {
			garbageCollected = true;
			System.gc();
		}
		
		// wait a bit for garbage collection to shake out
		if (this.totalScreenTime > 1) {
			GameSession.getGame().gotoDelayedScreen();
		}
		
	}

	private void setupTable() {

		stage = new Stage();
		skin = GameSession.getMenuTool().getSkin();
		table = new Table();
		
		flashColor.setShimmer(true, 2.0f);

		// turn on debugging
		//table.debug();

		stage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		stage.addActor(table);

		final int minWidth = 100;
		final int maxWidth = 800;
		final int prefWidth = 512;
		final int minHeight = 50;
		final int maxHeight = 200;
		final int prefHeight = 100;

		// set default cell measurements
		table.defaults().prefWidth(prefWidth).minWidth(minWidth).maxWidth(maxWidth).prefHeight(prefHeight).minHeight(minHeight).maxHeight(maxHeight);

		// title text label
		titleLabel = new Label("Loading...", skin, "large");
		
		titleLabel.setColor(flashColor);
		
		titleLabel.setAlignment(Align.center);
		table.add(titleLabel).fill();
		table.row();

		// empty row
		table.add().prefHeight(prefHeight/2).minHeight(minHeight/2).maxHeight(maxHeight/2);
		table.row();

		// fps text label
		//		fpsLabel = new Label("FPS: " + Gdx.graphics.getFramesPerSecond() , skin);
		//		fpsLabel.setFontScale(fontScale * 1.5f);
		//		fpsLabel.setColor(titleColor);
		//		fpsLabel.setAlignment(Align.center);
		//		table.add(fpsLabel).fill();
		//		table.row();

		// set to fill stage
		table.setFillParent(true);

		// set as input
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public boolean loadAssets() {
		
		GameSession.getMenuTool().initializeSkin(false);

		setupTable();

		return true;
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
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

}
