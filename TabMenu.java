package com.mygdx.sheep;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

import com.mygdx.sheep.ui.ButtonListener;
import com.mygdx.sheep.ui.ButtonListenBridge;

public class TabMenu implements ButtonListener
{
	private sheep sheep;
	private Stage stage;
	private Table bottomTable;
	private ImageButton tab1;
	private ImageButton tab2;
	private ImageButton tab3;
	private Image tab1Image;
	private Image tab2Image;
	private Image tab3Image;
	public final static int LEVEL_SELECT = 2;
	public final static int ENDLESS_GAME = 3;
	public final static int MULTI_MENU = 4;
	private float percentTabWidth = 1.0f/3.0f;
	private AssetHolder assetHolder;
	private boolean bottomMenuUp;
	private float percentAnimated;
	private float animationSpeed = 3f;
	private float distanceToAnimate;
	private float bounceAmount = 0.3f;
	private boolean created = false;
	
	public void setSheep(sheep s)
	{
		bottomMenuUp = true;
		sheep = s;
	}
	public void setAssetHolder(AssetHolder ah)
	{
		assetHolder = ah;
	}
	public void create()
	{
		stage = new Stage();
		bottomTable = new Table();
		bottomTable.bottom();
		bottomTable.setFillParent(true);
		
		tab1 = new ImageButton(assetHolder.getDrawable(assetHolder.singlePlayerTabUp), assetHolder.getDrawable(assetHolder.singlePlayerTabDown));
		tab2 = new ImageButton(assetHolder.getDrawable(assetHolder.multiplayerTabUp), assetHolder.getDrawable(assetHolder.multiplayerTabDown));
		tab3 = new ImageButton(assetHolder.getDrawable(assetHolder.endlessTabUp), assetHolder.getDrawable(assetHolder.endlessTabDown));
		tab1Image = new Image(assetHolder.getDrawable(assetHolder.singlePlayerTabSelected));
		tab2Image = new Image(assetHolder.getDrawable(assetHolder.multiplayerTabSelected));
		tab3Image = new Image(assetHolder.getDrawable(assetHolder.endlessTabSelected));
		tab1.addListener(new ButtonListenBridge().setButtonListener(this).setId(LEVEL_SELECT));
		tab2.addListener(new ButtonListenBridge().setButtonListener(this).setId(MULTI_MENU));
		tab3.addListener(new ButtonListenBridge().setButtonListener(this).setId(ENDLESS_GAME));
		float buttonHeight = assetHolder.getPercentWidthInt(percentTabWidth*(200.0f/355.0f));
		stage.addActor(bottomTable);
		distanceToAnimate = -buttonHeight-2;
		buttonPressed(LEVEL_SELECT);
		created = true;
	}
	public void buttonPressed(int id)
	{
		float buttonHeight = assetHolder.getPercentWidthInt(percentTabWidth*(200.0f/355.0f));
		bottomTable.clearChildren();
		if (id == LEVEL_SELECT)
			bottomTable.add(tab1Image).height(buttonHeight).width(assetHolder.getPercentWidthInt(percentTabWidth));
		else
			bottomTable.add(tab1).height(buttonHeight).width(assetHolder.getPercentWidthInt(percentTabWidth));
		if (id == MULTI_MENU)
			bottomTable.add(tab2Image).height(buttonHeight).width(assetHolder.getPercentWidthInt(percentTabWidth));
		else
			bottomTable.add(tab2).height(buttonHeight).width(assetHolder.getPercentWidthInt(percentTabWidth));
		if (id == ENDLESS_GAME)
			bottomTable.add(tab3Image).height(buttonHeight).width(assetHolder.getPercentWidthInt(percentTabWidth));
		else
			bottomTable.add(tab3).height(buttonHeight).width(assetHolder.getPercentWidthInt(percentTabWidth));
		if (created)
			sheep.buttonPressed(id);
	}
	public InputProcessor getInput()
	{
		return stage;
	}
	public void animateDown()
	{
//		sheep.removeInMux(stage);
		bottomMenuUp = false;
	}
	public void animateUp()
	{
		bottomMenuUp = true;
	}
	public float bounceFunctDown(float percent)
	{
		return percent*percent;
	}
	public float bounceFunctUp(float percent)
	{
		return (float)(percent*percent*Math.abs(Math.sin((-Math.pow(percent-1,2f)+1)*3f*Math.PI/2f)));
	}
	public void render()
	{
		float delta = Gdx.graphics.getDeltaTime();
		if (bottomMenuUp)
		{
			percentAnimated -= delta*animationSpeed;
			if (percentAnimated < 0)
				percentAnimated = 0;
			bottomTable.setY(bounceFunctUp(percentAnimated)*distanceToAnimate);
			
		}else
		{
//			distanceToAnimate
			percentAnimated += delta*animationSpeed;
			if (percentAnimated > 1)
				percentAnimated = 1;
			bottomTable.setY(bounceFunctDown(percentAnimated)*distanceToAnimate);
		}
		stage.act(delta);
		stage.draw();
	}
}
