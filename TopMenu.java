package com.mygdx.sheep;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.mygdx.sheep.ui.ButtonListener;
import com.mygdx.sheep.ui.ButtonListenBridge;

public class TopMenu implements ButtonListener
{
	public Stage topStage;
	public Table topRightTable;
	public Table centerTable;
	private static final int MAIN_MENU = 1;
	private static final int CLOSE = 2;
	private static final int PROFILE = 3;
	private static final int LOGIN_BUTTON = 4;
	private InputMultiplexer inMux;
	private AssetHolder assetHolder;
	private sheep sheep;
	private ShapeRenderer shapeRenderer;
	private float animationPercent;
	private float animationSpeed = 3f;
	private boolean inProfileMenu = false;
	private ImageButton profileIcon;
	private ImageButton closeIcon;
	private TextField usernameField;
	private TextField passwordField;
	private TextButton loginButton;
	private float profileIconBigSize;
	private float iconBigPadding;
	private float profileIconSize;
	private float iconPadding;

	public TopMenu()
	{
		inProfileMenu = false;
		animationPercent = 0;
	}

	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
	}
	public void setSheep(sheep s)
	{
		sheep = s;
	}

	public void create()
	{
		inMux = new InputMultiplexer();
		topStage = new Stage();
		shapeRenderer = new ShapeRenderer();
		inMux.addProcessor(topStage);
		topRightTable = new Table();
		centerTable = new Table();
		centerTable.setFillParent(true);
		centerTable.center();
		topRightTable.top();
		topRightTable.right();
		topRightTable.setFillParent(true);
		profileIcon = new ImageButton(assetHolder.profileIcon);
		closeIcon = new ImageButton(assetHolder.closeIcon);
		profileIcon.addListener(new ButtonListenBridge().setButtonListener(this).setId(PROFILE));
		closeIcon.addListener(new ButtonListenBridge().setButtonListener(this).setId(CLOSE));
//		profileIcon.setPosition(90, 190);
		topStage.addActor(topRightTable);
		topStage.addActor(centerTable);
		cornerIcon();
		
		usernameField = new TextField("", assetHolder.textFieldStyle);
		passwordField = new TextField("", assetHolder.textFieldStyle);
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter((char)42);
		
		loginButton = new TextButton("login", assetHolder.buttonStyle);
		loginButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(LOGIN_BUTTON));
	}
	public void updateVals()
	{
		profileIconBigSize = assetHolder.getPercentWidth(.3f);
		iconBigPadding = assetHolder.getPercentWidth(.1f);
		profileIconSize = assetHolder.getPercentWidth(.13f);
		iconPadding = assetHolder.getPercentWidth(.02f);
	}

	public void cornerIcon()
	{
		sheep.restoreInput();
		inProfileMenu = false;
		topRightTable.clearChildren();
		centerTable.clearChildren();
		updateVals();
		//centerTable.add(closeIcon).size(profileIconBigSize, profileIconBigSize).pad(iconBigPadding);
		topRightTable.add(profileIcon).size(profileIconSize, profileIconSize).pad(iconPadding);
	}
	public void centerIcon()
	{
		sheep.onlyTopAndTabs();
		inProfileMenu = true;
		topRightTable.clearChildren();
		centerTable.clearChildren();
		centerTable.add(profileIcon).size(profileIconBigSize, profileIconBigSize).pad(iconBigPadding);
		centerTable.row();
		centerTable.add(usernameField).size(profileIconBigSize, profileIconSize).pad(iconPadding);
		centerTable.row();
		centerTable.add(passwordField).size(profileIconBigSize, profileIconSize).pad(iconPadding);
		centerTable.row();
		centerTable.add(loginButton).size(profileIconBigSize, profileIconSize).pad(iconPadding);
		topRightTable.add(closeIcon).size(profileIconSize, profileIconSize).pad(iconPadding);
	}

	public void buttonPressed(int id)
	{
		Gdx.app.log("id", ""+id);
		switch (id)
		{
			case PROFILE:
				if (!inProfileMenu)
				{
					centerIcon();
				}
				break;
			case CLOSE:
				if (inProfileMenu)
				{
					cornerIcon();
				}
				break;
			case LOGIN_BUTTON:
				if (inProfileMenu)
				{
					cornerIcon();
					sheep.gotoMenu("multi");
					try
					{
						sheep.getMultiplayerMenu().doLogin(MultiplayerMenu.TOP_MENU, usernameField.getText(), NetUtil.encode(passwordField.getText()));
					}catch(Exception e)
					{
						Gdx.app.log("error in top menu", e.toString());
					}
				}
				break;
		}
	}

	public void render()
	{
		if (inProfileMenu)
			animationPercent += Gdx.graphics.getDeltaTime()*animationSpeed;
		else
			animationPercent -= Gdx.graphics.getDeltaTime()*animationSpeed;
		if (animationPercent > 1)
			animationPercent = 1;
		else if (animationPercent < 0)
			animationPercent = 0;
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(assetHolder.getBgColor().r, assetHolder.getBgColor().g, assetHolder.getBgColor().b, animationPercent);
		shapeRenderer.rect(0, 0, assetHolder.getPercentWidth(1f), assetHolder.getPercentHeight(1));
		shapeRenderer.end();
		topStage.act(Gdx.graphics.getDeltaTime());
		topStage.draw();
	}

	public InputProcessor getInput()
	{
		return inMux;
	}
}
