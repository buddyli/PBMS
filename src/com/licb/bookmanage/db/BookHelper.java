package com.licb.bookmanage.db;

import com.licb.bookmanage.provider.BookProviderMetaData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 负责管理数据库图书表的创建与管理
 * 
 * @author licb
 * 
 */
public class BookHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "books.db";
	private static final int DB_VERSION = 1;

	public BookHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table ").append(BookProviderMetaData.BookTableMetaData.TABLE_BOOK_NAME);
		sql.append("(").append(BookProviderMetaData.BookTableMetaData._ID)
				.append(" integer primary key autoincrement").append(",");
		sql.append(BookProviderMetaData.BookTableMetaData.NAME).append(" text").append(",");
		sql.append(BookProviderMetaData.BookTableMetaData.ISBN).append(" text").append(",");
		sql.append(BookProviderMetaData.BookTableMetaData.AUTHOR).append(" text").append(",");
		sql.append(BookProviderMetaData.BookTableMetaData.INSERT_TIME).append(" integer").append(",");
		sql.append(BookProviderMetaData.BookTableMetaData.PAGES).append(" integer").append(",");
		sql.append(BookProviderMetaData.BookTableMetaData.READED).append(" integer");
		sql.append(")");

		db.execSQL(sql.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + BookProviderMetaData.BookTableMetaData.TABLE_BOOK_NAME);
		onCreate(db);
	}

}
