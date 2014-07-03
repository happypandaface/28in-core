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

public class EndlessInches extends SheepGame
{
	private Pen startPen;
	private Pen endPen;
	private Array<Vector2> oldPath = null;
	
	private float oldPathFade = 0f;
	private float oldPathFadeMax = 1f;
	
	private int numLevels;
	
	protected float scrollHeight;
	protected float scrollWidth;
	protected float scrollSpeed = 0.35f;
	protected float fastScrollSpeed = 2.4f;
	
	public void playEndless()
	{
		startPen = new Pen();
		startPen.set(1, 3);
		endPen = new Pen();
		endPen.set(3, 7);
		addTile(startPen);
		addTile(endPen);
		addTile(new SheepObj().setOffset(0));
		addTile(new SheepObj().setOffset(-4));
		playingEndless = true;
	}
	public void reset()
	{
		scrollHeight = 0;
		startPen = null;
		endPen = null;
		numLevels = 0;
		super.reset();
	}
	public void render()
	{
		float delta = Gdx.graphics.getDeltaTime();
		
		// lose conditions
		if (!losing && sheepPath.size == 0 && startPen.isOffScreen(getOffsetTileX(), getOffsetTileY()))
		{
			loseTheGame();
		}
		if (!losing && sheepPath.size > 0)
		{
			int highestPathY = 0;
			for (int i = 0; i < sheepPath.size; ++i)
				if (sheepPath.get(i).y > highestPathY)
					highestPathY = (int)sheepPath.get(i).y;
			if (highestPathY + getOffsetTileY() < -2)
				loseTheGame();
		}
		
		if (gameOverlay.isPaused())
			delta = 0;
		
		if (!gameOverlay.isPaused())
		{// only remove when not paused so that if doesn't break if you lose
			// remove objects that can't effect the game anymore
			for (int i = tiles.size-1; i >= 0; --i)
			{
				Tile t = tiles.get(i);
				if (t.isOffScreen(getOffsetTileX(), getOffsetTileY()))
				{
					tiles.removeIndex(i);
				}
			}
		}
		if (!doneWithPath())// for redrawing paths in endless
		{
			sheepVel = 0;
			canDirectSheep = false;
			sheepGo = false;
		}
		if (endPen.getPos().y > -getOffsetTileY()+getNumTilesY())
			scrollHeight += delta*fastScrollSpeed;
		else
			scrollHeight += delta*scrollSpeed;
		super.render();
	}
	public boolean checkInBounds(Vector2 add)
	{
		return add.x < numTilesX && add.x >= 0;
	}
	public boolean checkIsStartingPiece(Vector2 add)
	{
		if (startPen.getPos().x == add.x &&
			startPen.getPos().y == add.y)
			return true;
		return false;
	}
	public float getOffsetX()
	{
		return 0;
	}
	public float getOffsetY()
	{
		return -scrollHeight*getTileHeight();
	}
	public float getOffsetTileX()
	{
		return 0;
	}
	public float getOffsetTileY()
	{
		return -scrollHeight;
	}
	public void drawSheepPath(SpriteBatch batch, float delta)
	{
		if (oldPath != null && (oldPathFade-delta)/oldPathFadeMax > 0)
		{
			oldPathFade -= delta;
			float color = 0.2f;
			float tint = 0.3f*oldPathFade/oldPathFadeMax;
			drawAPath(batch, oldPath, color, tint, delta);
		}
		super.drawSheepPath(batch, delta);
	}
	
	public boolean doneWithPath()
	{
		if (sheepPath.size == 0)
			return false;
		Vector2 pos = sheepPath.get(sheepPath.size-1);
		if (endPen.getPos().x == pos.x &&
			endPen.getPos().y == pos.y)
			return true;
		return false;
	}
	public void removeTile(Tile t)
	{
		tiles.removeValue(t, true);
	}
	public void winTheGame()
	{
		numLevels++;
		//removeTile(startPen);
		startPen = endPen;
		oldPath = new Array<Vector2>();
		for (int i = 0; i < sheepPath.size; ++i)
		{
			oldPath.add(sheepPath.get(i));
		}
		//oldPath = new Array<Vector2>(sheepPath.toArray());
		oldPathFade = oldPathFadeMax;
		sheepPath.clear();
		endPen = new Pen();
		if (numLevels < 2) {
			pattern1(startPen, endPen);
		}else if (numLevels < 3)
		{
			pattern2(startPen, endPen);
		}else// if (numLevels < 5)
		{
			float chance = (float)Math.random();
			if (chance < .25f)
				pattern2(startPen, endPen);
			else
				pattern3(startPen, endPen);
		}
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof SheepObj)
				((SheepObj)t).resetSteps();
		}
		sheepVel = 0;
		flashPath = flashPathTime;
		//winning = true;
		//gameOverlay.newMessage();
		//messages.add(new SheepMessage("You Win!", .5f).setColor("green"));
	}
	public void pattern1(Pen startPen, Pen endPen)
	{
		float tileW = getTileWidth();
		float tileH = getTileHeight();
		endPen.set((int)Math.floor(Math.random()*getNumTilesX()), startPen.getPos().y+(4));
		addTile(endPen);
		for (int y = (int)startPen.getPos().y+1; y < (int)endPen.getPos().y; ++y)
		{
			int rndX1 = (int)Math.floor(Math.random()*getNumTilesX());
			addTile(new Boulder().set(rndX1, y));
			// generate a second one that's not on the same tile:
			int rndX2 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-2)+1);
			Gdx.app.log("rndX1, rndX2", ""+rndX1+", "+rndX2);
			rndX2 = rndX2%(int)getNumTilesX();
			addTile(new Boulder().set(rndX2, y));
		}
	}
	public void pattern2(Pen startPen, Pen endPen)
	{
		float tileW = getTileWidth();
		float tileH = getTileHeight();
		int totY = (int)Math.random()<.5?4:5;
		endPen.set((int)Math.floor(Math.random()*getNumTilesX()), startPen.getPos().y+(totY));
		addTile(endPen);
		int guardY = (int)Math.floor(Math.random()*(totY-2))+1;
		for (int y = (int)startPen.getPos().y+1; y < (int)endPen.getPos().y; ++y)
		{
			if (y != startPen.getPos().y+guardY)
			{
				int rndX1 = (int)Math.floor(Math.random()*getNumTilesX());
				addTile(new Boulder().set(rndX1, y));
				// generate a second one that's not on the same tile:
				int rndX2 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-2)+1);
				rndX2 = rndX2%(int)getNumTilesX();
				addTile(new Boulder().set(rndX2, y));
			}else
			{
				int startX = (int)Math.floor(Math.random()*3);
				int offset = (int)Math.floor(Math.random()*4);
				WalkPath path = new WalkPath();
				path.add(new Vector2(startX, y));
				path.add(new Vector2(startX+1, y));
				path.add(new Vector2(startX+2, y));
				path.add(new Vector2(startX+1, y));
				addTile(new Guard().setPath(path).setOffset(offset));
			}
		}
	}
	public void pattern3(Pen startPen, Pen endPen)
	{
		float tileW = getTileWidth();
		float tileH = getTileHeight();
		endPen.set((int)Math.floor(Math.random()*getNumTilesX()), startPen.getPos().y+(Math.random()<.5?4:5));
		addTile(endPen);
		for (int y = (int)startPen.getPos().y+1; y < (int)endPen.getPos().y; ++y)
		{
			if (y != startPen.getPos().y+3 && y != startPen.getPos().y+1)
			{
				int rndX1 = (int)Math.floor(Math.random()*getNumTilesX());
				addTile(new Boulder().set(rndX1, y));
				// generate a second one that's not on the same tile:
				int rndX2 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-2)+1);
				Gdx.app.log("rndX1, rndX2", ""+rndX1+", "+rndX2);
				rndX2 = rndX2%(int)getNumTilesX();
				addTile(new Boulder().set(rndX2, y));
			}else
			{
				int startX = (int)Math.floor(Math.random()*3);
				int offset = (int)Math.floor(Math.random()*4);
				WalkPath path = new WalkPath();
				path.add(new Vector2(startX, y));
				path.add(new Vector2(startX+1, y));
				path.add(new Vector2(startX+2, y));
				path.add(new Vector2(startX+1, y));
				addTile(new Guard().setPath(path).setOffset(offset));
			}
		}
	}
	public int greenOverlayY()
	{
		float tileH = getTileHeight();
		return (int)(startPen.getPos().y*tileH+getOffsetY());
	}
	public int startGreenOverlay()
	{
		return (int)(startPen.getPos().x+getOffsetTileX());
	}
	public int endGreenOverlay()
	{
		return (int)(startPen.getPos().x+getOffsetTileX()+1);
	}
	public int redOverlayY()
	{
		float tileH = getTileHeight();
		return (int)(endPen.getPos().y*tileH+getOffsetY());
	}
	public int startRedOverlay()
	{
		return (int)(endPen.getPos().x+getOffsetTileX());
	}
	public int endRedOverlay()
	{
		return (int)(endPen.getPos().x+getOffsetTileX()+1);
	}
	
	public void retryLevel()
	{
		//assetHolder.levelLoader.reloadLevel(this, false);
		reset();
		playEndless();
	}
	public void retryLevelWithHelp()
	{
		//assetHolder.levelLoader.reloadLevel(this, true);
	}
	public void nextLevel()
	{
		//assetHolder.levelLoader.nextLevel(this, true);
	}
}
