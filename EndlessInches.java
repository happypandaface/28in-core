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
	private Array<Vector2> oldPath = null;
	
	private float oldPathFade = 0f;
	private float oldPathFadeMax = 1f;
	
	private int numLevels;
	
	protected float scrollHeight;
	protected float scrollWidth;
	protected float scrollSpeed = 0.35f;
	protected float fastScrollSpeed = 2.4f;
	
	private boolean madeFirstPath;
	
	public void playEndless()
	{
		startPen = new Pen();
		startPen.set((int)Math.floor(getNumTilesX()*Math.random()), 1);
		addTile(startPen);
		Pen startPenTemp = startPen;
		int penBuf = 2;
		for (int i = 0; i < 2; ++i)
		{
			Pen nextPen = new Pen();
			pattern1(startPenTemp, nextPen);
			addTile(nextPen);
			startPenTemp = nextPen;
		}
		addTile(new SheepObj().setOffset(0));
		addTile(new SheepObj().setOffset(-4));
		playingEndless = true;
	}
	public void reset()
	{
		scrollHeight = 0;
		madeFirstPath = false;
		startPen = null;
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
		if (canDirectSheep)
		{
			if (!gameOverlay.isPaused())
				sheep.overlayOff();
			madeFirstPath = true;
		}else
			if (numLevels == 0)// open tab menu if just starting
				sheep.overlayOn();
		
		// if they've already made one path and the next
		// pen is off screen, speed up a bit.
		if (madeFirstPath && nextPen().getPos().y > -getOffsetTileY()+getNumTilesY())
			scrollHeight += delta*fastScrollSpeed;
		else if (madeFirstPath)
			scrollHeight += delta*scrollSpeed;
		super.render();
	}
	public Pen lastPen()
	{
		// find the last pen,
		// which is the pen with the highest y
		// value that isn't the startPen
		Pen lastPen = null;
		int highestY = -1;
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof Pen && t != startPen)
			{
				int y = (int)t.getPos().y;
				if (highestY < y)
				{
					lastPen = (Pen)t;
					highestY = y;
				}
			}
		}
		return lastPen;
	}
	public Pen nextPen()
	{
		// find the next pen,
		// which is the pen with the lowest y
		// value that isn't the startPen
		// and isn't lower than the startPen
		Pen nextPen = null;
		int lowestY = -1;
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof Pen && t != startPen)
			{
				int y = (int)t.getPos().y;
				if (y > startPen.getPos().y && (lowestY == -1 || lowestY > y))
				{
					nextPen = (Pen)t;
					lowestY = y;
				}
			}
		}
		return nextPen;
	}
	public boolean checkInBoundsMove(Vector2 add)
	{
		return true;//add.x < numTilesX && add.x >= 0;
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
	public Pen getPenAt(Vector2 pos, Pen exclude)
	{
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof Pen && t != exclude)
			{
				if (t.getPos().x == pos.x &&
					t.getPos().y == pos.y)
					return (Pen)t;
			}
		}
		return null;
	}
	public boolean doneWithPath()
	{
		if (sheepPath.size == 0)
			return false;
		Vector2 pos = sheepPath.get(sheepPath.size-1);
		if (getPenAt(pos, startPen) != null)
			return true;
		//if (endPen.getPos().x == pos.x &&
		//	endPen.getPos().y == pos.y)
		//	return true;
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
		Pen nextPen = getPenAt(sheepPath.get(sheepPath.size-1), startPen);
		startPen = nextPen;
		oldPath = new Array<Vector2>();
		for (int i = 0; i < sheepPath.size; ++i)
		{
			oldPath.add(sheepPath.get(i));
		}
		//oldPath = new Array<Vector2>(sheepPath.toArray());
		oldPathFade = oldPathFadeMax;
		sheepPath.clear();
		Pen endPen = new Pen();
		pattern1(lastPen(), endPen);
		/*
		if (numLevels < 2) {
			pattern2(startPen, endPen);
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
		}*/
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
	public Array<Vector2> getPath(Vector2 start, Vector2 end, float directness)
	{
		Array<Vector2> path = new Array<Vector2>();
		path.add(new Vector2(start.x, start.y));
		final int OPT_LEFT = 0;
		final int OPT_RIGHT = 1;
		final int OPT_UP = 2;
		final int OPT_DOWN = 4;
		while(true)
		{
			int x = (int)path.get(path.size-1).x;
			int y = (int)path.get(path.size-1).y;
			// get the direction of the end
			Array<Integer> fav = new Array<Integer>();
			if (x < end.x)
				fav.add(OPT_RIGHT);
			if (x > end.x)
				fav.add(OPT_LEFT);
			if (y < end.y)
				fav.add(OPT_UP);
			if (y > end.y)
				fav.add(OPT_DOWN);
			// get possible options
			Array<Integer> opts = new Array<Integer>();
			if (x < getNumTilesX()-1)
				opts.add(OPT_RIGHT);
			if (x > 0)
				opts.add(OPT_LEFT);
			if (y < end.y)
				opts.add(OPT_UP);
			if (y > start.y)
				opts.add(OPT_DOWN);
			// if there's no favored options, we're at our destination
			if (fav.size == 0)
				break;
			// rnd choose to do a fav one or opt one
			Array<Integer> dirs = Math.random()<directness?fav:opts;
			// pick which option to do randomly
			float chance = (float)Math.random();
			float optChance = 1f/(float)opts.size;
			for (int i = 0; i < dirs.size; ++i)
			{
				if (chance < (float)(i+1)*optChance ||
					i == dirs.size-1)// last one
				{
					// do this option
					switch(dirs.get(i))
					{
						case OPT_LEFT:
							x -= 1;
							break;
						case OPT_RIGHT:
							x += 1;
							break;
						case OPT_UP:
							y += 1;
							break;
						case OPT_DOWN:
							y -= 1;
							break;
					}
					// exit the loop b/c we did the option
					break;
				}
			}
			// add this to the path
			path.add(new Vector2(x, y));
		}
		return path;
	}
	public String getLoseText()
	{
		return "You cleared:\n"+numLevels+" levels";
	}
	public Array<Vector2> makeFullPath(int numLinks, int startY, int range, int linkDist, Array<Array<Vector2>> paths)
	{
		boolean retry = true;
		Array<Vector2> guardPath = new Array<Vector2>();
		while(retry)// iterate til we get a good path
		{
			retry = false;
			guardPath.clear();
			for (int i = 0; i < numLinks; ++i)
			{
				Vector2 startGuardPos = null;
				int plusX = 0;
				int plusY = 0;
				do
				{	
					startGuardPos = i==0?new Vector2(getRndX(), getRndY(startY, range)):guardPath.peek();
					int distribution = (int)Math.floor(Math.random()*(float)linkDist);
					plusX = (int)(linkDist-distribution)*(Math.random()<.5?1:-1);
					plusY = (int)(distribution)*(Math.random()<.5?1:-1);
				}while(
					!(
						(plusX != 0 || plusY != 0) && // make sure it's a different tile
						startGuardPos.x+plusX < getNumTilesX() &&// make sure this is in bounds
						startGuardPos.y+plusY < startY+range &&// sorry for the double negatives
						startGuardPos.x+plusX >= 0 &&
						startGuardPos.y+plusY >= startY
					)
				);
				Vector2 endGuardPos = i<numLinks-1?new Vector2(startGuardPos.x+plusX, startGuardPos.y+plusY):guardPath.get(0);
				Array<Vector2> addingGuardPath = getPath(startGuardPos, endGuardPos, 1f);
				if (i != 0)
					addingGuardPath.removeIndex(0);
				if (i == numLinks-1)
					addingGuardPath.pop();
				//if (i != 0 && addingGuardPath.size > 0)// dunno why this is needed
				//	addingGuardPath.removeIndex(0);
				guardPath.addAll(addingGuardPath);
			}
			//make sure the guard isn't fully on a win path:
			//but has at least one tile on a win path
			if (!retry)// if we haven't decided to retry at this point
			{
				for (int p = 0; p < paths.size; ++p)
				{
					int numOn = 0;
					Array<Vector2> path = paths.get(p);
					for (int i = 0; i < path.size; ++i)
					{
						Vector2 pathVec = path.get(i);
						boolean isOn = false;
						for (int c = 0; c < guardPath.size; ++c)
						{
							Vector2 guardVec = guardPath.get(c);
							if (guardVec.x == pathVec.x &&
								guardVec.y == pathVec.y)
							{
								isOn = true;
							}
						}
						if (isOn)
							++numOn;
					}
					if (numOn == 0 || numOn == guardPath.size)// if there's none on or all on, retry
					{
						retry = true;
						break;
					}
				}
			}
		}
		return guardPath;
	}
	public int getRndY(int start, int range)
	{
		return (int)Math.floor(Math.random()*range)+start;
	}
	public int getRndX()
	{
		return (int)Math.floor(Math.random()*getNumTilesX());
	}
	public void pattern1(Pen startPen, Pen endPen)
	{
		int dist = 6;
		endPen.set((int)Math.floor(Math.random()*getNumTilesX()), startPen.getPos().y+(dist));
		addTile(endPen);
		Array<Array<Vector2>> winPaths = new Array<Array<Vector2>>();
		Array<Array<Vector2>> paths = new Array<Array<Vector2>>();// to clear boulders later
		int numPaths = 4;
		for (int i = 0; i < numPaths; ++i)
		{
			Array<Vector2> path = getPath(startPen.getPos(), endPen.getPos(), .75f);
			// put some grass on the path (except pens)
			for (int c = 1; c < path.size-1; ++c)
			{
				if (Math.random() < .025f)
					addTile(new Cut().set(path.get(c)));
				else if (Math.random() < .05f)
					addTile(new TallGrass().set(path.get(c)));
			}
			winPaths.add(path);// add to win path for guards
			paths.add(path);
		}
		int numGuards = 1+(int)Math.floor(Math.random()*(float)numLevels/5f);
		int maxDist = 5;
		int distRange = 2;
		int numLinks = 2;
		for (int i = 0; i < numGuards; ++i)
		{
			int currDist = maxDist-(int)Math.floor(Math.random()*(float)maxDist);
			Array<Vector2> guardPath = makeFullPath(numLinks, (int)startPen.getPos().y+1, dist-1, maxDist, winPaths);
			addTile(new Guard().setPath(guardPath));
			paths.add(guardPath);
		}
		// fill all tiles that aren't on the path with boulders
		for (int x = 0; x < getNumTilesX(); ++x)
			for (int y = (int)startPen.getPos().y+1; y < endPen.getPos().y; ++y)
			{
				boolean notInPath = true;
				if (Math.random() < .1f)
					notInPath = false;
				else
					pathsloop:// make sure the tile isn't a boulder already
					for (int c = 0; c < paths.size; ++c)
					{
						Array<Vector2> currPath = paths.get(c);
						for (int i = 0; i < currPath.size; ++i)
							if (currPath.get(i).x == x &&
								currPath.get(i).y == y)
							{
								notInPath = false;
								break pathsloop;
							}
					}
				if (notInPath)
					addTile(new Boulder().set(x, y));
			}
	}
	
	public void patternFirst(Pen startPen, Pen endPen)
	{
		float tileW = getTileWidth();
		float tileH = getTileHeight();
		endPen.set((int)Math.floor(Math.random()*getNumTilesX()), startPen.getPos().y+(4));
		addTile(endPen);
		int rndX1 = (int)Math.floor(Math.random()*getNumTilesX());
		int rndX2 = (int)Math.floor(Math.random()*getNumTilesX());
		for (int y = (int)startPen.getPos().y+1; y < (int)endPen.getPos().y; ++y)
		{
			if (y%2 == 1)
			{
				// generate a second one that's not on the same tile:
				rndX2 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-3)+2);
				rndX2 = rndX2%(int)getNumTilesX();
				addTile(new Boulder().set(rndX2, y));
			}
			rndX1 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-3)+2);
			rndX1 = rndX1%(int)getNumTilesX();
			addTile(new Boulder().set(rndX1, y));
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
		int rndX1 = (int)Math.floor(Math.random()*getNumTilesX());
		int rndX2 = (int)Math.floor(Math.random()*getNumTilesX());
		for (int y = (int)startPen.getPos().y+1; y < (int)endPen.getPos().y; ++y)
		{
			if (y != startPen.getPos().y+guardY)
			{
				if (y%2 == 1)
				{
					// generate a second one that's not on the same tile:
					rndX2 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-3)+2);
					rndX2 = rndX2%(int)getNumTilesX();
					addTile(new Boulder().set(rndX2, y));
				}
				rndX1 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-3)+2);
				rndX1 = rndX1%(int)getNumTilesX();
				addTile(new Boulder().set(rndX1, y));
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
		int rndX1 = (int)Math.floor(Math.random()*getNumTilesX());
		int rndX2 = (int)Math.floor(Math.random()*getNumTilesX());
		for (int y = (int)startPen.getPos().y+1; y < (int)endPen.getPos().y; ++y)
		{
			if (y != startPen.getPos().y+3 && y != startPen.getPos().y+1)
			{
				if (y%2 == 1)
				{
					// generate a second one that's not on the same tile:
					rndX2 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-3)+2);
					rndX2 = rndX2%(int)getNumTilesX();
					addTile(new Boulder().set(rndX2, y));
				}
				rndX1 = rndX1+(int)Math.floor(Math.random()*(getNumTilesX()-3)+2);
				rndX1 = rndX1%(int)getNumTilesX();
				addTile(new Boulder().set(rndX1, y));
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
		// TODO:
		// change endPen to nextPen
		// use code from render
		return (int)(nextPen().getPos().y*tileH+getOffsetY());
	}
	public int startRedOverlay()
	{
		return (int)(nextPen().getPos().x+getOffsetTileX());
	}
	public int endRedOverlay()
	{
		return (int)(nextPen().getPos().x+getOffsetTileX()+1);
	}
	@Override
	public void switchTo()
	{
		sheep.hideProfile();
		gameOverlay.unpauseOverlay();
		//sheep.overlayOff();
		//Gdx.input.setInputProcessor(inMux);
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
