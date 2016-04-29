package com.example.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class SoundRecorder {
	public MediaRecorder mRecorder;
	public String path;
	
	public boolean isRecording;

	public void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		path=newFileName();
		mRecorder.setOutputFile(path);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("1111", "prepare() failed");
		}
		mRecorder.start();

	}

	public void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	public String newFileName() {
		String mFileName = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		String s = new SimpleDateFormat("yyyy-MM-dd hhmmss").format(new Date());
		return mFileName += "/rcd_" + s + ".3gp";
	}
	
	
}
