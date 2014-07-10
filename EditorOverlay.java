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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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

import com.mygdx.sheep.tiles.*;

import com.mygdx.sheep.ui.ButtonListener;
import com.mygdx.sheep.ui.ButtonListenBridge;

public class EditorOverlay extends GameOverlay 
{
	private PathWalker pwToEdit = null;
	private Table bottomMenu;
	private static final int DELETE = HIGHEST_ENUM+1;
	private static final int OFFSET = HIGHEST_ENUM+2;
	private static final int RETURN = HIGHEST_ENUM+3;
	public boolean addingReload= false;

	public EditorOverlay()
	{
		super();
		bottomMenu = new Table();
		bottomMenu.setFillParent(true);
		bottomMenu.bottom();
		bottomMenu.center();
		stage.addActor(bottomMenu);
	}
	@Override
	public void addReloadButton()
	{
	}
	@Override
	public void buttonPressed(int id)
	{
		switch(id)
		{
			case OFFSET:
				pwToEdit.addOffset();
				return;
			case RETURN:
				bottomMenu.clearChildren();
				pwToEdit = null;
				return;
			case DELETE:
				bottomMenu.clearChildren();
				pwToEdit.setToBeRemoved(true);
				pwToEdit = null;
				return;
		}
		super.buttonPressed(id);
	}
	@Override
	public void addLevelName(Table table)
	{
		Label tutorial = new Label("Use the top left to\nselect a tile and tap to\nplace tiles. Tap and\ndrag to set paths.\nDouble tap to\nremove/edit a tile.", assetHolder.labelStyle);
		assetHolder.correctLabel(tutorial);
		table.add(tutorial).row();
	}

	public void setPathWalker(PathWalker pw)
	{
		pwToEdit = pw;
		Button offset = new TextButton("OFFSET", assetHolder.buttonStyle);
		Button delete = new TextButton("DELETE", assetHolder.buttonStyle);
		Button returnButt = new TextButton("RETURN", assetHolder.buttonStyle);
		offset.addListener(new ButtonListenBridge().setButtonListener(this).setId(OFFSET));
		delete.addListener(new ButtonListenBridge().setButtonListener(this).setId(DELETE));
		returnButt.addListener(new ButtonListenBridge().setButtonListener(this).setId(RETURN));
		bottomMenu.add(offset).size(assetHolder.getButtonWidth(), assetHolder.getButtonHeight()).row().pad(assetHolder.getPadding());
		bottomMenu.add(returnButt).size(assetHolder.getButtonWidth(), assetHolder.getButtonHeight()).row();
		bottomMenu.add(delete).size(assetHolder.getButtonWidth(), assetHolder.getButtonHeight()).row();
	}
	@Override
	public void addRetryButtons(Table table)
	{
	}

	public boolean isPaused()
	{
		if (pwToEdit != null)
		{
			return true;
		}else
			return super.isPaused();
	}
	
	public boolean doMenu()
	{
		boolean successfulOpen = super.doMenu();
		if (successfulOpen)
		{
			Gdx.app.log("cool beans", "beans cool");
			addUploadButton(overlayMenu);
			addNameLevel(overlayMenu);
		}
		return successfulOpen;
	}
	
	public void addNameLevel(Table table)
	{
		TextButton setName = new TextButton("Change Name", assetHolder.buttonStyle);
		setName.addListener(new InputListener(){
			private GameOverlay gOverlay;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				sheep.getMultiplayerMenu().nameLevel();//uploadLevel(assetHolder.levelLoader.toString(sheepGame.getLevelInfo()));
				sheep.gotoMenu("multi");
			}
			public InputListener setSceneChanger(GameOverlay s)
			{
				this.gOverlay = s;
				return this;
			}
		}.setSceneChanger(this));
		table.add(setName).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
	}
	public void addUploadButton(Table table)
	{
		TextButton upload = new TextButton("Upload", assetHolder.buttonStyle);
		upload.addListener(new InputListener(){
			private GameOverlay gOverlay;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				sheep.getTopMenu().uploadLevel(assetHolder.levelLoader.toString(sheepGame.getLevelInfo()));
				//sheep.gotoMenu("multi");
			}
			public InputListener setSceneChanger(GameOverlay s)
			{
				this.gOverlay = s;
				return this;
			}
		}.setSceneChanger(this));
		table.add(upload).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
	}
}
