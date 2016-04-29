package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDB extends SQLiteOpenHelper {
	
	public final static String TABLE_NAME="notes";
	public final static String CONTENT="content";
	public static String ID="_id";
	public final static String TITLE="title";
	public final static String TIME="time";
	public final static String RECORDER="recorder";
	public final static String PATH="path";
	public final static String VIDEO="video";
	public final static String CURRENT_TIME="current_time";
	
	

	public NotesDB(Context context) {
		super(context, "notes.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table "+ TABLE_NAME+" ( "
					+ID+" integer primary key autoincrement, "
					+TITLE+" text, "
					+RECORDER+" text, "
					+CONTENT+" text , "
					+PATH+" text , "
					+VIDEO+" text , "
					+CURRENT_TIME+" text , "
					+TIME+" text not null)");
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	

}
