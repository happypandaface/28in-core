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

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

public class TopMenu implements ButtonListener
{
	public Stage topStage;
	public Table topRightTable;
	public Table centerTable;
	public Table bottomTable;
	private static final int MAIN_MENU = 1;
	private static final int CLOSE = 2;
	private static final int PROFILE = 3;
	private static final int LOGIN_BUTTON = 4;
	private static final int MK_ACC = 5;
	private static final int LOGIN_MENU = 6;
	private static final int MAKE_ACCOUNT_BUTTON = 7;
	private static final int MSG = 8;
	private static final int BACK_BUTTON = 9;
	private static final int MULTI_MENU = 10;
	private static final int TYPING = 11;
	private int currentMenu = LOGIN_MENU;
	private int backMenu = LOGIN_MENU;
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
	private TextField repasswordField;
	private Label messageLabel;
	private Label nameLabel;
	private TextButton loginButton;
	private TextButton makeAccountButton;
	private TextButton needAccount;
	private TextButton haveAccount;
	private TextButton backButton;
	private float profileIconBigSize;
	private float iconBigPadding;
	private float profileIconSize;
	private float iconPadding;
	private final String successStr = "success";
	private String currentLogin;
	private String currentPassHash;

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
		bottomTable = new Table();
		bottomTable.setFillParent(true);
		centerTable.setFillParent(true);
		centerTable.center();
		bottomTable.bottom();
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
		topStage.addActor(bottomTable);
		cornerIcon();
		
		usernameField = new TextField("", assetHolder.textFieldStyle);
		usernameField.setRightAligned(false);
		usernameField.addListener(new ButtonListenBridge().setButtonListener(this).setId(TYPING));
		passwordField = new TextField("", assetHolder.textFieldStyle);
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter((char)42);
		repasswordField = new TextField("", assetHolder.textFieldStyle);
		repasswordField.setPasswordMode(true);
		repasswordField.setPasswordCharacter((char)42);
		
		makeAccountButton = new TextButton("CREATE ACCOUNT", assetHolder.buttonStyle);
		makeAccountButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(MAKE_ACCOUNT_BUTTON));
		loginButton = new TextButton("login", assetHolder.buttonStyle);
		loginButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(LOGIN_BUTTON));
		needAccount = new TextButton("NEED AN ACCOUNT?", assetHolder.buttonStyle);
		needAccount.addListener(new ButtonListenBridge().setButtonListener(this).setId(MK_ACC));
		haveAccount = new TextButton("ALREADY HAVE AN ACCOUNT?", assetHolder.buttonStyle);
		haveAccount.addListener(new ButtonListenBridge().setButtonListener(this).setId(LOGIN_MENU));
		backButton = new TextButton("back", assetHolder.buttonStyle);
		backButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(BACK_BUTTON));
		messageLabel = new Label("back", assetHolder.labelStyle);
		nameLabel = new Label("Not logged in", assetHolder.labelStyle);
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
		bottomTable.clearChildren();
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
		bottomTable.clearChildren();
		if (currentMenu == LOGIN_MENU)
		{
			centerTable.add(profileIcon).size(profileIconBigSize, profileIconBigSize).pad(iconBigPadding);
			centerTable.row();
			centerTable.add(usernameField).size(profileIconBigSize, profileIconSize).pad(iconPadding);
			centerTable.row();
			centerTable.add(passwordField).size(profileIconBigSize, profileIconSize).pad(iconPadding);
			centerTable.row();
			centerTable.add(loginButton).size(profileIconBigSize, profileIconSize).pad(iconPadding);
			bottomTable.add(needAccount).size(assetHolder.getPercentWidth(1), profileIconSize);
		}else if (currentMenu == MK_ACC)
		{
			centerTable.add(profileIcon).size(profileIconBigSize, profileIconBigSize).pad(iconBigPadding);
			centerTable.row();
			centerTable.add(usernameField).size(profileIconBigSize, profileIconSize).pad(iconPadding);
			centerTable.row();
			centerTable.add(passwordField).size(profileIconBigSize, profileIconSize).pad(iconPadding).row();
			centerTable.add(repasswordField).size(profileIconBigSize, profileIconSize).pad(iconPadding);
			centerTable.row();
			centerTable.add(makeAccountButton).size(profileIconBigSize, profileIconSize).pad(iconPadding);
			bottomTable.add(haveAccount).size(assetHolder.getPercentWidth(1), profileIconSize);
		}else if (currentMenu == MSG)
		{
			centerTable.add(messageLabel).size(profileIconBigSize, profileIconSize).pad(iconPadding).row();
			centerTable.add(backButton).size(profileIconBigSize, profileIconSize).pad(iconPadding);
		}else if (currentMenu == MULTI_MENU)
		{
			nameLabel.setText("Welcome, "+currentLogin+"!");
			//centerTable.add(levelEditor).size(profileIconBigSize, profileIconSize).pad(iconPadding);
			centerTable.add(nameLabel).size(profileIconBigSize, profileIconSize).pad(iconPadding);
		}
		// always allow close
		topRightTable.add(closeIcon).size(profileIconSize, profileIconSize).pad(iconPadding);
	}

	public void buttonPressed(int id)
	{
		Gdx.app.log("id", ""+id);
		centerTable.setY(assetHolder.getPercentHeight(0f));
		switch (id)
		{
			case TYPING:
				centerTable.setY(assetHolder.getPercentHeight(.3f));
				break;
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
			case MAKE_ACCOUNT_BUTTON:
				if (inProfileMenu)
				{
					createUser();
				}
				break;
			case BACK_BUTTON:
				if (inProfileMenu)
				{
					currentMenu = backMenu;
					centerIcon();
				}
				break;
			case LOGIN_BUTTON:
				if (inProfileMenu)
				{
					cornerIcon();
					sheep.gotoMenu("multi");
					try
					{
						doLogin(LOGIN_MENU, usernameField.getText(), NetUtil.encode(passwordField.getText()));
					}catch(Exception e)
					{
						Gdx.app.log("error in top menu", e.toString());
					}
				}
				break;
			case MK_ACC:
				currentMenu = MK_ACC;
				centerIcon();
				break;
			case LOGIN_MENU:
				currentMenu = LOGIN_MENU;
				centerIcon();
				break;
			case MULTI_MENU:
				currentMenu = MULTI_MENU;
				centerIcon();
				break;
		}
	}
	
	
	public void doLogin(int backMenu, String un, String hash)
	{
		Gdx.app.log("username", un);
		showMessage("Logging in...", backMenu);
		
		NetUtil.sendRequest(NetUtil.LOGIN, "username="+un+"&password="+hash, new HttpResponseListener()
		{
			private String hash;
			private String login;
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				if (str.equals(successStr))
				{
					sheep.setSaved("username", login);
					currentLogin = login;
					currentPassHash = hash;
					showMessage(str, MULTI_MENU, "continue");
				}else
					showMessage(str, LOGIN_MENU);
				Gdx.app.log("login res", str);
				//do stuff here based on response
			}

			public void failed(Throwable t)
			{
				showMessage("Failed:\n"+t.getMessage(), LOGIN_MENU);
				//do stuff here based on the failed attempt
			}

			public void cancelled()
			{
				showMessage("Request cancelled", LOGIN_MENU);
				//do stuff here based on the failed attempt
			}
			public HttpResponseListener setHashAndLogin(String p, String l)
			{
				hash = p;
				login = l;
				return this;
			}
		}.setHashAndLogin(hash, un));
	}
	
	
	public void showMessage(String message, int menu)
	{
		showMessage(message, menu, "back");
	}
	public void showMessage(String message, int menu, String button)
	{
		messageLabel.setText(message);
		backMenu = menu;
		currentMenu = MSG;
		centerIcon();
	}
	
	public void createUser()
	{
		String un = usernameField.getText();
		String pass1 = passwordField.getText();
		String pass2 = repasswordField.getText();
		if (!pass1.equals(pass2))
			showMessage("Passwords don't\nmatch", MK_ACC);
		else
		{
			showMessage("Creating account...", MK_ACC);
			try
			{
				String hash = NetUtil.encode(pass1);
				
				NetUtil.sendRequest(NetUtil.CREATE_ACCOUNT, "username="+un+"&password="+hash, new HttpResponseListener() {
					public void handleHttpResponse(HttpResponse httpResponse) {
						String str = httpResponse.getResultAsString();
						if (str.equals(successStr))
						{
							showMessage(str, LOGIN_MENU, "login");
						}else
							showMessage(str, MK_ACC);
						Gdx.app.log("rtn", httpResponse.getResultAsString());
						//do stuff here based on response
					}

					public void failed(Throwable t) {
						showMessage("Failed:\n"+t.getMessage(), MK_ACC);
						//do stuff here based on the failed attempt
					}

					public void cancelled() {
						showMessage("Request cancelled", MK_ACC);
						//do stuff here based on the failed attempt
					}
				});
			}catch (Exception e)
			{
				showMessage("hash error", MK_ACC);
				Gdx.app.log("error", e.toString());
			}
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
