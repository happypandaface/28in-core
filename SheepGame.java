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

import com.mygdx.sheep.tiles.*;

public class SheepGame implements InputProcessor
{
	SpriteBatch batch;
	protected float numTilesX = 5;
	protected float numTilesY = 8;
	protected Array<Vector2> sheepPath;
	public LevelInfo tiles;
	public Array<SheepMessage> messages;
	public ArrayMap<String, Texture> texLink;
	protected boolean sheepGo;
	protected boolean canDirectSheep;
	protected AssetHolder assetHolder;
	protected sheep sheep;
	protected InputMultiplexer inMux;
	protected Tile touchedTile = null;
	protected float overlayOffset = 0;
	protected boolean losing = false;
	protected boolean winning = false;
	protected boolean skipTap = false;
	protected float sheepVel = 0;
	protected float sheepAccel = 4.0f;
	protected float sheepDeccel = 6.0f;
	protected float flashPath;
	protected float flashPathTime = 1f;
	protected GameOverlay gameOverlay = null;
	protected GameOverlay normOverlay;
	protected int cutsUsed;
	protected int cutsGained;
	
	protected boolean playingEndless;
	
	public SheepGame()
	{
		inMux = new InputMultiplexer();
		inMux.addProcessor(this);
		normOverlay = new GameOverlay();
		normOverlay.setSheepGame(this);
	}
	
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
		normOverlay.setAssetHolder(assetHolder);
	}
	
	public void setSheepMain(sheep s)
	{
		sheep = s;
		normOverlay.setSheepMain(s);
	}
	
	public void create()
	{
		normOverlay.create();
		inMux.addProcessor(this);
		
		sheepPath = new Array<Vector2>();
		texLink = new ArrayMap<String, Texture>();
		tiles = new LevelInfo();
		messages = new Array<SheepMessage>();
		batch = new SpriteBatch();
		texLink.put("sheep", new Texture(Gdx.files.internal("140616_Sheep RD1-BIG-sheep.png")));
		texLink.put("grass", new Texture(Gdx.files.internal("140616_Tile RD1-BIG.png")));
		texLink.put("boulder", new Texture(Gdx.files.internal("140621-28Inches-Tile-Boulder.png")));
		texLink.put("tallGrass", new Texture(Gdx.files.internal("140623-28Inches-Tile-Grass.png")));
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(new Color(1f, 1f, 1f, 1f));
			pixmap.fill();
			texLink.put("path", new Texture(pixmap));
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(new Color(0f, 0f, 0f, 1f));
			pixmap.fill();
			texLink.put("guard", new Texture(pixmap));
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(new Color(1f, 1f, 0f, .3f));
			pixmap.fill();
			texLink.put("light", new Texture(pixmap));
		}
		
		
	}
	
	public void reset()
	{
		playingEndless = false;
		if (gameOverlay != null)
			inMux.removeProcessor(gameOverlay.inMux);
		gameOverlay = normOverlay;
		gameOverlay.reset();
		inMux.addProcessor(gameOverlay.inMux);
		flashPath = flashPathTime;
		tiles.clear();
		messages.clear();
		//shownBottomMenu = false;
		cutsUsed = 0;
		cutsGained = 0;
		sheepVel = 0;
		skipTap = false;
		losing = false;
		winning = false;
		sheepPath.clear();
		sheepGo = false;
		canDirectSheep = false;
	}
	
	public void setOverlay(GameOverlay go)
	{
		if (gameOverlay != null)
			inMux.removeProcessor(gameOverlay.inMux);
		gameOverlay = go;
		inMux.addProcessor(gameOverlay.inMux);
	}
	public Array<Vector2> getSheepPath()
	{
		return sheepPath;
	}
	
	public void switchTo()
	{
		gameOverlay.unpauseOverlay();
		sheep.overlayOff();
		//Gdx.input.setInputProcessor(inMux);
	}
	public InputProcessor getInput()
	{
		return inMux;
	}
	
	public void addMessage(SheepMessage msg)
	{
		messages.add(msg);
	}
	public SheepMessage getMessage()
	{
		return messages.get(0);
	}
	public boolean hasMessage()
	{
		return messages.size > 0;
	}
	public boolean lastMessage()
	{
		return messages.size == 1;
	}
	public void removeOneMessage()
	{
		messages.removeIndex(0);
	}
	public void addTiles(Array<Tile> tiles)
	{
		for (int i = 0; i < tiles.size; ++i)
			addTile(tiles.get(i));
	}
	public void addTile(Tile t)
	{
		t.setAssetHolder(assetHolder);
		if (t instanceof SheepObj)
		{
			((SheepObj)t).setPath(sheepPath);
		}
		t.create(this);
		tiles.add(t);
	}
	
	public void render()
	{
		float realDelta = Gdx.graphics.getDeltaTime();// for all effects
		float delta = realDelta;// for game effects
		if (gameOverlay.isPaused())
			delta = 0;
		render(realDelta, delta);
	}
	public void render(float realDelta, float delta)
	{
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (sheepGo)
			sheepVel += delta*sheepAccel;
		else
			sheepVel -= delta*sheepDeccel;
		if (sheepVel > 1)
			sheepVel = 1;
		if (sheepVel < 0)
			sheepVel = 0;
		
		float tileW = getTileWidth();
		float tileH = getTileHeight();
		float startX = getStartX();
		float startY = getStartY();
		
		if (!gameOverlay.isPaused() && messages.size == 0)
		{
			for (int i = 0; i < tiles.size; ++i)
			{
				Tile t = tiles.get(i);
				t.update(delta);
			}
		}
		//batch.enableBlending();
		batch.begin();
		if (playingEndless)
		{
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			for (int x = 0; x < getNumTilesX(); ++x)
				for (int y = -1; y <= getNumTilesY()+1; ++y)
				{
					batch.draw(texLink.get("grass"), startX+x*tileW+getOffsetX()%getTileWidth(), startY+y*tileH+getOffsetY()%getTileHeight(), tileW, tileH);
				}
		}else
		{
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			for (int x = 0; x < getNumTilesX(); ++x)
				for (int y = 0; y < getNumTilesY(); ++y)
				{
					batch.draw(texLink.get("grass"), startX+x*tileW+getOffsetX(), startY+y*tileH+getOffsetY(), tileW, tileH);
				}
		}
		
		// draw everything but guards and sheep
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (!(t instanceof Guard) && !(t instanceof SheepObj))
			{
				t.draw(batch, delta);
			}
		}
		drawSheepPath(batch, delta);
		batch.setColor(1, 1, 1, 1);
		// draw guards
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof Guard)
			{
				t.draw(batch, delta);
			}
		}
		// these are for win/loss conditions
		int numSheep = 0;
		int doneSheep = 0;
		int deadSheep = 0;
		// this is the num of cuts you have
		int cutsCollected = 0;
		// draw and count sheep and other things
		//  like collected stuff and dead stuff
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof SheepObj)
			{
				++numSheep;
				t.draw(batch, delta);
			}
			if (t instanceof SheepObj && ((SheepObj)t).getCompleted())
				++doneSheep;
			if (t instanceof SheepObj && ((SheepObj)t).isDead())
				++deadSheep;
			if (t instanceof Cut && ((Cut)t).isCollected())
				++cutsCollected;
		}
		if (cutsCollected-cutsUsed > 0)
		{
			batch.draw(Cut.getTex(assetHolder), startX, startY, tileW, tileH);
		}
		// this makes the variable available to the tiles
		cutsGained = cutsCollected;
		if (doneSheep == numSheep && !winning)
			winTheGame();
		if (deadSheep > 0 && !losing)
			loseTheGame();
		if (sheepPath.size == 0)
		{
			overlayOffset += delta;
			if (overlayOffset > 1)
				overlayOffset -= 1;
			for (int x = startGreenOverlay(); x < endGreenOverlay(); ++x)
				batch.draw(assetHolder.startOverlay, x*getTileWidth()+getStartX(), greenOverlayY()+getStartY(), getTileWidth(), getTileHeight(), overlayOffset, 0, overlayOffset+1.0f, 1.0f);
		}else
		if (!doneWithPath())
		{
			overlayOffset += delta;
			if (overlayOffset > 1)
				overlayOffset -= 1;
			for (int x = startRedOverlay(); x < endRedOverlay(); ++x)
				batch.draw(assetHolder.endOverlay, x*getTileWidth()+getStartX(), redOverlayY()+getStartY(), getTileWidth(), getTileHeight(), overlayOffset, 0, overlayOffset+1.0f, 1.0f);
		}
		batch.end();
		gameOverlay.render(realDelta);
	}
	public int cutsAvailable()
	{
		return cutsGained-cutsUsed;
	}
	public void useCut()
	{
		cutsUsed++;
	}
	public int greenOverlayY()
	{
		return 0;
	}
	public int startGreenOverlay()
	{
		return 0;
	}
	public int endGreenOverlay()
	{
		return (int)numTilesX+1;
	}
	public int redOverlayY()
	{
		float tileH = getTileHeight();
		return (int)((numTilesY-1)*tileH);
	}
	public int startRedOverlay()
	{
		return 0;
	}
	public int endRedOverlay()
	{
		return (int)numTilesX+1;
	}
	public void drawSheepPath(SpriteBatch batch, float delta)
	{
		float color = 0.2f+0.8f*flashPath/flashPathTime;
		float tint = 0.3f+0.7f*flashPath/flashPathTime;
		if (!canDirectSheep)
		{
			color = 0.2f;
			tint = 0.3f;
		}
		if (canDirectSheep)
		{
			flashPath -= delta;
			if (flashPath < 0)
				flashPath = 0;
		}
		drawAPath(batch, sheepPath, color, tint, delta);
	}
	public void drawAPath(SpriteBatch batch, Array<Vector2> path, float color, float tint, float delta)
	{
		float tileW = getTileWidth();
		float tileH = getTileHeight();
		float startX = getStartX();
		float startY = getStartY();
		
		batch.setColor(color, color, color, tint);
		// the path you draw
		for (int i = 0; i < path.size; ++i)
		{
			Vector2 p = path.get(i);
			// make it flash after created
			batch.draw(texLink.get("path"), startX+p.x*tileW+getOffsetX(), startY+p.y*tileH+getOffsetY(), tileW, tileH);
		}
		
	}
	
	public void loseTheGame()
	{
		losing = true;
		gameOverlay.newMessage();
		messages.add(new SheepMessage("You Lose!", .5f));
	}
	
	public void winTheGame()
	{
		winning = true;
		gameOverlay.newMessage();
		messages.add(new SheepMessage("You Win!", .5f).setColor("green"));
	}
	
	public boolean doneWithPath()
	{
		if (sheepPath.size == 0)
			return false;
		if (sheepPath.get(sheepPath.size-1).y == numTilesY-1)
			return true;
		return false;
	}
	
	public boolean getSheepGo()
	{
		return sheepGo;
	}
	
	public float getSheepVel()
	{
		return sheepVel;
	}
	
	public float getNumTilesX()
	{
		return numTilesX;
	}
	
	public float getNumTilesY()
	{
		return numTilesY;
	}
	
	public float getStretchedTileWidth()
	{
		return Gdx.graphics.getWidth()/numTilesX;
	}
	
	public float getStretchedTileHeight()
	{
		return Gdx.graphics.getHeight()/(numTilesY+1);
	}
	
	public float getStartX()
	{
		return (Gdx.graphics.getWidth()-(numTilesX)*getTileWidth())/2f;
	}
	
	public float getStartY()
	{
		return (Gdx.graphics.getHeight()-(numTilesY+1)*getTileHeight())/2f;
	}
	
	public float getTileWidth()
	{
		float w = getStretchedTileWidth();
		float h = getStretchedTileHeight();
		float ratio = w/h;
		if (ratio > 1)
			w = h;
		return w;
	}
	
	public float getTileHeight()
	{
		float w = getStretchedTileWidth();
		float h = getStretchedTileHeight();
		float ratio = w/h;
		if (ratio < 1)
			h = w;
		return h;
	}
	
	public boolean keyDown(int keycode)
	{
		return false;
	}
	public boolean keyTyped(char character)
	{
		return false;
	}
	public boolean keyUp(int keycode)
	{
		return false;
	}
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}
	public boolean scrolled(int amount)
	{
		return false;
	}
	public boolean isWinning()
	{
		return winning;
	}
	public boolean isLosing()
	{
		return losing;
	}
	public boolean checkInBounds(Vector2 add)
	{
		return add.x < numTilesX && add.x >= 0 &&
			add.y < numTilesY && add.y >= 0;
	}
	public Vector2 getTouch(int screenX, int screenY)
	{
		float x = (float)screenX-getStartX();
		float y = (float)(Gdx.graphics.getHeight()-screenY)-getStartY();
		int tileX = (int)Math.floor((float)x/(float)getTileWidth()-getOffsetTileX());
		int tileY = (int)Math.floor((float)y/(float)getTileHeight()-getOffsetTileY());
		Vector2 add = new Vector2(tileX, tileY);
		return add;
	}
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (losing || winning || messages.size > 0)
		{
			skipTap = true;
		}
		if (!gameOverlay.isPaused())
		{
			Vector2 add = getTouch(screenX, screenY);
			startDrag(add);
		}
		return touchDragged(screenX, screenY, pointer);
	}
	public boolean checkIfCanPathOver(Tile t)
	{
		if (t instanceof Boulder)
		{
			return false;
		}
		return true;
	}
	public boolean checkIsStartingPiece(Vector2 add)
	{
		return add.y == 0;
	}
	public float getOffsetX()
	{
		return 0;
	}
	public float getOffsetY()
	{
		return 0;
	}
	public float getOffsetTileX()
	{
		return 0;
	}
	public float getOffsetTileY()
	{
		return 0;
	}
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if (!gameOverlay.isPaused())
		{
			if (!doneWithPath() && messages.size == 0)
			{
				Vector2 add = getTouch(screenX, screenY);
				doDrag(add);
			}else
			{
				
			}
		}
		return false;
	}
	public void startDrag(Vector2 add)
	{
		if (checkInBounds(add))
		{
			boolean tileTouch = false;
			if (canDirectSheep)
			{
				for (int i = 0; i < tiles.size; ++i)
				{
					Tile t = tiles.get(i);
					Vector2 currPos = t.getFlrPos();
					if (currPos.x == add.x &&
						currPos.y == add.y)
					{
						if (t.touchedDown())
						{
							touchedTile = t;
							tileTouch = true;
							break;
						}
					}
				}
			}
			if (!tileTouch && canDirectSheep)
				sheepGo = true;
		}
	}
	public void doDrag(Vector2 add)
	{
		if (checkInBounds(add))
		{
			boolean pieceWorks = true;
			for (int i = 0; i < sheepPath.size; ++i)
			{
				Vector2 p = sheepPath.get(i);
				if (p.x == add.x && p.y == add.y)
					pieceWorks = false;
			}
			for (int i = 0; i < tiles.size; ++i)
			{
				Tile t = tiles.get(i);
				if (!checkIfCanPathOver(t))
				{
					if (t.checkOverlap(add))
						pieceWorks = false;
				}
			}
			if (sheepPath.size == 0 && !checkIsStartingPiece(add))
				pieceWorks = false;
			if (sheepPath.size != 0 && sheepPath.get(sheepPath.size-1).cpy().sub(add).len() != 1)
				pieceWorks = false;
			if (pieceWorks)
				sheepPath.add(add);
			else
			{
				// if it's on the path, remove all beyond that one.
				for (int i = 0; i < sheepPath.size-1; ++i)// minus 1 form size (can't remove beyond the last one
				{
					Vector2 curr = sheepPath.get(i);
					if (curr.x == add.x &&
						curr.y == add.y)
					{
						sheepPath.removeRange(i+1, sheepPath.size-1);
						break;
					}
				}
			}
		}
	}
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (skipTap && (losing || winning || messages.size > 0))
		{
			gameOverlay.trySkip();
		}
		skipTap = false;
		if (!gameOverlay.isPaused())
		{
			Vector2 add = getTouch(screenX, screenY);
			touchReleased(add);
		}
		return false;
	}
	public void touchReleased(Vector2 add)
	{
		if (doneWithPath())
			canDirectSheep = true;
		if (canDirectSheep)
			sheepGo = false;
		if (checkInBounds(add))
		{
			if (!gameOverlay.isPaused())
			{
				for (int i = 0; i < tiles.size; ++i)
				{
					Tile t = tiles.get(i);
					Vector2 currPos = t.getFlrPos();
					if (currPos.x == add.x &&
						currPos.y == add.y)
					{
						if (t == touchedTile)
							touchedTile.touchedUp();
					}
				}
			}
		}
	}
	public LevelInfo getLevelInfo()
	{
		return tiles;
	}
	public void retryLevel()
	{
		assetHolder.levelLoader.reloadLevel(this, false);
	}
	
	public void retryLevelWithHelp()
	{
		assetHolder.levelLoader.reloadLevel(this, true);
	}
	
	public void nextLevel()
	{
		assetHolder.levelLoader.nextLevel(this, true);
	}
}
