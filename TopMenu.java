package com.mygdx.sheep;

import com.badlogic.gdx.scenes.scene2d.utils.Align;
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
	private static final int GOTO_EDIT = 12;
	private static final int LEVEL_EDIT = 13;
	private static final int CHOICE = 14;
	private static final int CONT_UPLOAD = 15;
	private static final int NO_BUTTON = 16;
	private static final int YES_BUTTON = 17;
	private static final int LOGOUT_BUTTON = 18;
	private static final int MY_LEVELS= 19;
	private static final int GOTO_CHANGE_PASS = 20;

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
	private TextButton yesButton;
	private TextButton noButton;
	private Label nameLabel;
	private TextButton loginButton;
	private TextButton logoutButton;
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
	private Table topLeftTable;
	private Button editButton;
	private boolean showing = true;
	private boolean inMenu = false;
	private Button myLevels;
	private Button changePass;
	private boolean badVersion = false;

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
	public String getLoginInfo(String cmd)
	{
		cmd += "username="+currentLogin+"&password="+currentPassHash;
		return cmd;
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
		topLeftTable = new Table();
		topLeftTable.setFillParent(true);
		topLeftTable.top();
		topLeftTable.left();
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
		topStage.addActor(topLeftTable);
		cornerIcon();
	
		editButton = new TextButton("new level", assetHolder.buttonStyle);
		editButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(GOTO_EDIT));
		myLevels = new TextButton("my levels", assetHolder.buttonStyle);
		myLevels.addListener(new ButtonListenBridge().setButtonListener(this).setId(MY_LEVELS));
		changePass= new TextButton("change password", assetHolder.buttonStyle);
		changePass.addListener(new ButtonListenBridge().setButtonListener(this).setId(GOTO_CHANGE_PASS));

		usernameField = new TextField("", assetHolder.textFieldStyle);
		usernameField.setRightAligned(false);
		usernameField.addListener(new ButtonListenBridge().setButtonListener(this).setId(TYPING));
		passwordField = new TextField("", assetHolder.textFieldStyle);
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter((char)42);
		passwordField.addListener(new ButtonListenBridge().setButtonListener(this).setId(TYPING));
		repasswordField = new TextField("", assetHolder.textFieldStyle);
		repasswordField.addListener(new ButtonListenBridge().setButtonListener(this).setId(TYPING));
		repasswordField.setPasswordMode(true);
		repasswordField.setPasswordCharacter((char)42);
		
		makeAccountButton = new TextButton("CREATE ACCOUNT", assetHolder.buttonStyle);
		makeAccountButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(MAKE_ACCOUNT_BUTTON));
		loginButton = new TextButton("login", assetHolder.buttonStyle);
		loginButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(LOGIN_BUTTON));
		needAccount = new TextButton("NEED AN ACCOUNT?", assetHolder.newButtonStyle);
		needAccount.addListener(new ButtonListenBridge().setButtonListener(this).setId(MK_ACC));
		haveAccount = new TextButton("HAVE AN ACCOUNT?", assetHolder.newButtonStyle);
		haveAccount.addListener(new ButtonListenBridge().setButtonListener(this).setId(LOGIN_MENU));
		backButton = new TextButton("back", assetHolder.buttonStyle);
		backButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(BACK_BUTTON));
		messageLabel = new Label("back", assetHolder.labelStyle);
		messageLabel.setAlignment(Align.bottom, Align.center);
		yesButton = new TextButton("back", assetHolder.buttonStyle);
		yesButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(YES_BUTTON));
		noButton = new TextButton("back", assetHolder.buttonStyle);
		noButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(NO_BUTTON));
		logoutButton = new TextButton("log out", assetHolder.buttonStyle);
		logoutButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(LOGOUT_BUTTON));
		nameLabel = new Label("Not logged in", assetHolder.labelStyle);
		getVersion();
		String currentLogin = sheep.getSaved("username", "");
		String currentPassHash = sheep.getSaved("passhash", "");
		if (!currentLogin.equals("") && !currentPassHash.equals(""))
		{
			doLogin(LOGIN_MENU, currentLogin, currentPassHash);
		}
	}
	public void updateVals()
	{
		profileIconBigSize = assetHolder.getPercentWidth(.3f);
		iconBigPadding = assetHolder.getPercentWidth(.1f);
		profileIconSize = assetHolder.getPercentWidth(.13f);
		iconPadding = assetHolder.getPercentWidth(.02f);
	}

	public void hide()
	{
		inMux.clear();
		inMux.removeProcessor(topStage);
		showing = false;
	}
	public void show()
	{
		inMux.clear();
		inMux.addProcessor(topStage);
		showing = true;
	}

	private String levelToUpload;
	public void uploadLevel(String mul)
	{
		levelToUpload = mul;
		showConfirm("This will overwrite\nany level of the\nsame name and it's\nratings", CONT_UPLOAD, LEVEL_EDIT, "continue", "back");
	}
	public void doUpload()
	{
		showMessage("Uploading...", LEVEL_EDIT);
		String commandString = new String();
		commandString = getLoginInfo(commandString);
		//commandString += "username="+currentLogin+"&password="+currentPassHash;
		commandString += "&levelString="+levelToUpload+"&levelName="+sheep.getMultiplayerMenu().getLevelName();
		Gdx.app.log("commandString", commandString);
		NetUtil.sendRequest(NetUtil.SAVE_LEVEL, commandString, new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				showMessage(str, LEVEL_EDIT, "continue");
				Gdx.app.log("savlvlrtn", str);
				//do stuff here based on response
			}

			public void failed(Throwable t) {
				showMessage("Failed:\n"+t.getMessage(), LEVEL_EDIT);
				//do stuff here based on the failed attempt
			}

			public void cancelled() {
				showMessage("Request cancelled", LEVEL_EDIT);
				//do stuff here based on the failed attempt
			}
		});
		Gdx.app.log("level string: ", levelToUpload);
	}


	public void cornerIcon()
	{
		sheep.restoreInput();
		inProfileMenu = false;
		topRightTable.clearChildren();
		topLeftTable.clearChildren();
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
		topLeftTable.clearChildren();
		centerTable.clearChildren();
		bottomTable.clearChildren();
		if (badVersion)
		{
			Label upLabel = new Label("PLEASE UPDATE YOUR APP", assetHolder.labelStyle);
			assetHolder.correctLabel(upLabel);
			centerTable.add(upLabel).size(profileIconBigSize, profileIconBigSize).pad(iconBigPadding).row();
		}
		if (currentMenu == LOGIN_MENU)
		{
			centerTable.add(profileIcon).size(profileIconBigSize, profileIconBigSize).pad(iconBigPadding);
			centerTable.row();
			centerTable.add(new Label("username:", assetHolder.labelStyle)).size(assetHolder.getButtonWidth(), assetHolder.getButtonHeight()).row();
			centerTable.add(usernameField).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding);
			centerTable.row();
			centerTable.add(new Label("password:", assetHolder.labelStyle)).size(assetHolder.getButtonWidth(), assetHolder.getButtonHeight()).row();
			centerTable.add(passwordField).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding);
			centerTable.row();
			centerTable.add(loginButton).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding);
			bottomTable.add(needAccount).size(assetHolder.getPercentWidth(1), profileIconSize);
		}else if (currentMenu == MK_ACC)
		{
			centerTable.add(profileIcon).size(profileIconBigSize, profileIconBigSize).pad(iconBigPadding);
			centerTable.row();
			centerTable.add(new Label("username:", assetHolder.labelStyle)).size(assetHolder.getButtonWidth(), assetHolder.getButtonHeight()).row();
			centerTable.add(usernameField).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding);
			centerTable.row();
			centerTable.add(new Label("password:", assetHolder.labelStyle)).size(assetHolder.getButtonWidth(), assetHolder.getButtonHeight()).row();
			centerTable.add(passwordField).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding).row();
			centerTable.add(new Label("confirm:", assetHolder.labelStyle)).size(assetHolder.getButtonWidth(), assetHolder.getButtonHeight()).row();
			centerTable.add(repasswordField).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding);
			centerTable.row();
			centerTable.add(makeAccountButton).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding);
			bottomTable.add(haveAccount).size(assetHolder.getPercentWidth(1), profileIconSize);
		}else if (currentMenu == MSG)
		{
			centerTable.add(messageLabel).size(profileIconBigSize, profileIconSize).pad(iconPadding).row();
			centerTable.add(backButton).size(profileIconBigSize, profileIconSize).pad(iconPadding);
		}else if (currentMenu == MULTI_MENU)
		{
			assetHolder.correctLabel(nameLabel);
			nameLabel.setText("Welcome, "+currentLogin+"!");
			//centerTable.add(levelEditor).size(profileIconBigSize, profileIconSize).pad(iconPadding);
			centerTable.add(nameLabel).size(profileIconBigSize, profileIconSize).pad(iconPadding).row();
			centerTable.add(editButton).size(assetHolder.getButtonWidth(), profileIconSize).row();
			centerTable.add(myLevels).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding).row();
			centerTable.add(changePass).size(assetHolder.getButtonWidth(), profileIconSize).pad(iconPadding).row();
			centerTable.add(logoutButton).size(profileIconBigSize, profileIconSize).pad(iconPadding).row();
		}else if (currentMenu == CHOICE)
		{
			centerTable.add(messageLabel).size(profileIconBigSize, profileIconSize).pad(iconPadding).row();
			centerTable.add(yesButton).size(profileIconBigSize, profileIconSize).pad(iconPadding).row();
			centerTable.add(noButton).size(profileIconBigSize, profileIconSize).pad(iconPadding).row();
			
		}
		// always allow close
		topRightTable.add(closeIcon).size(profileIconSize, profileIconSize).pad(iconPadding);
	}
	public String getUsername()
	{
		return currentLogin;
	}

	public void buttonPressed(int id)
	{
		Gdx.app.log("id", ""+id);
		centerTable.setY(assetHolder.getPercentHeight(0f));
		// test
		switch (id)
		{
			case GOTO_CHANGE_PASS:
				cornerIcon();
				sheep.getMultiplayerMenu().changePass();
				sheep.gotoMenu("multi");
				break;
			case MY_LEVELS:
				sheep.getMultiplayerMenu().loadLevels(0, NetUtil.USER);
				cornerIcon();
				sheep.gotoMenu("multi");
				break;
			case LOGOUT_BUTTON:
				currentLogin = "";
				currentPassHash = "";
				sheep.setSaved("username", "");
				sheep.setSaved("passhash", "");
				currentMenu = LOGIN_MENU;
				centerIcon();
				break;
			case NO_BUTTON:
				if (noMenu == LEVEL_EDIT)
				{
					cornerIcon();
					sheep.gotoMenu("levelEdit");
				}
				break;
			case YES_BUTTON:
				if (yesMenu == CONT_UPLOAD)
				{
					doUpload();
				}
				break;
			case LEVEL_EDIT:
				cornerIcon();
				sheep.gotoMenu("levelEdit");
				break;
			case GOTO_EDIT:
				sheep.getMultiplayerMenu().editNewLevel();
				cornerIcon();
				break;
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
				if (backMenu == LEVEL_EDIT)
				{
					currentMenu = MAIN_MENU;
					backMenu = MAIN_MENU;
					centerIcon();
					cornerIcon();
					sheep.gotoMenu("levelEdit");
				}else
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
					//sheep.gotoMenu("multi");
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
	public void rateLevel(String name, String user, float num)
	{
		String cmd = "username=\""+currentLogin+"\"&password="+currentPassHash+"&levelCreator="+user+"&levelName=\""+name+"\"&rating="+(int)num;
		Gdx.app.log("rate cmd", cmd);
		NetUtil.sendRequest(NetUtil.RATE_LEVEL, cmd, new HttpResponseListener()
		{
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				Gdx.app.log("rate res", str);
				if (str.equals(successStr))
				{
					showMessage(str, LOGIN_MENU);
				}else
					showMessage(str, LOGIN_MENU);
				//do stuff here based on response
			}
			public void failed(Throwable t)
			{
				String str = t.getMessage();
				Gdx.app.log("rate fail", str);
				showMessage("Failed:\n"+str, LOGIN_MENU);
				//do stuff here based on the failed attempt
			}

			public void cancelled()
			{
				Gdx.app.log("rate cancel", "too bad!");
				showMessage("Request cancelled", LOGIN_MENU);
				//do stuff here based on the failed attempt
			}
		});
	}
	
	public void getVersion()
	{
		NetUtil.sendRequest(NetUtil.VERSION, "", new HttpResponseListener()
		{
			private String hash;
			private String login;
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				Gdx.app.log("version", str);
				if (str.equals("1"))
				{
					badVersion = false;
					
				}else
				{
					badVersion = true;
				}
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
		});
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
					sheep.setSaved("passhash", hash);
					currentLogin = login;
					currentPassHash = hash;
					gotoMenu(MULTI_MENU);
				//	showMessage(str, MULTI_MENU, "continue");
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
	
	private int yesMenu;
	private int noMenu;
	public void showConfirm(String message, int yesMenu, int noMenu, String yesText, String noText)
	{
		messageLabel.setText(message);
		this.yesMenu = yesMenu;
		this.noMenu = noMenu;
		yesButton.setText(yesText);
		noButton.setText(noText);
		currentMenu = CHOICE;
		centerIcon();
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
		if (inProfileMenu)
			centerIcon();
	}
	public void gotoMenu(int menu)
	{
		currentMenu = menu;
		if (inProfileMenu)
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
						Gdx.app.log("mk acc rtn", httpResponse.getResultAsString());
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
		if (showing)
		{
			Gdx.gl.glEnable(GL20.GL_BLEND);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(assetHolder.getBgColor().r, assetHolder.getBgColor().g, assetHolder.getBgColor().b, animationPercent);
			shapeRenderer.rect(0, 0, assetHolder.getPercentWidth(1f), assetHolder.getPercentHeight(1));
			shapeRenderer.end();
			topStage.act(Gdx.graphics.getDeltaTime());
			topStage.draw();
		}
	}

	public InputProcessor getInput()
	{
		return inMux;
	}
}
