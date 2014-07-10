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

public class SheepObj extends PathWalker
{
	protected boolean dead = false;
	
	public SheepObj()
	{
		speed = 2.6f;
	}
	public String getTypeStr()
	{
		return "Sheep";
	}
	
	@Override
	public boolean getOnField()
	{
		// commented out so you can see the sheep that got caught
		//if (dead)
		//	return false;
		return super.getOnField();
	}
	
	public Array<Vector2> getPath()
	{
		if (sheepGame != null)
			return sheepGame.getSheepPath();
		return super.getPath();
	}
	
	public void gitRekt()
	{
		dead = true;
	}
	
	public boolean isDead()
	{
		return dead;
	}
	public boolean isOffScreen(float offsetX, float offsetY)
	{
		return false;
	}
	
	@Override
	public void update(float delta)
	{
		float moveSpeed = 1;
		for (int i = 0; i < sheepGame.tiles.size; ++i)
		{
			Tile t = sheepGame.tiles.get(i);
			if (t instanceof TallGrass)
			{
				TallGrass tg = (TallGrass)t;
				Vector2 dist = tg.getRndPos().cpy().sub(getRndPos());
				if (tg.checkOverlap(this) &&
					tg.slowsSheep())
				{
					moveSpeed = .5f;
				}
			}
		}
		if (sheepGame.getSheepVel() > 0)
		{
			moving = true;
			stepsThroughPath += delta*speed*moveSpeed*sheepGame.getSheepVel();
		}else
		{
			moving = false;
		}
		pos = getPosition(stepsThroughPath);
	}
	@Override
	public boolean shouldFlipAgain()
	{
		return (dir.y == -1);
	}
	@Override
	public boolean checkCanDraw()
	{
		return (getExists() && getOnField() && !getCompleted());
	}
	@Override
	public Texture getWalkerTex(int type)
	{
		switch(type)
		{
			case STAND_SIDE:
				return assetHolder.newSheepTex1;
			case SIDE_WALK1:
				return assetHolder.newSheepTex2;
			case SIDE_WALK2:
				return assetHolder.newSheepTex3;
			case STAND_UP:
				return assetHolder.newSheepUpTex1;
			case UP_WALK1:
				return assetHolder.newSheepUpTex2;
			case UP_WALK2:
				return assetHolder.newSheepUpTex3;
			case STAND_DOWN:
				return assetHolder.newSheepDownTex1;
			case DOWN_WALK1:
				return assetHolder.newSheepDownTex2;
			case DOWN_WALK2:
				return assetHolder.newSheepDownTex3;
		}
		return super.getWalkerTex(type);
	}
	
	
	public static Texture getTex(AssetHolder assetHolder)
	{
		return assetHolder.sheepTex1;
	}
}
