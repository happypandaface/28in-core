package com.mygdx.sheep;

import com.badlogic.gdx.InputProcessor;
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

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

public class LevelEditor extends SheepGame
{
	private TileChooser tileChooser;
	private PathWalker pathWalker;
	private TestOverlay testOverlay;
	private EditorOverlay editOver;
	private float touchTimer;
	private float doubleTapTime = .3f;
	private Vector2 removeVec = null;
	private Vector2 firstTouch = null;
	
	public LevelEditor()
	{
		super();
		testOverlay = new TestOverlay();
		editOver = new EditorOverlay();
	}
	
	public void setAssetHolder(AssetHolder as)
	{
		super.setAssetHolder(as);
		testOverlay.setAssetHolder(assetHolder);
		editOver.setAssetHolder(assetHolder);
	}
	
	public void setSheepMain(sheep s)
	{
		super.setSheepMain(s);
		testOverlay.setSheepMain(sheep);
		editOver.setSheepMain(sheep);
	}
	
	public void create()
	{
		// adding extensions must come before calling create
		tileChooser = new TileChooser();
		super.create();
		testOverlay.setSheepGame(this);
		testOverlay.create();
		editOver.setSheepGame(this);
		editOver.displayFadedCut = false;
		editOver.addExtension(tileChooser);
		editOver.create();
	}
	public void reset()
	{
		touchTimer = doubleTapTime+1;
		super.reset();
		setOverlay(editOver);
		addTile(new SheepObj().setOffset(0));
		addTile(new SheepObj().setOffset(-4));
	}
	public void drawSheepPath(SpriteBatch batch, float delta)
	{
		if (pathWalker != null)
			drawAPath(batch, pathWalker.path, 0.3f, 0.2f, 0);
	}
	
	public void loseTheGame()
	{
	}
	
	public void render()
	{
		if (removeVec != null)
		{
			for (int i = tiles.size-1; i >= 0; --i)
			{
				Tile t = tiles.get(i);
				if (t.checkOverlap(removeVec))
				{
					if (t instanceof PathWalker)
					{
						editOver.setPathWalker((PathWalker)t);
					}else
						tiles.removeIndex(i);
				}
			}
			removeVec = null;
		}
		touchTimer += Gdx.graphics.getDeltaTime();
		render(0, 0);
	}
	public void winTheGame()
	{
		// overriden to disallow win
	}
	public void playGame()
	{
		String gameStr = assetHolder.levelLoader.toString(tiles);
		Gdx.app.log("gameStr", gameStr);
		assetHolder.levelLoader.loadFromString(sheep.getPuzzleMode(), gameStr);
		sheep.getPuzzleMode().setOverlay(testOverlay);
		testOverlay.setSheepGame(sheep.getPuzzleMode());
		testOverlay.reset();
		sheep.gotoMenu("game");
	}
	public void saveGame()
	{
		String gameStr = assetHolder.levelLoader.toString(tiles);
		Gdx.app.log("gameStr", gameStr);
	}
	@Override
	public boolean checkInBounds(Vector2 add)
	{
		if (add.y == 0)
			return false;
		if (add.y == getNumTilesY())
			return false;
		return super.checkInBounds(add);
	}
	public void startDrag(Vector2 add)
	{
		if (!gameOverlay.isPaused() && checkInBounds(add))
		{
			Tile addTile = tileChooser.getSelectedTile();
			boolean exists = false;
			for (int i = 0; i < tiles.size; ++i)
			{
				Tile t = tiles.get(i);
				if (t.checkOverlap(add))
				{
					if (t.getClass().equals(addTile.getClass()))
						exists = true;
					else
						if (!(t instanceof BlockUp ||
							t instanceof BlockDown ||
							t instanceof BlockLeft ||
							t instanceof BlockRight ||
							addTile instanceof BlockUp ||
							addTile instanceof BlockDown ||
							addTile instanceof BlockLeft ||
							addTile instanceof BlockRight))
						{
							exists = true;
						}
				}
			}
			if (!exists)
			{
				if (addTile instanceof PathWalker)
				{
					pathWalker = ((PathWalker)addTile);
					addTile(pathWalker.addPath(add.x, add.y));
				}else
				{
					addTile(addTile.set(add.x, add.y));
				}
			}
		}
	}
	public void doDrag(Vector2 add)
	{
		if (checkInBounds(add))
		{
			if (pathWalker != null)
			{
				if (worksAsPath(pathWalker, add) && pathWalker.notInPath(add))
					pathWalker.addPath(add.x, add.y);
			}else
			{
				startDrag(add);
			}
			/*
			boolean exists = false;
				Tile t = tiles.get(i);
				if (t.checkOverlap(add))
					exists = true;
			}
			if (!exists)
			{
				addTile(new Boulder().set(add.x, add.y));
			}
			*/
		}
	}
	public void touchReleased(Vector2 add)
	{
		Gdx.app.log("touchTimer", ""+touchTimer+", "+doubleTapTime);
		if (touchTimer != 0 && touchTimer < doubleTapTime)
		{
			if (checkInBounds(add) && firstTouch != null && add.epsilonEquals(firstTouch, .1f))
				removeVec = add;
		}else
			firstTouch = add;
		touchTimer = 0;
		if (pathWalker != null)
		{
			pathWalker.makePathLegal();
		}
		pathWalker = null;
	}
}
