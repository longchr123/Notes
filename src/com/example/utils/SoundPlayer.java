package com.example.utils;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;

public class SoundPlayer {
	public MediaPlayer mPlayer;

	public void startPlaying(String fileName) {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(fileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e("22222", "prepare() failed");
		}
	}

	public void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}
}