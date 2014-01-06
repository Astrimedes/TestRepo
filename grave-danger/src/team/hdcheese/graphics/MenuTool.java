package team.hdcheese.graphics;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class MenuTool {
	
	Skin skin;
	public Skin getSkin() {
		return skin;
	}

	public MenuTool() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean initializeSkin(boolean forceCreate) {
		if (forceCreate) {
			if (skin != null) {
				skin.dispose();
			}
			skin = createSkin();
		} else {
			if (skin == null) {
				skin = createSkin();
			}
		}
		
		return skin != null;
	}

	private HashMap<String, BitmapFont> loadTrueTypeFonts() {

		HashMap<String, BitmapFont> maps = new HashMap<String, BitmapFont>();

		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ccaps.ttf"));

		// generate small menu font
		int size = (MathUtils.clamp(Gdx.graphics.getWidth() / 24, 1, 200));
		Gdx.app.log("fonts", "small font size = " + size);
		BitmapFont smallFont = gen.generateFont(size);
		smallFont.setColor(Color.WHITE);
		maps.put("small", smallFont);

		// generate medium menu font
		size = (MathUtils.clamp(Gdx.graphics.getWidth() / 16, 1, 200));
		Gdx.app.log("fonts", "medium font size = " + size);
		BitmapFont mediumFont = gen.generateFont(size);
		mediumFont.setColor(Color.WHITE);
		maps.put("medium", mediumFont);

		// generate large menu font
		size = (MathUtils.clamp(Gdx.graphics.getWidth() / 8, 1, 200));
		Gdx.app.log("fonts", "large font size = " + size);
		BitmapFont largeFont = gen.generateFont(size);
		largeFont = gen.generateFont(size);
		largeFont.setColor(Color.WHITE);
		maps.put("large", largeFont);

		gen.dispose();

		return maps;
	}

	private Skin createSkin() {

		Skin newSkin = new Skin(new TextureAtlas("skin/uiskin2.atlas"));

		// fonts are truetype fonts
		HashMap<String, BitmapFont> fontMap = loadTrueTypeFonts();
		newSkin.add("small", fontMap.get("small"));
		newSkin.add("medium", fontMap.get("medium"));
		newSkin.add("large", fontMap.get("large"));

		// colors
		newSkin.add("green", Color.GREEN.cpy());
		newSkin.add("white", Color.WHITE.cpy());
		newSkin.add("red", Color.RED.cpy());
		newSkin.add("black", Color.BLACK.cpy());

		// tintedDrawable
		Skin.TintedDrawable tinted = new Skin.TintedDrawable();
		tinted.name = "white";
		tinted.color = newSkin.getColor("white");
		tinted.color.a = 0.45f;
		newSkin.add("dialogDim", tinted);

		// button style
		// default
		Button.ButtonStyle bStyle = new Button.ButtonStyle();
		bStyle.down = newSkin.getDrawable("default-round-down");
		bStyle.up = newSkin.getDrawable("default-round");
		newSkin.add("default", bStyle);
		// toggle
		bStyle = new Button.ButtonStyle();
		bStyle.down = newSkin.getDrawable("default-round-down");
		bStyle.checked = newSkin.getDrawable("default-round-down");
		bStyle.up = newSkin.getDrawable("default-round");
		newSkin.add("toggle", bStyle);

		// text button style
		// default
		TextButton.TextButtonStyle tStyle = new TextButton.TextButtonStyle();
		tStyle.down =  newSkin.getDrawable("default-round-down");
		tStyle.up = newSkin.getDrawable("default-round");
		tStyle.font = newSkin.getFont("medium");
		tStyle.fontColor = newSkin.getColor("white");
		newSkin.add("default", tStyle);
		// toggle
		TextButton.TextButtonStyle tStyle2 = new TextButton.TextButtonStyle();
		tStyle2.down =  newSkin.getDrawable("default-round-down");
		tStyle2.up = newSkin.getDrawable("default-round");
		tStyle2.checked = newSkin.getDrawable("default-round-down");
		tStyle2.font = newSkin.getFont("medium");
		tStyle2.fontColor = newSkin.getColor("white");
		tStyle2.downFontColor = newSkin.getColor("green");
		newSkin.add("toggle", tStyle2);

		// label style
		Label.LabelStyle lStyle = new Label.LabelStyle(newSkin.getFont("medium"), newSkin.getColor("white"));
		newSkin.add("default", lStyle);
		lStyle = new Label.LabelStyle(newSkin.getFont("small"), newSkin.getColor("white"));
		newSkin.add("small", lStyle);
		lStyle = new Label.LabelStyle(newSkin.getFont("large"), newSkin.getColor("white"));
		newSkin.add("large", lStyle);

		// text field style
		TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle(newSkin.getFont("medium"), newSkin.getColor("white"),
				newSkin.getDrawable("cursor"), // cursor
				newSkin.getDrawable("selection"), // selection
				newSkin.getDrawable("textfield")); // background
		newSkin.add("default", tfStyle);
		
		// list style
		List.ListStyle listStyle = new List.ListStyle(
				newSkin.getFont("small"), newSkin.getColor("green"), 
				newSkin.getColor("white"), newSkin.getDrawable("default-rect-pad"));
		newSkin.add("default", listStyle);
		
		// scroll pane
		ScrollPane.ScrollPaneStyle spStyle = new ScrollPane.ScrollPaneStyle(
				newSkin.getDrawable("default-rect"), 
				newSkin.getDrawable("default-scroll"), newSkin.getDrawable("default-round-large"), 
				newSkin.getDrawable("default-scroll"), newSkin.getDrawable("default-round-large"));
		newSkin.add("default", spStyle);

		return newSkin;

	}

	public void disposeSkin() {
		if (skin != null) {
			skin.dispose();
		}
	}

}
