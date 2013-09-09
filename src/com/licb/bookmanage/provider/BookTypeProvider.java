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

import com.licb.bookmanage.provider.BookProviderMetaData.BookTypeTableMetaData;

/**
 * 图书表的Provider。提供基本的增删改查的操作
 * 
 * @author licb
 * 
 */
public class BookTypeProvider extends ContentProvider {
	// 设置列投影映射，类似于sql中的as关键字，可以给列起别名
	private static HashMap<String, String> sBooksProjectionMap;
	static {
		sBooksProjectionMap = new HashMap<String, String>();
		sBooksProjectionMap.put(BookTypeTableMetaData._ID,
				BookTypeTableMetaData._ID);
		sBooksProjectionMap.put(BookTypeTableMetaData.TYPE_NAME,
				BookTypeTableMetaData.TYPE_NAME);
	}

	// 设置URI匹配样式
	private static final UriMatcher sUriMatcher;
	private static final int INCOMING_BOOK_TYPE_COLLECTION_URI_INDICATOR = 1;
	private static final int INCOMING_SINGLE_TYPE_BOOK_URI_INDICATOR = 2;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "book_types",
				INCOMING_BOOK_TYPE_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "book_types/#",
				INCOMING_SINGLE_TYPE_BOOK_URI_INDICATOR);
	}

	private DatabaseHelper mOpenHelper;

	// 数据库创建和修改辅助内部类
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {// 启动时创建数据库
			super(context, BookProviderMetaData.DB_NAME, null,
					BookProviderMetaData.DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuffer sql = new StringBuffer();
			sql.append("create table ").append(
					BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME);
			sql.append("(").append(BookTypeTableMetaData._ID)
					.append(" integer primary key autoincrement").append(",");
			sql.append(BookTypeTableMetaData.TYPE_NAME).append(" text");
			sql.append(");");
			db.execSQL(sql.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "
					+ BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count = 0;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case INCOMING_BOOK_TYPE_COLLECTION_URI_INDICATOR:
			count = db.delete(BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME,
					where, whereArgs);
			break;
		case INCOMING_SINGLE_TYPE_BOOK_URI_INDICATOR:
			String id = uri.getPathSegments().get(1);
			count = db.delete(
					BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME,
					BookTypeTableMetaData._ID
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
		case INCOMING_BOOK_TYPE_COLLECTION_URI_INDICATOR:
			return BookTypeTableMetaData.CONTENT_TYPE;
		case INCOMING_SINGLE_TYPE_BOOK_URI_INDICATOR:
			return BookTypeTableMetaData.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (sUriMatcher.match(uri) != INCOMING_BOOK_TYPE_COLLECTION_URI_INDICATOR) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME,
				BookTypeTableMetaData.TYPE_NAME, values);
		if (rowId > 0) {
			Uri insertedBookUri = ContentUris.withAppendedId(
					BookTypeTableMetaData.CONTENT_URI, rowId);

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
		case INCOMING_BOOK_TYPE_COLLECTION_URI_INDICATOR:
			qb.setTables(BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME);
			qb.setProjectionMap(sBooksProjectionMap);
			break;
		case INCOMING_SINGLE_TYPE_BOOK_URI_INDICATOR:
			qb.setTables(BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME);
			qb.setProjectionMap(sBooksProjectionMap);
			// 从请求的URI中获取到图书的ID
			qb.appendWhere(BookTypeTableMetaData._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = BookTypeTableMetaData.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		// 这个方法书的第83页写的第一个参数应该是错误的。
		Cursor c = db.query(BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME,
				projection, selection, selectionArgs, null, null, orderBy);
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
		case INCOMING_BOOK_TYPE_COLLECTION_URI_INDICATOR:
			count = db.update(BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME,
					values, selection, selectionArgs);
			break;
		case INCOMING_SINGLE_TYPE_BOOK_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			// 拼装了一个where查询子句
			count = db.update(BookTypeTableMetaData.TABLE_BOOK_TYPE_NAME,
					values, BookTypeTableMetaData._ID
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
