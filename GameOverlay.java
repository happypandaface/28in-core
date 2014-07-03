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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
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

public class GameOverlay
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
	protected Array<OverlayExtension> extensions;
	
	protected float resultTime = -1;
	protected float resultFadeDelay = 1.5f;
	protected float resultBannerAlpha = 0.8f;// this is how see through the overlays are (for messages in-game)
	
	protected boolean shownBottomMenu;
	
	public GameOverlay()
	{
		extensions = new Array<OverlayExtension>();
		batch = new SpriteBatch();
		stage = new Stage();
		inMux = new InputMultiplexer();
		inMux.addProcessor(stage);
		overlayStage = new Stage();
		
		topMenu = new Table();
		topMenu.setFillParent(true);
		topMenu.top();
		topMenu.left();
		stage.addActor(topMenu);
		
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
	
	public void create()
	{
		ImageButton back = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("140621-Menu-Button.png")))));
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
		for (int i = 0; i < extensions.size; ++i)
			extensions.get(i).create();
	}
	
	public void reset()
	{
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
			resultTime += delta;
			drawSheepMessage(sheepGame.getMessage());
			batch.end();
		}
		stage.act(delta);
		stage.draw();
		for (int i = 0; i < extensions.size; ++i)
			extensions.get(i).render(delta);
		if (inOverlay)
		{
			batch.begin();
			batch.setColor(0f, 0f, 0f, 0.75f);
			batch.draw(assetHolder.white, 0, 0, assetHolder.getPercentWidth(1), assetHolder.getPercentHeight(1));
			batch.end();
		}
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
		batch.setColor(1.0f, 1.0f, 1.0f, alpha);
		
		TextBounds tb = assetHolder.fontWhite.getMultiLineBounds(msg.msg);
		
		// draw the background for the text with some padding
		Texture tex = assetHolder.redTex;
		if (msg.color.equals("green"))
			tex = assetHolder.greenTex;
		batch.draw(tex, assetHolder.getPercentWidth(.45f)-(float)tb.width/2.0f, assetHolder.getPercentHeight(msg.pos-.025f)-(float)tb.height/2.0f,
			tb.width+assetHolder.getPercentWidth(.1f), tb.height+assetHolder.getPercentHeight(.05f));
		
		// set the alpha of the font
		assetHolder.fontWhite.setColor(1, 1, 1, alpha);
		assetHolder.fontWhite.drawMultiLine(batch, msg.msg, assetHolder.getPercentWidth(.5f)-(float)tb.width/2.0f, assetHolder.getPercentHeight(msg.pos)+(float)tb.height/2.0f, tb.width, HAlignment.CENTER);
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
		table.add(selectLevel).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
		if (assetHolder.levelLoader.currentLevelHasHelp())
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
	
	public void nextLevel()
	{
		bottomMenu.clearChildren();
		sheepGame.nextLevel();
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
