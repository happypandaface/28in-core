package com.mygdx.sheep;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.mygdx.sheep.ui.ButtonListener;
import com.mygdx.sheep.ui.ButtonListenBridge;

public class GameOverlay implements ButtonListener
{
	protected boolean usingOverlay;
	public InputMultiplexer inMux;
	protected Stage stage;
	protected Stage overlayStage;
	protected Table bottomMenu;
	protected Table overlayMenu;
	protected SheepGame sheepGame;
	protected AssetHolder assetHolder;
	protected sheep sheep;
	protected boolean inOverlay;
	protected boolean paused;
	protected SpriteBatch batch;
	protected Table topMenu;
	protected Table topRightMenu;
	protected String levelName;
	protected String creatorName;
	protected Array<OverlayExtension> extensions;
	private ShapeRenderer shapeRenderer;
	
	protected float resultTime = -1;
	protected float resultFadeDelay = 0.3f;
	protected float fadeSpeed = 1f;
	protected float resultBannerAlpha = 1f;// this is how see through the overlays are (for messages in-game)
	public boolean displayFadedCut = true;
	public boolean addingReload= true;
	
	protected boolean shownBottomMenu;
	protected static final int RELOAD = 1;
	protected static final int HIGHEST_ENUM = 1;
	
	public GameOverlay()
	{
		extensions = new Array<OverlayExtension>();
		batch = new SpriteBatch();
		stage = new Stage();
		inMux = new InputMultiplexer();
		inMux.addProcessor(stage);
		overlayStage = new Stage();
		shapeRenderer = new ShapeRenderer();
		topMenu = new Table();
		topRightMenu= new Table();
		topMenu.setFillParent(true);
		topMenu.top();
		topMenu.left();
		topRightMenu.setFillParent(true);
		topRightMenu.top();
		topRightMenu.right();
		stage.addActor(topMenu);
		stage.addActor(topRightMenu);
		
		bottomMenu = new Table();
		bottomMenu.setFillParent(true);
		bottomMenu.bottom();
		stage.addActor(bottomMenu);
		
		overlayMenu = new Table();
		overlayMenu.setFillParent(true);
		overlayMenu.center();
		overlayStage.addActor(overlayMenu);
		
	}
	
	public void addExtension(OverlayExtension oe)
	{
		extensions.add(oe);
		oe.setAssetHolder(assetHolder);
		oe.setSheepMain(sheep);
		oe.setSheepGame(sheepGame);
		for (int i = 0; i < extensions.size; ++i)
			extensions.get(i).addToInMux(inMux);
	}
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
	}
	public void setSheepMain(sheep s)
	{
		sheep = s;
	}
	public void setSheepGame(SheepGame sg)
	{
		sheepGame = sg;
	}
	
	public void buttonPressed(int id)
	{
		switch (id)
		{
			case RELOAD:
				retryLevel();
				break;
		}
	}
	public void create()
	{
		ImageButton back = new ImageButton(assetHolder.getDrawable(assetHolder.newHamUp), assetHolder.getDrawable(assetHolder.newHamDown));
		back.addListener(new InputListener(){
			private GameOverlay gameOverlay;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				gameOverlay.doMenu();
			}
			public InputListener setSceneChanger(GameOverlay s)
			{
				this.gameOverlay = s;
				return this;
			}
		}.setSceneChanger(this));
		topMenu.add(back).width(sheepGame.getTileWidth()).height(sheepGame.getTileHeight());
		addReloadButton();
		for (int i = 0; i < extensions.size; ++i)
			extensions.get(i).create();
	}

	public void addReloadButton()
	{
		ImageButton reload = new ImageButton(assetHolder.getDrawable(assetHolder.reloadUp), assetHolder.getDrawable(assetHolder.reloadDown));
		reload.addListener(new ButtonListenBridge().setButtonListener(this).setId(RELOAD));
		topRightMenu.add(reload).width(sheepGame.getTileWidth()).height(sheepGame.getTileHeight()).left();
	}
	
	public void reset()
	{
		levelName = "";
		creatorName = "";
		bottomMenu.clearChildren();
		unpauseOverlay();
		inOverlay = false;
		resultTime = -1;
		shownBottomMenu = false;
	}
	
	public void newMessage()
	{
		resultTime = 0;
	}
	
	public void render(float delta)
	{
		// do messages
		if (sheepGame.hasMessage())
		{
			batch.begin();
			if (resultTime < 0)
				resultTime = 0;
			resultTime += delta*fadeSpeed;
			drawSheepMessage(sheepGame.getMessage());
			batch.end();
		}
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(assetHolder.getBgColor().r, assetHolder.getBgColor().g, assetHolder.getBgColor().b, 1f);
		shapeRenderer.rect(0, assetHolder.getPercentHeight(1f)-sheepGame.getTileHeight(), assetHolder.getPercentWidth(1f), (sheepGame.getTileHeight()));
		shapeRenderer.end();
		stage.act(delta);
		stage.draw();
		for (int i = 0; i < extensions.size; ++i)
			extensions.get(i).render(delta);
		batch.begin();
		float tileW = sheepGame.getTileWidth();
		float tileH = sheepGame.getTileHeight();
		if (sheepGame.cutsAvailable() > 0)
			batch.draw(assetHolder.cutIcon, tileW*1.2f, assetHolder.getPercentHeight(1f)-sheepGame.getTileHeight()*.8f, sheepGame.getTileWidth()*.6f, sheepGame.getTileHeight()*.6f);
		else if (displayFadedCut)
			batch.draw(assetHolder.cutFaded, tileW*1.2f, assetHolder.getPercentHeight(1f)-sheepGame.getTileHeight()*.8f, sheepGame.getTileWidth()*.6f, sheepGame.getTileHeight()*.6f);
		if (inOverlay)
		{
			batch.setColor(0f, 0f, 0f, 0.75f);
			batch.draw(assetHolder.white, 0, 0, assetHolder.getPercentWidth(1), assetHolder.getPercentHeight(1));
		}
		batch.end();
		overlayStage.act(delta);
		overlayStage.draw();
	}
	
	public void trySkip()
	{
		if (resultTime < resultFadeDelay)
		{
			resultTime = resultFadeDelay;
		}else
		{/*// now this is done with buttons:
			if(sheepGame.isLosing())
			{
				sheepGame.retryLevel();
			}else
			if(sheepGame.isWinning())
			{
				sheepGame.nextLevel();
			}*/
		}
	}
	
	public void drawSheepMessage(SheepMessage msg)
	{
		msg.msg = msg.msg.replace("\n", " ");
		msg.pos = .3f;
		if (resultTime > resultFadeDelay)
		{
			resultTime = resultFadeDelay;
			if (!shownBottomMenu)
			{
				shownBottomMenu = true;
				showSheepMessageButtons();
			}
		}
		// get the alpha based on the time the message has been on the screen
		float alpha = (resultTime/resultFadeDelay)*resultBannerAlpha;
		// set the alpha of the batch
		
		float dialogueBubbleWidth = assetHolder.getPercentWidth(.85f);
		float dialogueBubbleWidth2 = assetHolder.getPercentWidth(.80f);
		float dialogueBubbleWMargin = (assetHolder.getPercentWidth(1f)-dialogueBubbleWidth)*.5f;
		TextBounds tb = assetHolder.fontGreen.getWrappedBounds(msg.msg, dialogueBubbleWidth);
		
		// draw the background for the text with some padding
		Texture tex = assetHolder.redTex;
		if (msg.color.equals("green"))
			tex = assetHolder.greenTex;
		float msgPosOffsetY = .4f;
		float announceSize = assetHolder.getPercentHeight(.75f);
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(assetHolder.announcer, -announceSize*.25f, -announceSize*.2f, announceSize, announceSize); 
		batch.setColor(1.0f, 1.0f, 1.0f, alpha);
		assetHolder.dialogueBubble.draw(batch, dialogueBubbleWMargin, assetHolder.getPercentHeight(msg.pos+msgPosOffsetY-.057f)-(float)tb.height/2.0f,
			dialogueBubbleWidth, tb.height+assetHolder.getPercentHeight(.08f));
		
		// set the alpha of the font
		assetHolder.fontGreen.setColor(assetHolder.fontGreen.getColor().r, assetHolder.fontGreen.getColor().g, assetHolder.fontGreen.getColor().b, alpha);
		assetHolder.fontGreen.drawWrapped(batch, msg.msg, assetHolder.getPercentWidth(.5f)-(float)tb.width/2.0f, assetHolder.getPercentHeight(msg.pos+msgPosOffsetY)+(float)tb.height/2.0f, tb.width, HAlignment.CENTER);
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	
	public void removeSheepMessage()
	{
		resultTime = 0;
		shownBottomMenu = false;
		bottomMenu.clearChildren();
		sheepGame.removeOneMessage();
	}
	
	public boolean inOverlay()
	{
		return usingOverlay;
	}
	
	public void addRetryButtons(Table table)
	{
		TextButton retry = new TextButton("Restart Level", assetHolder.buttonStyle);
		retry.addListener(new InputListener(){
			private GameOverlay gOverlay;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				gOverlay.unpauseOverlay();
				gOverlay.retryLevel();
			}
			public InputListener setSceneChanger(GameOverlay s)
			{
				this.gOverlay = s;
				return this;
			}
		}.setSceneChanger(this));
		table.add(retry).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
		TextButton selectLevel = new TextButton("Main Menu", assetHolder.buttonStyle);
		selectLevel.addListener(new InputListener(){
			private GameOverlay gOverlay;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				gOverlay.unpauseOverlay();
				gOverlay.toMainMenu();
			}
			public InputListener setSceneChanger(GameOverlay s)
			{
				this.gOverlay = s;
				return this;
			}
		}.setSceneChanger(this));
		//table.add(selectLevel).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		//table.row();
		//if (assetHolder.levelLoader.currentLevelHasHelp())
		if (sheepGame.hasHelp())
			addHelpButton(table);
	}
	
	public void setupWinButtons()
	{
		bottomMenu.clearChildren();
		if (assetHolder.levelLoader.areMoveLevels())
		{
			TextButton nextLevel = new TextButton("Next Level", assetHolder.buttonStyle);
			nextLevel.addListener(new InputListener(){
				private GameOverlay gOverlay;
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
				{
					return true;
				}
				public void touchUp(InputEvent event, float x, float y, int pointer, int button)
				{
					gOverlay.unpauseOverlay();
					gOverlay.nextLevel();
				}
				public InputListener setSceneChanger(GameOverlay s)
				{
					this.gOverlay = s;
					return this;
				}
			}.setSceneChanger(this));
			bottomMenu.add(nextLevel).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			bottomMenu.row();
		}
		addRetryButtons(bottomMenu);
	}
	
	public void addHelpButton(Table table)
	{
		TextButton help = new TextButton("Replay Messages", assetHolder.buttonStyle);
		help.addListener(new InputListener(){
			private GameOverlay gOverlay;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				gOverlay.unpauseOverlay();
				gOverlay.retryLevelWithHelp();
			}
			public InputListener setSceneChanger(GameOverlay s)
			{
				this.gOverlay = s;
				return this;
			}
		}.setSceneChanger(this));
		table.add(help).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
	}
	
	public void setupLoseButtons()
	{
		bottomMenu.clearChildren();
		addRetryButtons(bottomMenu);
	}
	
	public void retryLevel()
	{
		bottomMenu.clearChildren();
		sheepGame.retryLevel();
	}
	
	public void retryLevelWithHelp()
	{
		bottomMenu.clearChildren();
		sheepGame.retryLevelWithHelp();
	}
	public void setCreatorName(String s)
	{
		creatorName = s;
	}
	public void setLevelName(String s)
	{
		levelName = s;
	}
	public void nextLevel()
	{
		bottomMenu.clearChildren();
		sheepGame.nextLevel();
	}
	public void addLevelName(Table table)
	{
		Label levelNameLabel = new Label(levelName, assetHolder.labelStyle);
		assetHolder.correctLabel(levelNameLabel);
		table.add(levelNameLabel).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(assetHolder.getBasicPadding()*.2f).row();
		if (creatorName == null || !creatorName.equals(""))
		{
			Label creatorNameLabel = new Label("By: "+creatorName, assetHolder.labelStyle);
			assetHolder.correctLabel(creatorNameLabel);
			table.add(creatorNameLabel).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(assetHolder.getBasicPadding()*.2f).row();
		}
	}
	public void rateLevel(int r)
	{
		//sheep.getTopMenu().rateLevel(
		Gdx.app.log("rating", ""+r);
		sheep.getTopMenu().rateLevel(levelName, creatorName, r);
	}
	public void addRate(Table table)
	{
		if (sheepGame.canRate())
		{
			Table starTable = new Table();
			for (int i = 0; i < 5; ++i)
			{
				Button star = new ImageButton(new TextureRegionDrawable(new TextureRegion(assetHolder.onStar)), new TextureRegionDrawable(new TextureRegion(assetHolder.offStar)));
				star.addListener(new ButtonListenBridge().setButtonListener(new ButtonListener()
				{
					public void buttonPressed(int id)
					{
						rateLevel(id);
					}
				}).setId(i));
				starTable.add(star).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).pad(1);
			}
			Label rateText = new Label("tap to rate:", assetHolder.labelStyle);
			assetHolder.correctLabel(rateText);
			table.add(rateText).size(assetHolder.getPercentWidth(.6f), assetHolder.getButtonHeight()).row();
			table.add(starTable).row();
		}
	}
	public void addResumeButton(Table table)
	{
		TextButton resume = new TextButton("Resume", assetHolder.buttonStyle);
		resume.addListener(new InputListener(){
			private GameOverlay gOverlay;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				gOverlay.unpauseOverlay();
			}
			public InputListener setSceneChanger(GameOverlay s)
			{
				this.gOverlay = s;
				return this;
			}
		}.setSceneChanger(this));
		table.add(resume).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
	}
	
	public void unpauseOverlay()
	{
		if (inOverlay)
		{
			paused = false;
			inOverlay = false;
			overlayMenu.clearChildren();
			inMux.removeProcessor(overlayStage);
			inMux.addProcessor(stage);
			sheep.overlayOff();
			for (int i = 0; i < extensions.size; ++i)
				extensions.get(i).addToInMux(inMux);
		}
	}
	
	public void showSheepMessageButtons()
	{
		if (sheepGame.isLosing())
		{
			setupLoseButtons();
		}else
		if (sheepGame.isWinning())
		{
			setupWinButtons();
		}else
		if (sheepGame.lastMessage())
		{
			bottomMenu.clearChildren();
			TextButton start = new TextButton("Start Level", assetHolder.buttonStyle);
			start.addListener(new InputListener(){
				private GameOverlay gOverlay;
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
				{
					return true;
				}
				public void touchUp(InputEvent event, float x, float y, int pointer, int button)
				{
					gOverlay.removeSheepMessage();
				}
				public InputListener setSceneChanger(GameOverlay s)
				{
					this.gOverlay = s;
					return this;
				}
			}.setSceneChanger(this));
			bottomMenu.add(start).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		}else
		{
			bottomMenu.clearChildren();
			TextButton next = new TextButton("Next", assetHolder.buttonStyle);
			next.addListener(new InputListener(){
				private GameOverlay gOverlay;
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
				{
					return true;
				}
				public void touchUp(InputEvent event, float x, float y, int pointer, int button)
				{
					gOverlay.removeSheepMessage();
				}
				public InputListener setSceneChanger(GameOverlay s)
				{
					this.gOverlay = s;
					return this;
				}
			}.setSceneChanger(this));
			bottomMenu.add(next).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		}
	}
	
	// this returns if opening the menu was successful
	// you can't open this menu if the game has ended or 
	// you're already in the menu
	// this boolean value is useful for sub classes
	public boolean doMenu()
	{
		if (!inOverlay && !sheepGame.isLosing() && !sheepGame.isWinning())
		{
			overlayMenu.clearChildren();
			inOverlay = true;
			paused = true;
			sheep.getTopMenu().show();
			addLevelName(overlayMenu);
			addRate(overlayMenu);
			addResumeButton(overlayMenu);
			addRetryButtons(overlayMenu);
			inMux.addProcessor(overlayStage);
			inMux.removeProcessor(stage);
			sheep.overlayOn();
			for (int i = 0; i < extensions.size; ++i)
				extensions.get(i).removeFromInMux(inMux);
			return true;
		}
		return false;
	}
	
	public boolean isPaused()
	{
		return paused || sheepGame.hasMessage();
	}
	
	public void toMainMenu()
	{
		sheep.gotoMenu("main");
	}
	
	public void goBack()
	{
		sheep.gotoMenu("level");
	}
	
}
