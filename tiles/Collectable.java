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

public class Collectable extends Tile
{
	protected boolean collected;
	public Collectable()
	{
		super();
		collected = false;
	}
	public boolean isCollected()
	{
		return collected;
	}
	public static Texture getTex(AssetHolder assetHolder)
	{
		return assetHolder.white;
	}
	public Texture getTex()
	{
		return this.getTex(assetHolder);
	}
	public void update(float delta)
	{
		if (getExists())
		{
			for (int i = 0; i < sheepGame.tiles.size; ++i)
			{
				Tile t = sheepGame.tiles.get(i);
				if (t instanceof SheepObj)
				{
					SheepObj so = ((SheepObj)t);
					if (so.getOnField() && so.checkOverlap(this) &&
						so.getExists())
					{
						setCollected(true);
					}
				}
			}
		}
	}
	public void setCollected(boolean b)
	{
		if (!collected)
		{
			if (sheepGame.collect(this))
			{
				// this means the game successfully collected
				// the item
				exists = false;
				collected = b;

			}
		}
	}
}
