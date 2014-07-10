package com.mygdx.sheep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class SheepSound
{
	private Music loop;
	public SheepSound()
	{
		loop = Gdx.audio.newMusic(Gdx.files.internal("jazzy2.mp3"));
		loop.setLooping(true);
	}
	public void loopMusic()
	{
		loop.play();
	}
}
