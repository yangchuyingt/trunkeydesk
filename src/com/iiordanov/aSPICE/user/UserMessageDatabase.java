package com.iiordanov.aSPICE.user;

import com.iiordanov.aSPICE.Constants;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class UserMessageDatabase extends SQLiteOpenHelper {
	private static String databaseName="userDatabase";
    private UserMessageDatabase db;
    private static final int version=1;
    public UserMessageDatabase(Context context){
    	super(context, databaseName, null, version);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL(DbConstant.createusertable);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public UserMessageDatabase(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	public UserMessageDatabase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

}
