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

public class Dog extends Enemy
{
	public static Texture getTex(AssetHolder assetHolder)
	{
		return assetHolder.newDogTex1;
	}
	public String getTypeStr()
	{
		return "Dog";
	}
	@Override
	public float getOffsetY()
	{
		return super.getOffsetY()-.2f;
	}
	@Override
	public boolean shouldFlipAgain()
	{
		return true;
	}
	@Override
	public Texture getWalkerTex(int type)
	{
		switch(type)
		{
			case STAND_SIDE:
				return assetHolder.newDogTex1;
			case SIDE_WALK1:
				return assetHolder.newDogTex2;
			case SIDE_WALK2:
				return assetHolder.newDogTex3;
			case STAND_UP:
				return assetHolder.newDogUpTex1;
			case UP_WALK1:
				return assetHolder.newDogUpTex2;
			case UP_WALK2:
				return assetHolder.newDogUpTex3;
			case STAND_DOWN:
				return assetHolder.newDogDownTex1;
			case DOWN_WALK1:
				return assetHolder.newDogDownTex2;
			case DOWN_WALK2:
				return assetHolder.newDogDownTex3;
		}
		return super.getWalkerTex(type);
	}
}
