package com.licb.bookmanage.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.licb.bookmanage.provider.BookProviderMetaData.BookTableMetaData;

/**
 * 图书表的Provider。提供基本的增删改查的操作
 * 
 * @author licb
 * 
 */
public class BookProvider extends ContentProvider {
	// 设置列投影映射，类似于sql中的as关键字，可以给列起别名
	private static HashMap<String, String> sBooksProjectionMap;
	static {
		sBooksProjectionMap = new HashMap<String, String>();
		sBooksProjectionMap.put(BookTableMetaData._ID, BookTableMetaData._ID);
		sBooksProjectionMap.put(BookTableMetaData.NAME, BookTableMetaData.NAME);
		sBooksProjectionMap.put(BookTableMetaData.AUTHOR,
				BookTableMetaData.AUTHOR);
		sBooksProjectionMap.put(BookTableMetaData.ISBN, BookTableMetaData.ISBN);
		sBooksProjectionMap.put(BookTableMetaData.INSERT_TIME,
				BookTableMetaData.INSERT_TIME);
		sBooksProjectionMap.put(BookTableMetaData.READED,
				BookTableMetaData.READED);
		sBooksProjectionMap.put(BookTableMetaData.PAGES,
				BookTableMetaData.PAGES);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_TYPE,
				BookTableMetaData.BOOK_TYPE);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_BUY_STATUS,
				BookTableMetaData.BOOK_BUY_STATUS);
		sBooksProjectionMap.put(BookTableMetaData.BOOK_READ_STATUS,
				BookTableMetaData.BOOK_READ_STATUS);
		sBooksProjectionMap.put(BookTableMetaData.FORMATTED_INSERT_TIME,
				BookTableMetaData.FORMATTED_INSERT_TIME);
	}

	// 设置URI匹配样式
	private static final UriMatcher sUriMatcher;
	private static final int INCOMING_BOOK_COLLECTION_URI_INDICATOR = 1;
	private static final int INCOMING_SINGLE_BOOK_URI_INDICATOR = 2;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books",
				INCOMING_BOOK_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books/#",
				INCOMING_SINGLE_BOOK_URI_INDICATOR);
	}

	private DatabaseHelper mOpenHelper;

	// 数据库创建和修改辅助内部类
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, BookProviderMetaData.DB_NAME, null,
					BookProviderMetaData.DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuffer sql = new StringBuffer();
			sql.append("create table ").append(
					BookTableMetaData.TABLE_BOOK_NAME);
			sql.append("(").append(BookTableMetaData._ID)
					.append(" integer primary key autoincrement").append(",");
			sql.append(BookTableMetaData.NAME).append(" text").append(",");
			sql.append(BookTableMetaData.ISBN).append(" text").append(",");
			sql.append(BookTableMetaData.AUTHOR).append(" text").append(",");
			sql.append(BookTableMetaData.INSERT_TIME).append(" integer")
					.append(",");
			sql.append(BookTableMetaData.PAGES).append(" integer").append(",");
			sql.append(BookTableMetaData.READED).append(" integer").append(",");
			sql.append(BookTableMetaData.FORMATTED_INSERT_TIME).append(" text")
					.append(",");
			sql.append(BookTableMetaData.BOOK_TYPE).append(" text").append(",");
			sql.append(BookTableMetaData.BOOK_BUY_STATUS).append(" text")
					.append(",");
			sql.append(BookTableMetaData.BOOK_READ_STATUS).append(" text");
			sql.append(")");
			db.execSQL(sql.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "
					+ BookTableMetaData.TABLE_BOOK_NAME);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count = 0;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
			count = db.delete(BookTableMetaData.TABLE_BOOK_NAME, where,
					whereArgs);
			break;
		case INCOMING_SINGLE_BOOK_URI_INDICATOR:
			String id = uri.getPathSegments().get(1);
			count = db.delete(
					BookTableMetaData.TABLE_BOOK_NAME,
					BookTableMetaData._ID
							+ "="
							+ id
							+ (TextUtils.isEmpty(where) ? "" : (" and ("
									+ where + ") ")), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// 。。。notify的用法请参阅第12章。。。
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	// 返回给定URI的MIME类型
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
			return BookTableMetaData.CONTENT_TYPE;
		case INCOMING_SINGLE_BOOK_URI_INDICATOR:
			return BookTableMetaData.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (sUriMatcher.match(uri) != INCOMING_BOOK_COLLECTION_URI_INDICATOR) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		Long now = Long.valueOf(System.currentTimeMillis());
		// 对用户可能不会输入字段的冗余处理
		if (values.containsKey(BookTableMetaData.INSERT_TIME) == false) {
			values.put(BookTableMetaData.INSERT_TIME, now);
		}
		if (values.containsKey(BookTableMetaData.PAGES) == false) {
			values.put(BookTableMetaData.PAGES, -1);
		}
		if (values.containsKey(BookTableMetaData.READED) == false) {
			values.put(BookTableMetaData.READED, -1);
		}
		if (values.containsKey(BookTableMetaData.ISBN) == false) {
			values.put(BookTableMetaData.ISBN, "Unknown ISBN");
		}
		if (values.containsKey(BookTableMetaData.AUTHOR) == false) {
			values.put(BookTableMetaData.AUTHOR, "Unknown AUTHOR");
		}
		if (values.containsKey(BookTableMetaData.NAME) == false) {
			throw new IllegalArgumentException("please input a valid bookname "
					+ uri);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(BookTableMetaData.TABLE_BOOK_NAME,
				BookTableMetaData.NAME, values);
		if (rowId > 0) {
			Uri insertedBookUri = ContentUris.withAppendedId(
					BookTableMetaData.CONTENT_URI, rowId);

			// 。。。notify的用法请参阅第12章。。。
			getContext().getContentResolver().notifyChange(insertedBookUri,
					null);
			return insertedBookUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		// 千万不要忘了这里的实例化操作
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	// 使用SQLiteQueryBuilder对象来建立和执行查询
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
			qb.setTables(BookTableMetaData.TABLE_BOOK_NAME);
			qb.setProjectionMap(sBooksProjectionMap);
			break;
		case INCOMING_SINGLE_BOOK_URI_INDICATOR:
			qb.setTables(BookTableMetaData.TABLE_BOOK_NAME);
			qb.setProjectionMap(sBooksProjectionMap);
			// 从请求的URI中获取到图书的ID
			qb.appendWhere(BookTableMetaData._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = BookTableMetaData.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		// 这个方法书的第83页写的第一个参数应该是错误的。
		Cursor c = db.query(BookTableMetaData.TABLE_BOOK_NAME, projection,
				selection, selectionArgs, null, null, orderBy);
		// 返回的记录条数
		// int i = c.getCount();

		// Tell the cursor what uri to match,so it knows when its source data
		// changes
		// 。。。notify的用法请参阅第12章。。。
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
			count = db.update(BookTableMetaData.TABLE_BOOK_NAME, values,
					selection, selectionArgs);
			break;
		case INCOMING_SINGLE_BOOK_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			// 拼装了一个where查询子句
			count = db.update(
					BookTableMetaData.TABLE_BOOK_NAME,
					values,
					BookTableMetaData._ID
							+ "="
							+ rowId
							+ ((TextUtils.isEmpty(selection)) ? "" : (" and ("
									+ selection + ")")), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		// notify将在第12张详解
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
