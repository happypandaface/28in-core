package com.mygdx.sheep;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

import com.mygdx.sheep.ui.ButtonListener;
import com.mygdx.sheep.ui.ButtonListenBridge;

/*

*/
public class MultiplayerMenu implements ButtonListener 
{
	private Stage stage;
	private AssetHolder assetHolder;
	private SheepGame sheepGame;
	private sheep sheep;
	private Table loginTable;
	private Table bottomTable;
	private Table levelsTable;
	private TextButton newUser;
	private TextButton loginButton;
	private TextButton loginButton2;
	private TextButton back;
	private TextButton createButton;
	private TextButton backButton;
	private TextButton logOutButton;
	private TextButton levelEditor;
	private TextButton playRandom;
	private TextButton myLevels;
	private TextButton changeLevelName;
	private TextButton changePassButton;
	private TextButton gotoChangePass;
	private TextButton highestRated;
	private TextButton newest;
	private Label msgLable;
	private Label nameLabelLogin;
	private Label passLabelLogin;
	private Label newPassLabelLogin;
	private Label passwordLabelLogin;
	private Label multiplayerTitle;
	private TextField loginUN;
	private TextField oldPass;
	private TextField loginPW;
	private TextField createPWconfirm;
	private TextField levelName;
	private static int CREATE_MENU = 1;
	private static int LOGIN_MENU = 2;
	private static int MULTI_MENU = 3;
	private static int LEVEL_EDIT = 4;
	private static int NAME_LEVEL = 5;
	private static int CHANGE_PASS_MENU = 5;
	public static int TOP_MENU = 6;
	public final static int POP_TAB = 7;
	public final static int NEW_TAB = 8;
	public final static int MY_LEVELS = 9;
	private int currBackMenu;
	private String currentLogin = "";
	private String currentPassHash = "";
	private String successStr = "success";
	private Array<MenuLevelInfo> levels;
	private int levelsPerPage = 4;
	private final static int CHANGE_PASS_MENU_BUTTON = 1;
	private final static int CHANGE_PASS = 2;
	private Table popTogNew;
	private boolean showingPop;
	
	public void setSheepMain(sheep s)
	{
		sheep = s;
	}
	public void setGame(SheepGame sg)
	{
		sheepGame = sg;
	}
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
	}
	
	public void load()
	{
		stage = new Stage();
		levels = new Array<MenuLevelInfo>();
		
		bottomTable = new Table();
		levelsTable = new Table();
		levelsTable.center();
		bottomTable.setFillParent(true);
		bottomTable.bottom();
		
		loginTable = new Table();
		loginTable.setFillParent(true);
		loginTable.center();
		
		msgLable = new Label("", assetHolder.labelStyle);
		msgLable.setAlignment(Align.bottom, Align.center);
		
		nameLabelLogin = new Label("username:", assetHolder.labelStyle);
		loginUN = new TextField("", assetHolder.textFieldStyle);
		String un = sheep.getSaved("username", "");
		loginUN.setText(un);
		
		multiplayerTitle = new Label("MULTIPLAYER", assetHolder.labelStyle);
		highestRated = new TextButton("POPULAR", assetHolder.smallButtonStyle);
		highestRated.addListener(new ButtonListenBridge().setButtonListener(this).setId(POP_TAB));
		newest = new TextButton("NEWEST", assetHolder.smallButtonStyle);
		popTogNew = new Table();

		newest.addListener(new ButtonListenBridge().setButtonListener(this).setId(NEW_TAB));

		passLabelLogin = new Label("password:", assetHolder.labelStyle);
		newPassLabelLogin = new Label("new password:", assetHolder.labelStyle);
		levelName = new TextField("", assetHolder.textFieldStyle);
		loginPW = new TextField("", assetHolder.textFieldStyle);
		loginPW.setPasswordMode(true);
		loginPW.setPasswordCharacter((char)42);
		oldPass = new TextField("", assetHolder.textFieldStyle);
		oldPass.setPasswordMode(true);
		oldPass.setPasswordCharacter((char)42);
		passwordLabelLogin = new Label("confirm pass:", assetHolder.labelStyle);
		createPWconfirm = new TextField("", assetHolder.textFieldStyle);
		createPWconfirm.setPasswordMode(true);
		createPWconfirm.setPasswordCharacter((char)42);
		
		logOutButton = new TextButton("log out", assetHolder.buttonStyle);
		logOutButton.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				logOut();
			}
		});
		myLevels = new TextButton("my levels", assetHolder.buttonStyle);
		myLevels.addListener(new ButtonListenBridge().setButtonListener(this).setId(MY_LEVELS));
		
		changeLevelName = new TextButton("Set Name", assetHolder.buttonStyle);
		changeLevelName.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				sheep.gotoMenu("levelEdit");
			}
		});
		levelEditor = new TextButton("level editor", assetHolder.buttonStyle);
		levelEditor.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				editNewLevel();
			}
		});
		playRandom = new TextButton("play random", assetHolder.buttonStyle);
		playRandom.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				playRandom();
			}
		});
		back = new TextButton("Back", assetHolder.buttonStyle);
		back.addListener(new InputListener(){
			private MultiplayerMenu mm;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				mm.goBack();
			}
			public InputListener setSceneChanger(MultiplayerMenu s)
			{
				this.mm = s;
				return this;
			}
		}.setSceneChanger(this));
		changePassButton = new TextButton("Change Password", assetHolder.buttonStyle);
		changePassButton.addListener(new ButtonListenBridge().setButtonListener(this).setId(CHANGE_PASS));
		gotoChangePass = new TextButton("Change Password", assetHolder.buttonStyle);
		gotoChangePass.addListener(new ButtonListenBridge().setButtonListener(this).setId(CHANGE_PASS_MENU_BUTTON));
		
		newUser = new TextButton("New User", assetHolder.buttonStyle);
		newUser.addListener(new InputListener(){
			private MultiplayerMenu mm;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				mm.newUser();
			}
			public InputListener setSceneChanger(MultiplayerMenu s)
			{
				this.mm = s;
				return this;
			}
		}.setSceneChanger(this));
		loginButton = new TextButton("Login", assetHolder.buttonStyle);
		loginButton.addListener(new InputListener(){
			private MultiplayerMenu mm;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				mm.loginScreen();
			}
			public InputListener setSceneChanger(MultiplayerMenu s)
			{
				this.mm = s;
				return this;
			}
		}.setSceneChanger(this));
		loginButton2 = new TextButton("Login", assetHolder.buttonStyle);
		loginButton2.addListener(new InputListener(){
			private MultiplayerMenu mm;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				mm.doLogin();
			}
			public InputListener setSceneChanger(MultiplayerMenu s)
			{
				this.mm = s;
				return this;
			}
		}.setSceneChanger(this));
		createButton = new TextButton("Create", assetHolder.buttonStyle);
		createButton.addListener(new InputListener(){
			private MultiplayerMenu mm;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				mm.createUser();
			}
			public InputListener setSceneChanger(MultiplayerMenu s)
			{
				this.mm = s;
				return this;
			}
		}.setSceneChanger(this));
		backButton = new TextButton("Back", assetHolder.buttonStyle);
		backButton.addListener(new InputListener(){
			private MultiplayerMenu mm;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//Gdx.input.setOnscreenKeyboardVisible(true);
				mm.goBackInternal();
			}
			public InputListener setSceneChanger(MultiplayerMenu s)
			{
				this.mm = s;
				return this;
			}
		}.setSceneChanger(this));
		
		stage.addActor(loginTable);
		stage.addActor(bottomTable);
		
		
		mainLogin();
		buttonPressed(POP_TAB);
	}
	public void buttonPressed(int id)
	{
		switch (id)
		{
			case CHANGE_PASS:
				Gdx.app.log("change pass", loginPW.getText());
				String pass1 = loginPW.getText();
				String pass2 = createPWconfirm.getText();
				if (pass1.equals(pass2))
					doChangePassword(pass1);
				else
					showMessage("Passwords do not\nmatch", CHANGE_PASS_MENU);
				break;
			case CHANGE_PASS_MENU_BUTTON:
				changePass();
				break;
			case POP_TAB:
				popTogNew.clearChildren();
				popTogNew.add(new Image(assetHolder.popTogNew1));
				loadLevels(0, NetUtil.GET_POP);
				break;
			case NEW_TAB:
				popTogNew.clearChildren();
				popTogNew.add(new Image(assetHolder.popTogNew2));
				loadLevels(0, NetUtil.GET_NEW);
				break;
			case MY_LEVELS:
				getOnMyLevels();
				break;
		}
	}
	public void loadLevels(int page, int type)
	{
		// load the highest rated levels and display them
		// in a table
		levelsTable.clearChildren();
		levelsTable.add(new Label("Loading...", assetHolder.labelStyle));
		NetUtil.sendRequest(type, "", new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				Json json = new Json();
				json.addClassTag(assetHolder.mliClassName, MenuLevelInfo.class);
				String[] mlis = str.split("\n");
				levels.clear();
				for (int i = 0; i < mlis.length; ++i)
				{
					String lvlStr = mlis[i];
					if (!lvlStr.equals(""))
					{
						MenuLevelInfo mli = json.fromJson(MenuLevelInfo.class, lvlStr);
						levels.add(mli);
					}
				}
				
				makeLevelButtons(0);
				Gdx.app.log("rtn pop", str);
				//do stuff here based on response
			}

			public void failed(Throwable t) {
				String str = t.getMessage();
				showMessage("Failed:\n"+str, CREATE_MENU);
				Gdx.app.log("failed", str);
				//do stuff here based on the failed attempt
			}

			public void cancelled() {
				showMessage("Request cancelled", CREATE_MENU);
				//do stuff here based on the failed attempt
			}
		});
	}
	public void loadNew(int page)
	{
		// load the highest rated levels and display them
		// in a table
		levelsTable.clearChildren();
		levelsTable.add(new Label("Loading...", assetHolder.labelStyle));
	}
	public void doChangePassword(String pass)
	{
		try
		{
			showMessage("Changing...", CHANGE_PASS_MENU);
			String commandString = new String();
			commandString = getLoginInfo(commandString);
			commandString += "&newPassword="+NetUtil.encode(pass);
			//commandString += "username="+currentLogin+"&password="+currentPassHash;
			Gdx.app.log("commandString", commandString);
			NetUtil.sendRequest(NetUtil.CHANGE_PASS, commandString, new HttpResponseListener() {
				public void handleHttpResponse(HttpResponse httpResponse) {
					String str = httpResponse.getResultAsString();
					showMessage(str, MULTI_MENU, "continue");
					Gdx.app.log("rtn", str);
					//do stuff here based on response
				}

				public void failed(Throwable t) {
					showMessage("Failed:\n"+t.getMessage(), CHANGE_PASS_MENU);
					//do stuff here based on the failed attempt
				}

				public void cancelled() {
					showMessage("Request cancelled", CHANGE_PASS_MENU);
					//do stuff here based on the failed attempt
				}
			});
		}catch (Exception e)
		{
			showMessage("Problem: encryption", CHANGE_PASS_MENU);
		}
	}
	
	public void logOut()
	{
		currentLogin = "";
		currentPassHash = "";
		mainLogin();
	}
	
	public void goBackInternal()
	{
		if (currBackMenu == CREATE_MENU)
		{
			newUser();
		}else
		if (currBackMenu == LOGIN_MENU)
		{
			loginScreen();
		}else
		if (currBackMenu == MULTI_MENU)
		{
			mainLogin();
		}else
		if (currBackMenu == LEVEL_EDIT)
		{
			mainLogin();
			sheep.gotoMenu("levelEdit");
		}else
		if (currBackMenu == NAME_LEVEL)
		{
			nameLevel();
		}else
		if (currBackMenu == CHANGE_PASS_MENU)
		{
			changePass();
		}else
		if (currBackMenu == TOP_MENU)
		{
			loginScreen();
		}
	}
	
	public void nameLevel()
	{
		clearTables();
		loginTable.top();
		loginTable.add(levelName).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		loginTable.row();
		loginTable.add(changeLevelName).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		loginTable.row();
	}
	
	public String getLoginInfo(String cmd)
	{
		cmd += "username="+currentLogin+"&password="+currentPassHash;
		return cmd;
	}
	public void uploadLevel(String mul)
	{
		showMessage("Uploading...", LEVEL_EDIT);
		String commandString = new String();
		commandString = getLoginInfo(commandString);
		//commandString += "username="+currentLogin+"&password="+currentPassHash;
		commandString += "&levelString="+mul+"&levelName="+levelName.getText();
		Gdx.app.log("commandString", commandString);
		NetUtil.sendRequest(NetUtil.SAVE_LEVEL, commandString, new HttpResponseListener() {
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				showMessage(str, LEVEL_EDIT, "continue");
				Gdx.app.log("rtn", str);
				//do stuff here based on response
			}

			public void failed(Throwable t) {
				showMessage("Failed:\n"+t.getMessage(), CREATE_MENU);
				//do stuff here based on the failed attempt
			}

			public void cancelled() {
				showMessage("Request cancelled", CREATE_MENU);
				//do stuff here based on the failed attempt
			}
		});
		Gdx.app.log("level string: ", mul);
	}
	
	public void getOnMyLevels()// meant to write "all" not "on"
	{
		showMessage("Getting levels...", MULTI_MENU);
		
		NetUtil.sendRequest(NetUtil.GET_USR_LVL, "username="+currentLogin, new HttpResponseListener()
		{
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				Gdx.app.log("usrlvls res", str);
				//String[] parts = str.split(",");
				clearLevels();
				Json json = new Json();
				json.addClassTag("levelInfo", MenuLevelInfo.class);
				Array<MenuLevelInfo> lvlArr = json.fromJson(Array.class, str);
				levels.clear();
				for (int i = 0; i < lvlArr.size; ++i)
				{
					MenuLevelInfo mli = lvlArr.get(i);

					levels.add(mli);
//					String[] levelParts = parts[i].split(":");
					//mli.username = 
				}
//				setLevels(parts);
				makeLevelButtons(0);
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
	public void clearLevels()
	{
		levels.clear();
		
	}
	public void setLevels(Array<MenuLevelInfo> levels)
	{
		this.levels = levels;
	}
	
	// this shows the level buttons
	// should be evolved to work for other people's levels
	// as it currently only shows your own levels
	public void makeLevelButtons(int page)
	{
		clearTables();
		loginTable.center();
		mainLogin();
		for (int i = 0; i < levels.size; ++i)
		{
			MenuLevelInfo mli = levels.get(i);
			String levelName = mli.levelName;
			String username = mli.username;
//			String rating = mli.rating;
			//String username = levelNames[i];
			float padding = 0;//assetHolder.getPercentHeight(0.02f);
			TextButton lvl1 = new TextButton(levelName/*"Level "+(i+1)*/, assetHolder.newButtonStyle);
			loginTable.add(lvl1).height(assetHolder.getPercentHeight(assetHolder.buttonHeight)).width(assetHolder.getPercentWidth(assetHolder.buttonWidth)).pad(padding);
			loginTable.row();
			lvl1.addListener(new InputListener(){
				private String levelName;
				private String username;
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
				{
					return true;
				}
				public void touchUp(InputEvent event, float x, float y, int pointer, int button)
				{
					doLevel(this.username, this.levelName);
				}
				public InputListener setLevelInfo(String un, String s)
				{
					this.username = un;
					this.levelName = s;
					return this;
				}
			}.setLevelInfo(username, levelName));
		}
	}
	
	public void doLevel(String userName, String levelName)
	{
		showMessage("Loading level...", MULTI_MENU);
		String command = "username="+userName+"&levelName=\""+levelName+"\"";
		Gdx.app.log("command", command);
		NetUtil.sendRequest(NetUtil.GET_LEVEL, command, new HttpResponseListener()
		{
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				Gdx.app.log("getlvl res", str);

				assetHolder.levelLoader.loadFromString(sheep.getPuzzleMode(), str);
				sheep.gotoMenu("game");
				goBackInternal();
				//do stuff here based on response
			}

			public void failed(Throwable t)
			{
				String str = t.getMessage();
				showMessage("Failed:\n"+str, LOGIN_MENU);
				Gdx.app.log("Failed", str);
				//do stuff here based on the failed attempt
			}

			public void cancelled()
			{
				showMessage("Request cancelled", LOGIN_MENU);
				//do stuff here based on the failed attempt
			}
		});

	}
	
	
	public void playRandom()
	{
		showMessage("Loading random\nlevel...", MULTI_MENU);
		NetUtil.sendRequest(NetUtil.PLAY_RAND, "", new HttpResponseListener()
		{
			public void handleHttpResponse(HttpResponse httpResponse) {
				String str = httpResponse.getResultAsString();
				assetHolder.levelLoader.loadFromString(sheep.getPuzzleMode(), str);
				sheep.gotoMenu("game");
				goBackInternal();
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
		});
	}
	
	public void editNewLevel()
	{
		levelName.setText("level name");
		sheep.getLevelEditor().reset();
		sheep.gotoMenu("levelEdit");
	}
	
	public void mainLogin()
	{
		clearTables();
		levelsTable.clearChildren();
		loginTable.center();
		loginTable.add(multiplayerTitle).row();
		// these are the popular/new buttons
		Table twoButtons = new Table();
		twoButtons.add(highestRated).size(assetHolder.getSmallButtonWidth(), assetHolder.getSmallButtonHeight());
		twoButtons.add(newest).size(assetHolder.getSmallButtonWidth(), assetHolder.getSmallButtonHeight());
		loginTable.add(popTogNew).size(assetHolder.getPercentWidth(.65f), assetHolder.getPercentHeight(.05f)).row();

		//loginTable.add(twoButtons).row();
		loginTable.add(levelsTable).row();
		/*
		if (currentLogin.equals("") &&
			currentPassHash.equals(""))
		{
			loginTable.add(back).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
			loginTable.add(loginButton).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
			loginTable.add(newUser).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
		}else
		{
			loginTable.add(back).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
			loginTable.add(logOutButton).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
			loginTable.add(levelEditor).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
			loginTable.add(playRandom).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
			loginTable.add(myLevels).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
			loginTable.add(gotoChangePass).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			loginTable.row();
		}
		*/
	}
	
	public void doLogin()
	{
		try
		{
			String un = loginUN.getText();
			String pass1 = loginPW.getText();
			String hash = NetUtil.encode(pass1);
			doLogin(CREATE_MENU, un, hash);
		}catch (Exception e)
		{
			showMessage("hash error", CREATE_MENU);
			Gdx.app.log("error", e.toString());
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
	
	public void createUser()
	{
		String un = loginUN.getText();
		String pass1 = loginPW.getText();
		String pass2 = createPWconfirm.getText();
		if (!pass1.equals(pass2))
			showMessage("Passwords don't\nmatch", CREATE_MENU);
		else
		{
			showMessage("Creating account...", CREATE_MENU);
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
							showMessage(str, CREATE_MENU);
						Gdx.app.log("rtn", httpResponse.getResultAsString());
						//do stuff here based on response
					}

					public void failed(Throwable t) {
						showMessage("Failed:\n"+t.getMessage(), CREATE_MENU);
						//do stuff here based on the failed attempt
					}

					public void cancelled() {
						showMessage("Request cancelled", CREATE_MENU);
						//do stuff here based on the failed attempt
					}
				});
			}catch (Exception e)
			{
				showMessage("hash error", CREATE_MENU);
				Gdx.app.log("error", e.toString());
			}
		}
	}
	
	public void clearPasswords()
	{
		loginPW.setText("");
		createPWconfirm.setText("");
	}
	
	public void loginScreen()
	{
		currBackMenu = MULTI_MENU;
		clearTables();
		loginTable.top();
		clearPasswords();
		loginTable.add(nameLabelLogin).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(loginUN).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(passLabelLogin).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(loginPW).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(loginButton2).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(backButton).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
	}
	
	public void newUser()
	{
		currBackMenu = MULTI_MENU;
		clearTables();
		loginTable.top();
		clearPasswords();
		loginTable.add(nameLabelLogin).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(loginUN).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(passLabelLogin).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(loginPW).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(passwordLabelLogin).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(createPWconfirm).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(createButton).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(backButton).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
	}
	public void changePass()
	{
		currBackMenu = MULTI_MENU;
		clearTables();
		loginTable.top();
		clearPasswords();
		loginTable.add(passLabelLogin).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(oldPass).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(newPassLabelLogin).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(loginPW).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(passwordLabelLogin).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(createPWconfirm).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(changePassButton).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(backButton).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
	}
	public void showMessage(String msg, int menu, String buttonStr)
	{
		currBackMenu = menu;
		clearTables();
		loginTable.center();
		msgLable.setText(msg);
		backButton.setText(buttonStr);
		loginTable.add(msgLable).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		loginTable.add(backButton).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(1);
		loginTable.row();
		Gdx.input.setOnscreenKeyboardVisible(false);
	}
	public void showMessage(String msg, int menu)
	{
		showMessage(msg, menu, "back");
	}
	
	public void goBack()
	{
		sheep.gotoMenu("main");
	}
	
	public void render()
	{
		Color bgColor = assetHolder.getBgColor();
		Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	public void switchTo()
	{
	//	Gdx.input.setInputProcessor(stage);
	}
	public InputProcessor getInput()
	{
		return stage;
	}
	
	public void clearTables()
	{
		loginTable.clear();
		bottomTable.clear();
	}
	
	
	// this handles all events on buttons here
	// ...NAUGHT
	/*
	public boolean handle(Event event)
	{
		if (event instanceof InputEvent)
		{
			InputEvent ie = ((InputEvent)event);
			if (ie.getType() == InputEvent.Type.touchUp)
			{
				
				Gdx.app.log("whoop", "yeyeah"+ie.getPointer());
				if (ie.getRelatedActor() == newUser)
					Gdx.app.log("whoop", "yeyeah");
				if (ie.getTarget() == newUser)
					Gdx.app.log("whoop", "yeyeah");
			}
		}
		return true;
	}*/
}
