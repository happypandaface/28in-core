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

public class OverlayExtension
{
	protected SheepGame sheepGame;
	protected AssetHolder assetHolder;
	protected sheep sheep;
	protected Stage stage;
	
	public OverlayExtension()
	{
		stage = new Stage();
	}
	
	public void setSheepMain(sheep s)
	{
		sheep = s;
	}
	public void setSheepGame(SheepGame sg)
	{
		sheepGame = sg;
	}
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
	}
	
	public void create()
	{
	}
	
	public void addToInMux(InputMultiplexer inMux)
	{
		inMux.addProcessor(stage);
	}
	
	public void removeFromInMux(InputMultiplexer inMux)
	{
		inMux.removeProcessor(stage);
	}
	
	public void render(float delta)
	{
		stage.act(delta);
		stage.draw();
	}
}