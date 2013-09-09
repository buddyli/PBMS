package com.licb.bookmanage.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.licb.bookmanage.activity.R;

/**
 * 图书管理系统元数据类
 * 
 * @author licb
 * 
 */
public class BookProviderMetaData {
	// 定义授权，应该使用该字符串在Android描述文件中注册该应用程序
	public static final String AUTHORITY = "com.licb.bookmanage.provider.BookProvider";
	// 数据库的名称和版本
	public static final String DB_NAME = "books.db";
	public static final int DB_VERSION = 1;

	private BookProviderMetaData() {
	}

	// 图书表的相关属性
	public static final class BookTableMetaData implements BaseColumns {
		public static String[] FROM = { BookTableMetaData.NAME,
				BookTableMetaData.FORMATTED_INSERT_TIME,
				BookTableMetaData.AUTHOR, BookTableMetaData.BOOK_TYPE,
				BookTableMetaData.ISBN, BookTableMetaData.INSERT_TIME,
				BookTableMetaData.PAGES, BookTableMetaData.READED,
				BookTableMetaData.BOOK_BUY_STATUS,
				BookTableMetaData.BOOK_READ_STATUS, _ID };
		public static int[] TO_LIST = { R.id.bookName, R.id.bookInsertTime,
				R.id.bookAuthor, R.id.bookType };
		public static int[] TO_EDIT = { R.id.bookName, R.id.bookAuthor,
				R.id.bookIsbn, R.id.bookTotalPages, R.id.bookReaded,
				R.id.spinner_book_type, R.id.spinner_book_buy,
				R.id.spinner_book_read };

		private BookTableMetaData() {
		}

		public static final String TABLE_BOOK_NAME = "books";

		// 定义content provider uri和MIME类型定义
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/books");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.licb.bookmanage.book";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.licb.bookmanage.book";
		// 默认排序方法
		public static final String DEFAULT_SORT_ORDER = "insert_time DESC";

		// 表中的列
		public static final String NAME = "name";
		public static final String ISBN = "isbn";
		public static final String AUTHOR = "author";
		// long from System.currentTimeMillis()
		public static final String INSERT_TIME = "insert_time";
		// int
		public static final String PAGES = "pages";
		// int
		public static final String READED = "readed";
		// string
		public static final String BOOK_TYPE = "type";
		// string
		public static final String BOOK_BUY_STATUS = "buy_status";
		// string
		public static final String BOOK_READ_STATUS = "read_status";
		// string
		public static final String FORMATTED_INSERT_TIME = "format_time";
	}

	// 图书类型的相关属性
	public static class BookTypeTableMetaData implements BaseColumns {
		// 表名称
		public static final String TABLE_BOOK_TYPE_NAME = "book_types";
		// 列名称
		public static final String TYPE_NAME = "type_name";

		public BookTypeTableMetaData() {
		}

		public static final String[] FROM = new String[] {
				BookTypeTableMetaData.TYPE_NAME, BookTypeTableMetaData._ID };

		// 定义content provider uri和MIME类型定义
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/book_types");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.licb.bookmanage.book_type";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.licb.bookmanage.book_type";
		// 默认排序方法
		public static final String DEFAULT_SORT_ORDER = "_ID ASC";
	}

}
