package com.mygdx.sheep.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ButtonListenBridge extends InputListener
{
	private ButtonListener buttonListener;
	private int id;
	
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
	{
		return true;
	}

	public void touchUp(InputEvent event, float x, float y, int pointer, int button)
	{
		if (!event.isTouchFocusCancel())
			buttonListener.buttonPressed(id);
	}
	public ButtonListenBridge setId(int i)
	{
		id = i;
		return this;
	}
	public ButtonListenBridge setButtonListener(ButtonListener bl)
	{
		buttonListener = bl;
		return this;
	}
}
