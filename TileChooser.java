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
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.lang.reflect.Method;

import com.mygdx.sheep.tiles.*;

public class TileChooser extends OverlayExtension
{
	private Image currentTileImg;
	private final static int BOULDER = 1;
	private final static int DOG = 2;
	private final static int GUARD = 3;
	private final static int TALL_GRASS = 4;
	private final static int TOTAL_TILES = 4;
	private int currentTile = 1;
	
	public TileChooser()
	{
	}
	
	public void create()
	{
		super.create();
		Gdx.app.log("tile chooser", "created tile chooser");
		Table topRightTable = new Table();
		topRightTable.setFillParent(true);
		topRightTable.top();
		topRightTable.right();
		
		TextButton saveGame = new TextButton("save", assetHolder.buttonStyle);
		saveGame.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				saveGame();
			}
		});
		TextButton playGame = new TextButton("play", assetHolder.buttonStyle);
		playGame.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				playGame();
			}
		});
		TextButton tileTypeRight = new TextButton(">", assetHolder.buttonStyle);
		tileTypeRight.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				scrollRightTiles();
			}
		});
		currentTileImg = new Image();
		setTexImage();
		TextButton tileTypeLeft = new TextButton("<", assetHolder.buttonStyle);
		tileTypeLeft.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				scrollLeftTiles();
			}
		});
		topRightTable.add(saveGame).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentHeightInt(assetHolder.buttonHeight*1.5f)).pad(5);
		topRightTable.add(playGame).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentHeightInt(assetHolder.buttonHeight*1.5f)).pad(5);
		topRightTable.add(tileTypeLeft).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentHeightInt(assetHolder.buttonHeight));
		topRightTable.add(currentTileImg).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentHeightInt(assetHolder.buttonHeight));
		topRightTable.add(tileTypeRight).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentHeightInt(assetHolder.buttonHeight));
		topRightTable.pad(10);
		stage.addActor(topRightTable);
	}
	public void saveGame()
	{
		if (sheepGame instanceof LevelEditor)
		{
			((LevelEditor)sheepGame).saveGame();
		}
	}
	public void playGame()
	{
		if (sheepGame instanceof LevelEditor)
		{
			((LevelEditor)sheepGame).playGame();
		}
	}
	public void scrollRightTiles()
	{
		currentTile = (currentTile+1);
		if (currentTile == TOTAL_TILES+1)
			currentTile = 1;
		setTexImage();
	}
	public void scrollLeftTiles()
	{
		currentTile = (currentTile-1);
		if (currentTile == 0)
			currentTile = TOTAL_TILES;
		setTexImage();
	}
	public void setTexImage()
	{
		currentTileImg.setDrawable(new TextureRegionDrawable(new TextureRegion(getTex(currentTile))));
	}
	public Tile getSelectedTile()
	{
		switch (currentTile)
		{
			case BOULDER:
				return new Boulder();
			case DOG:
				return new Dog();
			case GUARD:
				return new Guard();
			case TALL_GRASS:
				return new TallGrass();
		}
		return null;
	}
	public void addSelectedTile(Vector2 pos, SheepGame sheepGame)
	{
		switch (currentTile)
		{
			case BOULDER:
				sheepGame.addTile(new Boulder().set(pos.x, pos.y));
				break;
			case DOG:
				sheepGame.addTile(new Dog().addPath(pos.x, pos.y));
				break;
			case GUARD:
				sheepGame.addTile(new Guard().addPath(pos.x, pos.y));
				break;
			case TALL_GRASS:
				sheepGame.addTile(new TallGrass().set(pos.x, pos.y));
				break;
		}
		
	}
	
	public Texture getTex(int tex)
	{
		switch (tex)
		{
			case BOULDER:
				return Boulder.getTex(assetHolder);
			case DOG:
				return Dog.getTex(assetHolder);
			case GUARD:
				return Guard.getTex(assetHolder);
			case TALL_GRASS:
				return TallGrass.getTex(assetHolder);
		}
		return null;
	}
}