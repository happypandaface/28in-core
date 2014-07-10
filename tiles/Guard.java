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

public class Guard extends Enemy
{
	public static Texture getTex(AssetHolder assetHolder)
	{
		return assetHolder.newGuardTex1;
	}
	/*
	public Texture getTex()
	{
		Texture tex = assetHolder.guardTex3;
		int steps = ((int)(getStepsThroughPath()*3.0f));
		if (steps%2 == 0)
			tex = assetHolder.guardTex2;
		return tex;
	}*/
	public String getTypeStr()
	{
		return "Guard";
	}
	@Override
	public Texture getWalkerTex(int type)
	{
		switch(type)
		{
			case STAND_SIDE:
				return assetHolder.newGuardTex1;
			case SIDE_WALK1:
				return assetHolder.newGuardTex2;
			case SIDE_WALK2:
				return assetHolder.newGuardTex3;
			case STAND_UP:
				return assetHolder.newGuardUpTex1;
			case UP_WALK1:
				return assetHolder.newGuardUpTex2;
			case UP_WALK2:
				return assetHolder.newGuardUpTex3;
			case STAND_DOWN:
				return assetHolder.newGuardDownTex1;
			case DOWN_WALK1:
				return assetHolder.newGuardDownTex2;
			case DOWN_WALK2:
				return assetHolder.newGuardDownTex3;
		}
		return super.getWalkerTex(type);
	}
	@Override
	public boolean shouldFlipAgain()
	{
		return true;
	}
	/*
	public void draw(SpriteBatch batch, float delta)
	{
		Texture tex = assetHolder.guardTex3;
		int steps = ((int)(getStepsThroughPath()*3.0f));
		if (steps%2 == 0)
			tex = assetHolder.guardTex2;
		
		drawSprite(batch, tex, pos.x, pos.y);
		drawSprite(batch, sheepGame.texLink.get("light"), pos.x+dir.x, pos.y+dir.y);
	}*/
}
