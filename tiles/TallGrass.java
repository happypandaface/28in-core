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

public class TallGrass extends Tile
{
	private boolean slow = true;
	private float removeAnim = 0;

	public static Texture getTex(AssetHolder assetHolder)
	{
		return assetHolder.tallGrass1;
	}
	public Texture getTex()
	{
		if (removeAnim < .1f)
			return assetHolder.tallGrassAnim1;
		if (removeAnim < .2f)
			return assetHolder.tallGrassAnim2;
		if (removeAnim < .4f)
			return assetHolder.tallGrassAnim3;
		if (removeAnim < .45f)
			return assetHolder.tallGrassAnim3;
		return this.getTex(assetHolder);
	}

	@Override
	public void update(float delta)
	{
		if (!slow)
		{
			removeAnim += delta;
			if (removeAnim > .45f)
				exists = false;
		}
		super.update(delta);
	}

	public boolean slowsSheep()
	{
		return exists;
	}
	
	public boolean touchedDown()
	{
		if (slow)
			return true;
		return false;
	}
	
	public boolean touchedUp()
	{
		if (exists && sheepGame.cutsAvailable() > 0)
		{
			sheepGame.useCut();
			slow = false;
			return true;
		}
		return false;
	}
	public String getTypeStr()
	{
		return "TallGrass";
	}
}
