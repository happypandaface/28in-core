package com.mygdx.sheep;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;

public class AssetHolder
{
	private AssetManager assets;
	public Texture sheepTex;
	public Image sheepImg;
	public BitmapFont fontWhite;
	public BitmapFont smallFontWhite;
	public BitmapFont fontRed;
	public Skin skin;
	public LabelStyle labelStyle;
	public LabelStyle smallLabelStyle;
	public TextButtonStyle buttonStyle;
	public TextButtonStyle smallButtonStyle;
	private String fontFile = "theGoodFont/goodFont.fnt";
	public TextureRegionDrawable backButtonTex;
	private String backButton = "140621-Menu-Button.png";
	public Texture sheepTex1;
	public Texture sheepTex2;
	public Texture sheepTex3;
	public Texture guardTex1;
	public Texture guardTex2;
	public Texture guardTex3;
	public Texture sheepVert1;
	public Texture sheepVert2;
	public Texture sheepVert3;
	public Texture startOverlay;
	public Texture endOverlay;
	public Texture redTex;
	public Texture greenTex;
	public Texture dog1;
	public Texture regTile1;
	public Texture boulder1;
	public Texture tallGrass1;
	public Texture white;
	public TextureRegionDrawable tabButtonUp;
	public TextureRegionDrawable tabButtonDown;
	private float fontSize = 1.0f/1500.0f;
	private float smallFontSize = 1.0f/2500.0f;
	public LevelLoader levelLoader;
	public float buttonHeight = 0.07f;
	public float buttonWidth = 0.75f;
	public TextFieldStyle textFieldStyle;
	public ImageButtonStyle playTabButtonStyle;
	public ScrollPaneStyle scrollPaneStyle;
	public TextButtonStyle newButtonStyle;
	public TextButtonStyle spButtonStyle;
	public Texture levelPreview;
	public Texture unlockedLevel;
	public Texture lockedLevel;
	public Texture rightArrowActive;
	public Texture rightArrowFaded;
	public Texture leftArrowFaded;
	public Texture leftArrowActive;
	public Color backgroundColor;
//	public Texture profileIcon;
	public TextureRegionDrawable profileIcon;
	public TextureRegionDrawable closeIcon;
	public String lightColorHex = "47edab";
	public String darkColorHex = "39b27b";
	public String mliClassName = "levelInfo";
	public Texture newSheepTex1;
	public Texture newSheepTex2;
	public Texture newSheepTex3;
	public Texture newSheepUpTex1;
	public Texture newSheepUpTex2;
	public Texture newSheepUpTex3;
	public Texture newSheepDownTex1;
	public Texture newSheepDownTex2;
	public Texture newSheepDownTex3;
	public Texture newGuardTex1;
	public Texture newGuardTex2;
	public Texture newGuardTex3;
	public Texture newGuardUpTex1;
	public Texture newGuardUpTex2;
	public Texture newGuardUpTex3;
	public Texture newGuardDownTex1;
	public Texture newGuardDownTex2;
	public Texture newGuardDownTex3;
	public Texture newDogTex1;
	public Texture newDogTex2;
	public Texture newDogTex3;
	public Texture newDogUpTex1;
	public Texture newDogUpTex2;
	public Texture newDogUpTex3;
	public Texture newDogDownTex1;
	public Texture newDogDownTex2;
	public Texture newDogDownTex3;
	public TextureRegionDrawable popTogNew1;
	public TextureRegionDrawable popTogNew2;

	public void startLoad()
	{
		backgroundColor = new Color(71f/256f, 237f/256f, 171f/256f, 1);
		assets = new AssetManager();
		assets.load(fontFile, BitmapFont.class);
		assets.load(backButton, Texture.class);
		assets.load("popTogNew1.png", Texture.class);
		assets.load("popTogNew2.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Frame0.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Frame1.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Frame2.png", Texture.class);
		assets.load("unlockedLevel.png", Texture.class);
		assets.load("lockedLevel.png", Texture.class);
		
		assets.load("140621-28Inches-Sprite-Guard-Frame-0.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Guard-Frame-1.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Guard-Frame-2.png", Texture.class);
		
		assets.load("140621-28Inches-Sprite-Sheep-Vertical-Frame0.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Vertical-Frame1.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Vertical-Frame2.png", Texture.class);
		
		assets.load("140616_Sheep RD1-BIG-sheep.png", Texture.class);
		assets.load("140616_Tile RD1-BIG.png", Texture.class);
		assets.load("140621-28Inches-Tile-Boulder.png", Texture.class);
		assets.load("140623-28Inches-Tile-Grass.png", Texture.class);
		
		assets.load("140621-28Inches-Button-Normal.png", Texture.class);
		assets.load("140621-28Inches-Button-Pressed.png", Texture.class);
		
		assets.load("140621-Starting-Area.png", Texture.class);
		assets.load("140621-Ending-Area-Overlay.png", Texture.class);
		assets.load("140628-28Inches-Inactive-Tab.png", Texture.class);
		assets.load("140628-28Inches-Active-Tab.png", Texture.class);
		assets.load("newButtonTexUp.png", Texture.class);
		assets.load("levelPreview.png", Texture.class);
		assets.load("rightArrowActive.png", Texture.class);
		assets.load("rightArrowFaded.png", Texture.class);
		assets.load("leftArrowFaded.png", Texture.class);
		assets.load("leftArrowActive.png", Texture.class);
		assets.load("dog.png", Texture.class);
		assets.load("profileIcon.png", Texture.class);
		assets.load("closeIcon.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-RedSheep-Horizontal-1.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-RedSheep-Horizontal-2.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-RedSheep-Horizontal-3.png", Texture.class);
		assets.load("02 Up/140703-28Inches-RedSheep-Vertical-1.png", Texture.class);
		assets.load("02 Up/140703-28Inches-RedSheep-Vertical-2.png", Texture.class);
		assets.load("02 Up/140703-28Inches-RedSheep-Vertical-3.png", Texture.class);
		assets.load("03 Down/140703-28Inches-RedSheep-Vertical-Down-1.png", Texture.class);
		assets.load("03 Down/140703-28Inches-RedSheep-Vertical-Down-2.png", Texture.class);
		assets.load("03 Down/140703-28Inches-RedSheep-Vertical-Down-3.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-Guard-Horizontal-1.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-Guard-Horizontal-2.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-Guard-Horizontal-3.png", Texture.class);
		assets.load("02 Up/140703-28Inches-Guard-Up-1.png", Texture.class);
		assets.load("02 Up/140703-28Inches-Guard-Up-2.png", Texture.class);
		assets.load("02 Up/140703-28Inches-Guard-Up-3.png", Texture.class);
		assets.load("03 Down/140703-28Inches-Guard-Down-1.png", Texture.class);
		assets.load("03 Down/140703-28Inches-Guard-Down-2.png", Texture.class);
		assets.load("03 Down/140703-28Inches-Guard-Down-3.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-Guard-Horizontal-1.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-Guard-Horizontal-2.png", Texture.class);
		assets.load("01 Horizontal/140703-28Inches-Guard-Horizontal-3.png", Texture.class);
		assets.load("singlePlayerLevelButton.png", Texture.class);
		assets.load("singlePlayerLevelButtonDown.png", Texture.class);
		assets.load("03 Dog/Up/140703-28Inches-Dog-Up-1.png", Texture.class);
		assets.load("03 Dog/Up/140703-28Inches-Dog-Up-2.png", Texture.class);
		assets.load("03 Dog/Up/140703-28Inches-Dog-Up-3.png", Texture.class);
		assets.load("03 Dog/Down/140703-28Inches-Dog-Down-1.png", Texture.class);
		assets.load("03 Dog/Down/140703-28Inches-Dog-Down-2.png", Texture.class);
		assets.load("03 Dog/Down/140703-28Inches-Dog-Down-3.png", Texture.class);
		assets.load("03 Dog/Horizontal/140703-28Inches-Dog-Horizontal-1.png", Texture.class);
		assets.load("03 Dog/Horizontal/140703-28Inches-Dog-Horizontal-2.png", Texture.class);
		assets.load("03 Dog/Horizontal/140703-28Inches-Dog-Horizontal-3.png", Texture.class);
	}
	public void finishLoad()
	{
		popTogNew1 = new TextureRegionDrawable(new TextureRegion(assets.get("popTogNew1.png", Texture.class)));
		popTogNew2 = new TextureRegionDrawable(new TextureRegion(assets.get("popTogNew2.png", Texture.class)));
		newSheepTex1 = assets.get("01 Horizontal/140703-28Inches-RedSheep-Horizontal-1.png", Texture.class);
		newSheepTex2 = assets.get("01 Horizontal/140703-28Inches-RedSheep-Horizontal-2.png", Texture.class);
		newSheepTex3 = assets.get("01 Horizontal/140703-28Inches-RedSheep-Horizontal-3.png", Texture.class);
		newSheepUpTex1 = assets.get("02 Up/140703-28Inches-RedSheep-Vertical-1.png", Texture.class);
		newSheepUpTex2 = assets.get("02 Up/140703-28Inches-RedSheep-Vertical-2.png", Texture.class);
		newSheepUpTex3 = assets.get("02 Up/140703-28Inches-RedSheep-Vertical-3.png", Texture.class);
		newSheepDownTex1 = assets.get("03 Down/140703-28Inches-RedSheep-Vertical-Down-1.png", Texture.class);
		newSheepDownTex2 = assets.get("03 Down/140703-28Inches-RedSheep-Vertical-Down-2.png", Texture.class);
		newSheepDownTex3 = assets.get("03 Down/140703-28Inches-RedSheep-Vertical-Down-3.png", Texture.class);
		newGuardTex1 = assets.get("01 Horizontal/140703-28Inches-Guard-Horizontal-1.png", Texture.class);
		newGuardTex2 = assets.get("01 Horizontal/140703-28Inches-Guard-Horizontal-2.png", Texture.class);
		newGuardTex3 = assets.get("01 Horizontal/140703-28Inches-Guard-Horizontal-3.png", Texture.class);
		newGuardUpTex1 = assets.get("02 Up/140703-28Inches-Guard-Up-1.png", Texture.class);
		newGuardUpTex2 = assets.get("02 Up/140703-28Inches-Guard-Up-2.png", Texture.class);
		newGuardUpTex3 = assets.get("02 Up/140703-28Inches-Guard-Up-3.png", Texture.class);
		newGuardDownTex1 = assets.get("03 Down/140703-28Inches-Guard-Down-1.png", Texture.class);
		newGuardDownTex2 = assets.get("03 Down/140703-28Inches-Guard-Down-2.png", Texture.class);
		newGuardDownTex3 = assets.get("03 Down/140703-28Inches-Guard-Down-3.png", Texture.class);
		newDogUpTex1 = assets.get("03 Dog/Up/140703-28Inches-Dog-Up-1.png", Texture.class);
		newDogUpTex2 = assets.get("03 Dog/Up/140703-28Inches-Dog-Up-2.png", Texture.class);
		newDogUpTex3 = assets.get("03 Dog/Up/140703-28Inches-Dog-Up-3.png", Texture.class);
		newDogDownTex1 = assets.get("03 Dog/Down/140703-28Inches-Dog-Down-1.png", Texture.class);
		newDogDownTex2 = assets.get("03 Dog/Down/140703-28Inches-Dog-Down-2.png", Texture.class);
		newDogDownTex3 = assets.get("03 Dog/Down/140703-28Inches-Dog-Down-3.png", Texture.class);
		newDogTex1 = assets.get("03 Dog/Horizontal/140703-28Inches-Dog-Horizontal-1.png", Texture.class);
		newDogTex2 = assets.get("03 Dog/Horizontal/140703-28Inches-Dog-Horizontal-2.png", Texture.class);
		newDogTex3 = assets.get("03 Dog/Horizontal/140703-28Inches-Dog-Horizontal-3.png", Texture.class);
		float fontScale = getPercentWidth(fontSize);
		float smallFontScale = getPercentWidth(smallFontSize);
		profileIcon = new TextureRegionDrawable(new TextureRegion(assets.get("profileIcon.png", Texture.class)));
		closeIcon = new TextureRegionDrawable(new TextureRegion(assets.get("closeIcon.png", Texture.class)));
		fontWhite = new BitmapFont(Gdx.files.internal(fontFile));
		fontWhite.setColor(1, 1, 1, 1);
		fontWhite.setScale(fontScale);//fontScale);
		smallFontWhite = assets.get(fontFile, BitmapFont.class);
		smallFontWhite.setScale(smallFontScale);
		levelLoader = new LevelLoader();
		skin = new Skin();
		regTile1 = assets.get("140616_Tile RD1-BIG.png", Texture.class);
		boulder1 = assets.get("140621-28Inches-Tile-Boulder.png", Texture.class);
		tallGrass1 = assets.get("140623-28Inches-Tile-Grass.png", Texture.class);
		sheepTex1 = assets.get("140621-28Inches-Sprite-Sheep-Frame0.png", Texture.class);
		sheepTex2 = assets.get("140621-28Inches-Sprite-Sheep-Frame1.png", Texture.class);
		sheepTex3 = assets.get("140621-28Inches-Sprite-Sheep-Frame2.png", Texture.class);
		guardTex1 = assets.get("140621-28Inches-Sprite-Guard-Frame-0.png", Texture.class);
		guardTex2 = assets.get("140621-28Inches-Sprite-Guard-Frame-1.png", Texture.class);
		guardTex3 = assets.get("140621-28Inches-Sprite-Guard-Frame-2.png", Texture.class);
		sheepVert1 = assets.get("140621-28Inches-Sprite-Sheep-Vertical-Frame0.png", Texture.class);
		sheepVert2 = assets.get("140621-28Inches-Sprite-Sheep-Vertical-Frame1.png", Texture.class);
		sheepVert3 = assets.get("140621-28Inches-Sprite-Sheep-Vertical-Frame2.png", Texture.class);
		startOverlay = assets.get("140621-Starting-Area.png", Texture.class);
		levelPreview = assets.get("levelPreview.png", Texture.class);
		unlockedLevel = assets.get("unlockedLevel.png", Texture.class);
		lockedLevel = assets.get("lockedLevel.png", Texture.class);
		rightArrowActive = assets.get("rightArrowActive.png", Texture.class);
		rightArrowFaded = assets.get("rightArrowFaded.png", Texture.class);
		leftArrowFaded = assets.get("leftArrowFaded.png", Texture.class);
		leftArrowActive = assets.get("leftArrowActive.png", Texture.class);
		newButtonStyle = new TextButtonStyle();
		newButtonStyle.up = new NinePatchDrawable(new NinePatch(assets.get("newButtonTexUp.png", Texture.class), 32, 32, 32, 32));
		newButtonStyle.down = new NinePatchDrawable(new NinePatch(assets.get("newButtonTexUp.png", Texture.class), 32, 32, 32, 32));
		spButtonStyle = new TextButtonStyle();
		spButtonStyle.up = new NinePatchDrawable(new NinePatch(assets.get("singlePlayerLevelButton.png", Texture.class), 32, 32, 109, 109));
		spButtonStyle.down = new NinePatchDrawable(new NinePatch(assets.get("singlePlayerLevelButtonDown.png", Texture.class), 32, 32, 109, 109));
		newButtonStyle.font = fontWhite;
		playTabButtonStyle = new ImageButtonStyle();
		playTabButtonStyle.imageUp = new TextureRegionDrawable(new TextureRegion(assets.get("140628-28Inches-Inactive-Tab.png", Texture.class)));
		playTabButtonStyle.imageDown = new TextureRegionDrawable(new TextureRegion(assets.get("140628-28Inches-Active-Tab.png", Texture.class)));
		startOverlay.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		dog1 = assets.get("dog.png", Texture.class);
		endOverlay = assets.get("140621-Ending-Area-Overlay.png", Texture.class);
		endOverlay.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		{
			backButtonTex = new TextureRegionDrawable(new TextureRegion(assets.get(backButton, Texture.class)));
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.RED);
			pixmap.fill();
			redTex = new Texture(pixmap);
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.GREEN);
			pixmap.fill();
			greenTex = new Texture(pixmap);
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();
			white = new Texture(pixmap);
			skin.add("white", new Texture(pixmap));
		}
		scrollPaneStyle = new ScrollPaneStyle();
		scrollPaneStyle.vScrollKnob = skin.newDrawable("white", Color.valueOf("ffffff"));
//		scrollPaneStyle.vScroll = skin.newDrawable("white", Color.valueOf("0000ff"));
//		scrollPaneStyle.background = skin.newDrawable("white", Color.valueOf("00ff00"));
		textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = assets.get(fontFile, BitmapFont.class);
		textFieldStyle.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
		textFieldStyle.cursor = skin.newDrawable("white", Color.valueOf("ffffff"));
		//textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(assets.get("140621-28Inches-Button-Normal.png", Texture.class)));
		textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(assets.get("140621-28Inches-Button-Pressed.png", Texture.class)));
		sheepTex = new Texture(Gdx.files.internal("140616_Sheep RD1-BIG-sheep.png"));
		sheepImg = new Image(sheepTex);
		//smallFontWhite.setScale(.1f);//fontScale);
		fontRed = new BitmapFont(Gdx.files.internal(fontFile));
		fontRed.setColor(1, 1, 1, 1);
		fontRed.setScale(fontScale);
		labelStyle = new LabelStyle();
		smallLabelStyle = new LabelStyle();
		smallLabelStyle.font = smallFontWhite;
		labelStyle.font = fontWhite;
		buttonStyle = new TextButtonStyle();
		smallButtonStyle = new TextButtonStyle();
		//NinePatch upPatch = new NinePatch(assets.get("140621-28Inches-Button-Normal.png", Texture.class), 128, 128, 128, 128);
		//NinePatch downPatch = new NinePatch(assets.get("140621-28Inches-Button-Pressed.png", Texture.class), 128, 128, 128, 128);
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assets.get("140621-28Inches-Button-Normal.png", Texture.class)));
		smallButtonStyle.up = new TextureRegionDrawable(new TextureRegion(assets.get("140621-28Inches-Button-Normal.png", Texture.class)));
		//new NinePatchDrawable(upPatch);//skin.newDrawable("white", Color.valueOf("ffffff"));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assets.get("140621-28Inches-Button-Pressed.png", Texture.class)));
		smallButtonStyle.down = new TextureRegionDrawable(new TextureRegion(assets.get("140621-28Inches-Button-Pressed.png", Texture.class)));
		//new NinePatchDrawable(downPatch);//skin.newDrawable("white", Color.valueOf("ff0000"));
		buttonStyle.font = fontRed;
		smallButtonStyle.font = smallFontWhite;
		smallButtonStyle.fontColor = Color.valueOf(lightColorHex);
		buttonStyle.fontColor = Color.valueOf("ffffff");
		buttonStyle.downFontColor = Color.valueOf("ffffff");
		buttonStyle.overFontColor = Color.valueOf("ff0000");
		buttonStyle.checkedFontColor = Color.valueOf("ff0000");
	}

	public Color getBgColor()
	{
		return backgroundColor;
	}
	
	public float getButtonHeight()
	{
		return getPercentHeight(buttonHeight);
	}
	public float getButtonWidth()
	{
		return getPercentWidth(buttonWidth);
	}
	public float getSmallButtonHeight()
	{
		return getPercentHeight(.05f);
	}
	public float getSmallButtonWidth()
	{
		return getPercentWidth(.3f);
	}
	public float getPercentWidth(float per)
	{
		return ((float)Gdx.graphics.getWidth())*per;
	}
	
	public float getPercentHeight(float per)
	{
		return ((float)Gdx.graphics.getHeight())*per;
	}
	
	public int getPercentWidthInt(float per)
	{
		return (int)(((float)Gdx.graphics.getWidth())*per);
	}
	
	public int getPercentHeightInt(float per)
	{
		return (int)(((float)Gdx.graphics.getHeight())*per);
	}
	
	public float getProgress()
	{
		return assets.getProgress();
	}
	
	public boolean update()
	{
		return assets.update();
	}
}
