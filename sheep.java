package com.mygdx.sheep;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;

import com.mygdx.sheep.ui.ButtonListener;
import com.mygdx.sheep.ui.ButtonListenBridge;

public class sheep extends ApplicationAdapter implements ButtonListener
{
	SpriteBatch batch;
	Texture img;
	private Stage stage;
    TextButton button;
	private int currentMenu;
	public final static int GAME_SCREEN = 1;
	public final static int LEVEL_SELECT = 2;
	public final static int ENDLESS_GAME = 3;
	public final static int MULTI_MENU = 4;
	public final static int MAIN_MENU = 5;
	public final static int LEVEL_EDIT = 6;
	public final static int HOW_PLAY = 7;
	private PuzzleMode puzzleMode;
	private EndlessInches endlessInches;
	private LevelEditor levelEdit;
	private LevelSelect levelSelect;
	private boolean loading = false;
	private boolean loaded = false;
	private AssetHolder assetHolder;
	private ShapeRenderer shapeRenderer;
	private MultiplayerMenu multiMenu;
	private Table topTable;
	private Table table;
	private float percentTabWidth = 1.0f/3.0f;
	private TabMenu tabMenu;
	private InputMultiplexer inMux;
	private TopMenu topMenu;
	private Preferences prefs;
	
	public sheep()
	{
		loading = true;
	}
	
		@Override
	public void create ()
	{
		shapeRenderer = new ShapeRenderer();
		
		assetHolder = new AssetHolder();
		assetHolder.startLoad();
		
		
		puzzleMode = new PuzzleMode();
		puzzleMode.setAssetHolder(assetHolder);
		puzzleMode.setSheepMain(this);
		
		endlessInches = new EndlessInches();
		endlessInches.setAssetHolder(assetHolder);
		endlessInches.setSheepMain(this);
		
		levelEdit = new LevelEditor();
		levelEdit.setAssetHolder(assetHolder);
		levelEdit.setSheepMain(this);
		
		levelSelect = new LevelSelect();
		levelSelect.setAssetHolder(assetHolder);
		levelSelect.setSheepMain(this);
		levelSelect.setGame(puzzleMode);
		
		multiMenu = new MultiplayerMenu();
		multiMenu.setAssetHolder(assetHolder);
		multiMenu.setSheepMain(this);
		multiMenu.setGame(puzzleMode);
	}
	
	public void load ()
	{
		prefs = Gdx.app.getPreferences("My Preferences");
		inMux = new InputMultiplexer();
		// this has to come first
		assetHolder.finishLoad();
		
		stage = new Stage();
		inMux.addProcessor(stage);
		
		tabMenu = new TabMenu();
		tabMenu.setAssetHolder(assetHolder);
		tabMenu.setSheep(this);
		tabMenu.create();
		inMux.addProcessor(tabMenu.getInput());
		
		topTable = new Table();
		table = new Table();
		
		levelSelect.load();
		multiMenu.load();
		Gdx.input.setInputProcessor(stage);
		puzzleMode.create();
		endlessInches.create();
		levelEdit.create();
		//table.addListener(this);
		button = new TextButton("Play", assetHolder.buttonStyle);
		button.addListener(new ButtonListenBridge().setButtonListener(this).setId(LEVEL_SELECT));
		Label nameLabel = new Label("28 INCHES", assetHolder.labelStyle);
		nameLabel.setWrap(false);
		TextButton button2 = new TextButton("How", assetHolder.buttonStyle);
		button2.addListener(new ButtonListenBridge().setButtonListener(this).setId(HOW_PLAY));
		TextButton multi = new TextButton("Multiplayer", assetHolder.buttonStyle);
		multi.addListener(new ButtonListenBridge().setButtonListener(this).setId(MULTI_MENU));
		TextButton endless = new TextButton("Endless Inches", assetHolder.buttonStyle);
		endless.addListener(new ButtonListenBridge().setButtonListener(this).setId(ENDLESS_GAME));
		TextButton lvlEdit = new TextButton("Level Editor", assetHolder.buttonStyle);
		lvlEdit.addListener(new ButtonListenBridge().setButtonListener(this).setId(LEVEL_EDIT));
		
		//bottomTable.add(button).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		
		topMenu = new TopMenu();
		topMenu.setAssetHolder(assetHolder);
		topMenu.setSheep(this);
		topMenu.create();

		topTable.setFillParent(true);
		topTable.top();
		topTable.add(assetHolder.sheepImg).width(150).height(150).pad(50);
		table.setFillParent(true);
		table.add(nameLabel).pad(30);
		table.row();
		table.add(button).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
		table.add(button2).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
		table.add(multi).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
		table.add(endless).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
		table.add(lvlEdit).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		Label studioLabel = new Label("STUDIO NAME FPO", assetHolder.labelStyle);
		studioLabel.setWrap(false);
		//table.bottom();
		//table.add(studioLabel);
		//bottomTable.add(studioLabel);
		stage.addActor(table);
		stage.addActor(topTable);
		batch = new SpriteBatch();
		
		gotoMenu("main");
		
		loaded = true;
	}
	public void setSaved(String s, String put)
	{
		prefs.putString(s, put);
		prefs.flush();
	}
	public String getSaved(String s, String def)
	{
		return prefs.getString(s, def);
	}
	
	public void buttonPressed(int id)
	{
		topMenu.cornerIcon();
		switch(id)
		{
			case HOW_PLAY:
				assetHolder.levelLoader.setLoadLevelListener(new LoadLevelListener()
				{
					private sheep sheep;
					public void levelLoaded(String levelName)
					{
						sheep.gotoMenu("game");
					}
					public LoadLevelListener setLevelSelect(sheep ls)
					{
						sheep = ls;
						return this;
					}
				}.setLevelSelect(this));
				assetHolder.levelLoader.loadLevel(puzzleMode, 0);
				break;
			case LEVEL_SELECT:
				gotoMenu("level");
				break;
			case MULTI_MENU:
				gotoMenu("multi");
				break;
			case ENDLESS_GAME:
				endlessInches.reset();
				endlessInches.playEndless();
				gotoMenu("endless");
				break;
			case LEVEL_EDIT:
				levelEdit.reset();
				gotoMenu("levelEdit");
				break;
		}
	}
	
	public boolean handle(Event event)
	{
		if (event instanceof InputEvent)
		{
			InputEvent ie = ((InputEvent)event);
			if (ie.getType() == InputEvent.Type.touchUp)
				Gdx.app.log("id", ""+ie.getTarget());
		}
		return true;
	}
	

	
	public MultiplayerMenu getMultiplayerMenu()
	{
		return multiMenu;
	}
	public LevelEditor getLevelEditor()
	{
		return levelEdit;
	}
	public SheepGame getPuzzleMode()
	{
		return puzzleMode;
	}
	/*
	public void gotoMenu(int m)
	{
		currentMenu = m;
		case MAIN_MENU:
			Gdx.input.setInputProcessor(stage);
			break;
		case GAME_SCREEN:
			puzzleMode.switchTo();
			break;
		case ENDLESS_GAME:
			endlessInches.switchTo();
			break;
		case LEVEL_SELECT:
			levelSelect.switchTo();
			break;
		case MULTI_MENU:
			multiMenu.switchTo();
			break;
		case LEVEL_EDIT:
			levelEdit.switchTo();
			break;
	}*/
	public void gotoMenu(String s)
	{
		if (s.equals("main"))
		{
			currentMenu = MAIN_MENU;
			populateInMux();
			inMux.addProcessor(stage);
			tabMenu.animateUp();
		}else
		if (s.equals("game"))
		{
			currentMenu = GAME_SCREEN;
			puzzleMode.switchTo();
			populateInMux();
			inMux.addProcessor(puzzleMode.getInput());
		}else
		if (s.equals("endless"))
		{
			currentMenu = ENDLESS_GAME;

			endlessInches.switchTo();
			populateInMux();
			inMux.addProcessor(endlessInches.getInput());
		}else
		if (s.equals("level"))
		{
			currentMenu = LEVEL_SELECT;
			levelSelect.switchTo();
			populateInMux();
			inMux.addProcessor(levelSelect.getInput());
		}else
		if (s.equals("multi"))
		{
			currentMenu = MULTI_MENU;
			multiMenu.switchTo();
			populateInMux();
			inMux.addProcessor(multiMenu.getInput());
		}else
		if (s.equals("levelEdit"))
		{
			currentMenu = LEVEL_EDIT;
			levelEdit.switchTo();
			populateInMux();
			inMux.addProcessor(levelEdit.getInput());
		}
	}
	public void overlayOff()
	{
		tabMenu.animateDown();
	}
	public void overlayOn()
	{
		tabMenu.animateUp();
	}
	public TabMenu getTabMenu()
	{
		return tabMenu;
	}
	public void populateInMux()
	{
		inMux.clear();
		inMux.addProcessor(topMenu.getInput());
		inMux.addProcessor(tabMenu.getInput());
		Gdx.input.setInputProcessor(inMux);
	}

	public TopMenu getTopMenu()
	{
		return topMenu;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(assetHolder.getBgColor().r, assetHolder.getBgColor().g, assetHolder.getBgColor().b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Gdx.gl.glEnable(GL20.GL_BLEND);
		if (loading && assetHolder.update())
		{
			load();
			loading = false;
		}else if (loading)
		{
			float per = assetHolder.getProgress();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(1, 0, 0, 1);
			shapeRenderer.rect(assetHolder.getPercentWidth(.2f), assetHolder.getPercentHeight(.45f), assetHolder.getPercentWidth(.6f), assetHolder.getPercentHeight(.1f));
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(1, 0, 0, 1);
			shapeRenderer.rect(assetHolder.getPercentWidth(.2f), assetHolder.getPercentHeight(.45f), assetHolder.getPercentWidth(.6f)*per, assetHolder.getPercentHeight(.1f));
			shapeRenderer.end();
		}
		if (loaded)
		{
			switch (currentMenu)
			{
				case GAME_SCREEN:
					puzzleMode.render();
					break;
				case ENDLESS_GAME:
					endlessInches.render();
					break;
				case MULTI_MENU:
					multiMenu.render();
					break;
				case LEVEL_SELECT:
					levelSelect.render();
					break;
				case LEVEL_EDIT:
					levelEdit.render();
					break;
				case MAIN_MENU:
					stage.act(Gdx.graphics.getDeltaTime());
					stage.draw();
					break;
			}
			topMenu.render();
			tabMenu.render();
		}
	}
}
