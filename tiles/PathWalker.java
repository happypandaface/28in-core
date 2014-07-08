package com.mygdx.sheep.tiles;

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
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import com.mygdx.sheep.*;

public class PathWalker extends Tile
{
	public Array<Vector2> path;
	protected float speed = 1;
	protected float stepsThroughPath = 0;
	protected float offset = 0;
	protected Vector2 dir;
	protected Vector2 lastDir;
	protected float highestPathY = 0;
	protected boolean moving = true;
	
	public static class PathWalkerJson extends Tile.TileJson
	{
		public Array<Vector2> path;
		public float offset;
	}
	public TileJson continueJsonObject(Tile.TileJson tj)
	{
		super.continueJsonObject(tj);
		((PathWalkerJson)tj).path = getPath();
		((PathWalkerJson)tj).offset = offset;
		return tj;
	}
	public TileJson startJsonObject()
	{
		return new PathWalkerJson();
	}
	public String getTypeStr()
	{
		return "PathWalker";
	}
	public Tile makeFromJsonObject(TileJson tj)
	{
		super.makeFromJsonObject(tj);
		path = ((PathWalkerJson)tj).path;
		offset = ((PathWalkerJson)tj).offset;
		return this;
	}
	
	public PathWalker()
	{
		dir = new Vector2(0, 0);
		lastDir = new Vector2(0, 0);
		path = new Array<Vector2>();
	}
	
	public boolean isOffScreen(float offsetX, float offsetY)
	{
		float tileW = sheepGame.getTileWidth();
		float tileH = sheepGame.getTileHeight();
		if (highestPathY + offsetY < -1)
		{
			return true;
		}
		return false;
	}
	public boolean notInPath(Vector2 add)
	{
		for (int i = 0; i < getPath().size; ++i)
		{
			Vector2 v = getPath().get(i);
			if (v.x == add.x &&
				v.y == add.y)
			{
				return false;
			}
		}
		return true;
	}
	public Array<Vector2> getPath()
	{
		return path;
	}
	public PathWalker addPath(float x, float y)
	{
		// makes sure that even if update is never called, the
		// pathwalker still is oriented correctly.
		//if (path.size == 0)
		//	pos.set(x, y);
		getPath().add(new Vector2(x, y));
		pos = getPosition(0);
		if (y > highestPathY)
			highestPathY = y;
		//if (path.size == 2)
		//	dir = path.get(1).cpy().sub(path.get(0));
		return this;
	}
	public boolean getIsBouncing(int steps)
	{
		if ((int)Math.floor(steps+offset*.3f)%3 == 0)
		{
			return true;
		}
		return false;
	}
	protected final static int STAND_SIDE = 1;
	protected final static int SIDE_WALK1 = 2;
	protected final static int SIDE_WALK2 = 3;
	protected final static int STAND_UP = 4;
	protected final static int UP_WALK1 = 5;
	protected final static int UP_WALK2 = 6;
	protected final static int STAND_DOWN = 7;
	protected final static int DOWN_WALK1 = 8;
	protected final static int DOWN_WALK2 = 9;
	public Texture getWalkerTex(int type)
	{
		return null;
	}
	public Texture getTex()
	{
		if (dir.y == 0)
		{
			offsetY = 0;
			if (!moving)
				return getWalkerTex(STAND_SIDE);//assetHolder.newSheepTex1;
			
			int steps = ((int)(getStepsThroughPath()*3.0f));
			if (getIsBouncing(steps))
				offsetY = .03f;
			if (steps%2 == 0)
			{
				return getWalkerTex(SIDE_WALK1);//assetHolder.newSheepTex2;
			}else
			{
				return getWalkerTex(SIDE_WALK2);//assetHolder.newSheepTex3;
			}
		}else
		if (dir.x == 0)
		{
			offsetY = 0;
			if (!moving)
				return dir.y>0?getWalkerTex(STAND_UP):getWalkerTex(STAND_DOWN);//assetHolder.newSheepDownTex1;
			
			int steps = ((int)(getStepsThroughPath()*3.0f));
			if (getIsBouncing(steps))
				offsetY = .03f;
			if (steps%2 == 0)
				return dir.y>0?getWalkerTex(UP_WALK1):getWalkerTex(DOWN_WALK1);//assetHolder.newSheepDownTex2;
			else
				return dir.y>0?getWalkerTex(UP_WALK2):getWalkerTex(DOWN_WALK2);
		}
		return getWalkerTex(STAND_SIDE);
	}
	
	public PathWalker setPath(Array<Vector2> p)
	{
		for (int i = 0; i < p.size; ++i)
		{
			addPath(p.get(i).x, p.get(i).y);
		}
		return this;
	}
	public boolean isPathLegal()
	{
		Vector2 dist = path.get(0).cpy().sub(path.peek());
		// guards can only move one space at a time
		if (dist.len() == 1 || dist.len() == 0)
			return true;
		return false;
	}
	public PathWalker makePathLegal()
	{
		if (!isPathLegal())
		{
			// recursing backwards over the path and adding copies of all the 
			// tiles will make the path legal
			// illegal paths almost always have more than 2 tiles
			// currrently there is no way to make path that
			// disobeys this
			for (int i = path.size-2; i > 0; --i)
			{
				Vector2 p = path.get(i);
				addPath(p.x, p.y);
			}
		}
		return this;
	}
	
	public PathWalker addOffset()
	{
		offset++;
		pos = getPosition(0);
		return this;
	}
	public PathWalker setOffset(float o)
	{
		offset = o;
		pos = getPosition(0);
		return this;
	}
	
	public float getStepsThroughPath()
	{
		return stepsThroughPath;
	}
	
	public void resetSteps()
	{
		stepsThroughPath = 0;
	}
	
	public void update(float d)
	{
		stepsThroughPath += d*speed;
		pos = getPosition(stepsThroughPath);
	}
	
	public boolean getOnField()
	{
		float idxFlt = stepsThroughPath+offset;
		if (idxFlt > 0 && !getCompleted())
			return true;
		return false;
	}
	
	public boolean getCompleted()
	{
		float idxFlt = stepsThroughPath+offset;
		if (idxFlt > getPath().size-1 && getPath().size > 0)
			return true;
		return false;
	}
	
	public Vector2 getPosition(float steps)
	{
		if (getPath().size == 0)
			return new Vector2(0, 0);
		float idxFlt = steps+offset;
		while (idxFlt < 0)
			idxFlt += getPath().size;
		int idxTot = (int)Math.floor(idxFlt);
		float thisStep = idxFlt-idxTot;
		int idx = idxTot%getPath().size;
		int idxNext = (idxTot+1)%getPath().size;
		Vector2 rtn = getPath().get(idx).cpy();
		Vector2 dist = getPath().get(idxNext).cpy().sub(rtn);
		if (dist.x != dir.x && dist.y != dir.y)
			lastDir = dir.cpy();// this updates lastDir to the previous dir
		dir = dist.cpy();
		rtn.add(dist.scl(thisStep));
		return rtn;
	}
	public boolean checkCanDraw()
	{
		return (getExists());
	}
	public boolean shouldFlipAgain()
	{
		return false;
	}
	@Override
	public void draw(SpriteBatch batch, float delta)
	{
		boolean flipX = false;
		if (dir.x == -1 || (dir.x == 0 && lastDir.x == -1))
			flipX = true;
		if (shouldFlipAgain())
			flipX = !flipX;
		if (checkCanDraw())
			drawSprite(batch, getTex(), pos.x, pos.y, flipX);
	}
}
