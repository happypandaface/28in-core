package com.mygdx.sheep;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import com.mygdx.sheep.ui.ButtonListener;
import com.mygdx.sheep.ui.ButtonListenBridge;

public class LevelSelect implements ButtonListener
{
	private Stage stage;
	public boolean loaded = false;
	private sheep sheep;
	private AssetHolder assetHolder;
	private SheepGame sheepGame;
	private Table centerTable;
	private Table headerTable;
	private Table levels;
	private ScrollPane levelScroll;
	private int currentPanel;
	private int levelsPerPage = 14;
	protected ShapeRenderer shapeRenderer;
	
	public LevelSelect()
	{
		shapeRenderer = new ShapeRenderer();
	}
	
	public void setSheepMain(sheep s)
	{
		sheep = s;
	}
	
	public void setGame(SheepGame sg)
	{
		sheepGame = sg;
	}
	
	public void load()
	{
		stage = new Stage();
		float buttonHeight = 0.07f;
		float buttonWidth = 0.75f;
		headerTable = new Table();
		headerTable.top();
		headerTable.setFillParent(true);
		Table topMenu = new Table();
		topMenu.setFillParent(true);
		topMenu.top();
		topMenu.left();
		ImageButton back = new ImageButton(assetHolder.backButtonTex);
		back.addListener(new InputListener(){
			private LevelSelect lSelect;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				lSelect.goBack();
			}
			public InputListener setSceneChanger(LevelSelect s)
			{
				this.lSelect = s;
				return this;
			}
		}.setSceneChanger(this));
		//topMenu.add(back).width(sheepGame.getTileWidth()).height(sheepGame.getTileHeight());
		
		Table bottomMenu = new Table();
		bottomMenu.setFillParent(true);
		bottomMenu.bottom();
		TextButton backLvl = new TextButton("Previous Levels", assetHolder.newButtonStyle);
		backLvl.addListener(new InputListener(){
			private LevelSelect lSelect;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				lSelect.backLevels();
			}
			public InputListener setSceneChanger(LevelSelect s)
			{
				this.lSelect = s;
				return this;
			}
		}.setSceneChanger(this));
		TextButton more = new TextButton("More Levels", assetHolder.buttonStyle);
		more.addListener(new InputListener(){
			private LevelSelect lSelect;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				lSelect.moreLevels();
			}
			public InputListener setSceneChanger(LevelSelect s)
			{
				this.lSelect = s;
				return this;
			}
		}.setSceneChanger(this));
		bottomMenu.add(more).height(assetHolder.getPercentHeightInt(buttonHeight)).width(assetHolder.getPercentWidthInt(buttonWidth)).pad(10);
		bottomMenu.row();
		bottomMenu.add(backLvl).height(assetHolder.getPercentHeightInt(buttonHeight)).width(assetHolder.getPercentWidthInt(buttonWidth)).pad(10);
//		stage.addActor(bottomMenu);
		Table centerWrapper = new Table();
		centerWrapper.setFillParent(true);
		centerWrapper.center();
		centerTable = new Table();
		centerTable.setFillParent(true);
		centerTable.center();//center();
	//	levels = new Table();
	//	levels.setFillParent(true);
		/*levelScroll = new ScrollPane(levels, assetHolder.scrollPaneStyle);
		levelScroll.setScrollY(200.0f);
		levelScroll.setCancelTouchFocus(false);
		
		levels.setFillParent(true);
		levelScroll.setFillParent(true);
		levelScroll.setFlickScroll(true);
		levelScroll.setSize(100.0f, 100.0f);
		levelScroll.setOverscroll(false, true);
		levelScroll.setForceScroll(false, true);
		levelScroll.setScrollingDisabled(true, false);
		centerTable.center();
		centerTable.setFillParent(true);
		centerTable.setSize(100.0f, 100.0f);
		centerTable.add(levelScroll);*/
//		centerTable.setSize(100.0f, 100.0f);
//		centerWrapper.add(centerTable).width(200.0f).height(200.0f);
		stage.addActor(centerTable);
		stage.addActor(headerTable);
		stage.addActor(topMenu);
		setLevelPanel(1);
		
		loaded = true;
	}

	public void buttonPressed(int i)
	{
		doLevel(i);
	}
	
	public void setLevelPanel(int p)
	{
		//levels.clearChildren();
		Table levels = new Table();
//		levels.setFillParent(true);
//		levels.center();
		currentPanel = p;
		float buttonHeight = assetHolder.getPercentWidth(.3f); 
		float circleSize = assetHolder.getPercentWidth(.2f);
		float circlePad = assetHolder.getPercentWidth(.03f);
		int atLevel = Integer.parseInt(sheep.getSaved("level", "-1"))+1;
		for (int i = (p-1)*levelsPerPage; i < (p)*levelsPerPage && i < assetHolder.levelLoader.getMaxLevels(); ++i)
		{
			float padBottom = 0;
			float padTop = 0;
			if (i == 0)
				padTop = 0;//buttonHeight;
			else if (i == assetHolder.levelLoader.getMaxLevels()-1)
				padBottom = buttonHeight;
			String levelName = assetHolder.levelLoader.getLevelName(i);
			Button lvl1 = new Button(assetHolder.spButtonStyle);
			Table leftTable = new Table();
			leftTable.left();
			leftTable.add(new Image(i<=atLevel?assetHolder.unlockedLevel:assetHolder.lockedLevel)).size(circleSize, circleSize).padRight(circlePad);
			Table textTable = new Table();
			leftTable.add(textTable);
			textTable.add(new Label(i<=atLevel?levelName:"LOCKED", assetHolder.labelStyle)).row();
			if (i <= atLevel)
				textTable.add(new Label("Level "+(i+1), assetHolder.smallLabelStyle)).left();
			lvl1.add(leftTable).fill().expand();
			levels.add(lvl1).height(buttonHeight).width(assetHolder.getPercentWidth(1.0f)).padTop(padTop).padBottom(padBottom);
			levels.row();
			if (i <= atLevel)
				lvl1.addListener(new ButtonListenBridge().setButtonListener(this).setId(i));
		}
		ScrollPane levelScroll = new ScrollPane(levels, assetHolder.scrollPaneStyle);
		//levelScroll.setFillParent(true);
//		levelScroll.setPosition(0, 0);//assetHolder.getPercentWidth(.5f), assetHolder.getPercentWidth(.5f));
		//levelScroll.setSize(100.0f, 100.0f);
		//levelScroll.
		levelScroll.setFlickScroll(true);
		levelScroll.setCancelTouchFocus(true);
		levelScroll.setTouchable(Touchable.enabled);
		levelScroll.setScrollingDisabled(true, false);
		levelScroll.layout();
		centerTable.clearChildren();
		headerTable.clearChildren();
		//headerTable.add(new Label("", assetHolder.labelStyle)).size(assetHolder.getPercentWidth(.75f), assetHolder.getPercentHeight(.1f)).row();
		centerTable.add(new Label("CAMPAIGN", assetHolder.labelStyle)).size(assetHolder.getPercentWidth(.75f), assetHolder.getPercentHeight(.1f)).row();
		centerTable.add(levelScroll).size(assetHolder.getPercentWidth(1.0f), assetHolder.getPercentHeight(.75f));
		//centerTable.add(levelScroll);
//		levelScroll.setScrollY(200.0f);
/*
		centerTable.clearChildren();
		Table midCenterTable = new Table();
		Table rightCenterTable = new Table();
		Table leftCenterTable = new Table();
		midCenterTable.setFillParent(true);
		midCenterTable.center();
		leftCenterTable.setFillParent(true);
		leftCenterTable.center();
		rightCenterTable.setFillParent(true);
		rightCenterTable.center();
		centerTable.add(leftCenterTable).fill().expand();
		centerTable.add(midCenterTable).fill().expand();
		centerTable.add(rightCenterTable).fill().expand();
		float padding = assetHolder.getPercentWidth(.05f);
		float arrowSize = assetHolder.getPercentWidth(.1f);
		float previewSize = assetHolder.getPercentWidth(.5f);
		leftCenterTable.add(new Image(assetHolder.leftArrowActive)).size(arrowSize, arrowSize);
		midCenterTable.add(new Image(assetHolder.levelPreview)).size(previewSize, previewSize).pad(padding);
		rightCenterTable.add(new Image(assetHolder.rightArrowActive)).size(arrowSize, arrowSize);
		//centerTable.layout();
		//centerTable.validate();
//		stage.addActor(levelScroll);
		//centerTable.add(levelScroll).size(assetHolder.getPercentWidth(.9f), assetHolder.getPercentHeight(.7f));
//		centerTable.validate();
*/
	}
	
	public int maxPanels()
	{
		return (int)Math.ceil((float)assetHolder.levelLoader.getMaxLevels()/(float)levelsPerPage);
	}
	
	public void moreLevels()
	{
		if (currentPanel < maxPanels())
			setLevelPanel(currentPanel+1);
	}
	
	public void backLevels()
	{
		if (currentPanel > 1)
			setLevelPanel(currentPanel-1);
	}
	
	public void doLevel(int l)
	{
		assetHolder.levelLoader.setLoadLevelListener(new LoadLevelListener()
		{
			private LevelSelect levelSelect;
			public void levelLoaded(String levelName)
			{
				levelSelect.loaded(levelName);
			}
			public LoadLevelListener setLevelSelect(LevelSelect ls)
			{
				levelSelect = ls;
				return this;
			}
		}.setLevelSelect(this));
		assetHolder.levelLoader.loadLevel(sheepGame, l);
	}
	
	public void loaded(String levelName)
	{
		sheep.gotoMenu("game");
	}
	
	public void goBack()
	{
		sheep.gotoMenu("main");
	}
	
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
	}
	
	public void render ()
	{
	/*
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(assetHolder.getBgColor().r, assetHolder.getBgColor().g, assetHolder.getBgColor().b, 1f);
		shapeRenderer.rect(0, assetHolder.getPercentHeight(1f)-sheepGame.getTileHeight()*2, assetHolder.getPercentWidth(1f), (sheepGame.getTileHeight()*2));
		shapeRenderer.end();*/
		if (assetHolder.levelLoader.isLoading())
			assetHolder.levelLoader.render();
		else
		{
			Gdx.gl.glClearColor(assetHolder.getBgColor().r, assetHolder.getBgColor().g, assetHolder.getBgColor().b, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act(Gdx.graphics.getDeltaTime());
			stage.draw();
		}
	}
	
	public void switchTo()
	{
		setLevelPanel(1);
	//	Gdx.input.setInputProcessor(stage);
	}
	public InputProcessor getInput()
	{
		return stage;
	}
}
