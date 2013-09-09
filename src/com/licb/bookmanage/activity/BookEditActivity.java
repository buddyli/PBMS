package com.licb.bookmanage.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.licb.bookmanage.provider.BookProviderMetaData.BookTableMetaData;
import com.licb.bookmanage.util.ActivityUtils;

public class BookEditActivity extends Activity implements OnClickListener {
	private long dbId;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_edit);
		ActivityUtils.add(this);
		// 填充图书分类下拉框
		Spinner sBookType = (Spinner) findViewById(R.id.spinner_book_type);
		ArrayAdapter<CharSequence> btAdapter = ArrayAdapter.createFromResource(
				this, R.array.book_type, android.R.layout.simple_spinner_item);
		btAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sBookType.setAdapter(btAdapter);
		sBookType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int index, long id) {

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// 填充图书阅读状态下拉框
		Spinner sBookRead = (Spinner) findViewById(R.id.spinner_book_read);
		ArrayAdapter<CharSequence> brAdapter = ArrayAdapter.createFromResource(
				this, R.array.book_read_status,
				android.R.layout.simple_spinner_item);
		brAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sBookRead.setAdapter(brAdapter);
		sBookRead.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int index, long id) {

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// 填充图书购买状态下拉框
		Spinner sBookBuy = (Spinner) findViewById(R.id.spinner_book_buy);
		ArrayAdapter<CharSequence> buAdapter = ArrayAdapter.createFromResource(
				this, R.array.book_buy_status,
				android.R.layout.simple_spinner_item);
		buAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sBookBuy.setAdapter(buAdapter);
		sBookBuy.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int index, long id) {

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		// 需要编辑的图书ID
		dbId = getIntent().getLongExtra("id", 0);
		if (dbId != 0) {
			Cursor cursor = getContentResolver().query(
					BookTableMetaData.CONTENT_URI, null,
					BookTableMetaData._ID + "=?",
					new String[] { String.valueOf(dbId) }, null);
			startManagingCursor(cursor);
			cursor.moveToFirst();
			String name = cursor.getString(cursor
					.getColumnIndex(BookTableMetaData.NAME));
			String author = cursor.getString(cursor
					.getColumnIndex(BookTableMetaData.AUTHOR));
			String isbn = cursor.getString(cursor
					.getColumnIndex(BookTableMetaData.ISBN));
			int readed = cursor.getInt(cursor
					.getColumnIndex(BookTableMetaData.READED));
			int pages = cursor.getInt(cursor
					.getColumnIndex(BookTableMetaData.PAGES));

			EditText nameText = ((EditText) findViewById(R.id.bookName_edit));
			nameText.setText(name);
			((EditText) findViewById(R.id.bookAuthor_edit)).setText(author);
			((EditText) findViewById(R.id.bookIsbn_edit)).setText(isbn);
			((EditText) findViewById(R.id.bookReaded_edit)).setText(String
					.valueOf(readed));
			((EditText) findViewById(R.id.bookTotalPages_edit)).setText(String
					.valueOf(pages));

			int typePosition = getSpinnerPosition(R.array.book_type,
					cursor.getString(cursor
							.getColumnIndex(BookTableMetaData.BOOK_TYPE)));
			int buyPosition = getSpinnerPosition(R.array.book_buy_status,
					cursor.getString(cursor
							.getColumnIndex(BookTableMetaData.BOOK_BUY_STATUS)));
			int readPosition = getSpinnerPosition(
					R.array.book_read_status,
					cursor.getString(cursor
							.getColumnIndex(BookTableMetaData.BOOK_READ_STATUS)));
			if (typePosition != -1) {
				sBookType.setSelection(typePosition);
			}
			if (buyPosition != -1) {
				sBookBuy.setSelection(buyPosition);
			}
			if (readPosition != -1) {
				sBookRead.setSelection(readPosition);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_book:
			updateBook(dbId);
			break;
		case R.id.back_main_from_edit:// 启动扫码界面
			Intent i = new Intent(this, BookListActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
	}

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void updateBook(long dbId) {
		// 分类
		Spinner sbt = (Spinner) findViewById(R.id.spinner_book_type);
		String btStr = processSpinner(sbt.getSelectedItem().toString());
		// 购买状态
		Spinner sbb = (Spinner) findViewById(R.id.spinner_book_buy);
		String bbStr = processSpinner(sbb.getSelectedItem().toString());
		// 阅读状态
		Spinner sbr = (Spinner) findViewById(R.id.spinner_book_read);
		String brStr = (sbr.getSelectedItem().toString());

		String name = ((EditText) findViewById(R.id.bookName_edit)).getText()
				.toString();
		String author = ((EditText) findViewById(R.id.bookAuthor_edit))
				.getText().toString();
		// isbn不用更新
		// String isbn = ((EditText) findViewById(R.id.bookIsbn)).getText()
		// .toString();
		// 插入时间没有更新
		// long time = System.currentTimeMillis();
		String pages = ((EditText) findViewById(R.id.bookTotalPages_edit))
				.getText().toString();
		String readed = ((EditText) findViewById(R.id.bookReaded_edit))
				.getText().toString();

		ContentValues values = new ContentValues();
		values.put(BookTableMetaData.NAME, name);
		values.put(BookTableMetaData.AUTHOR, author);
		// values.put(BookTableMetaData.ISBN, isbn);
		// values.put(BookTableMetaData.INSERT_TIME, time);
		values.put(BookTableMetaData.PAGES, pages);
		values.put(BookTableMetaData.READED, readed);
		values.put(BookTableMetaData.BOOK_TYPE, btStr);
		values.put(BookTableMetaData.BOOK_READ_STATUS, brStr);
		values.put(BookTableMetaData.BOOK_BUY_STATUS, bbStr);
		String fDate = sdf.format(Calendar.getInstance().getTime());
		values.put(BookTableMetaData.FORMATTED_INSERT_TIME, fDate);

		getContentResolver().update(BookTableMetaData.CONTENT_URI, values,
				BookTableMetaData._ID + "=?",
				new String[] { String.valueOf(dbId) });
		Toast.makeText(this, "提示：修改成功", Toast.LENGTH_SHORT).show();
	}

	private String processSpinner(String text) {
		if (text.startsWith("不限"))
			return "";
		else
			return text;
	}

	/**
	 * 获取Spinner下拉列表中某个资源的下标
	 * 
	 * @param resId
	 * @param item
	 * @return
	 */
	private int getSpinnerPosition(int resId, String item) {
		int index = -1;
		String[] arr = getResources().getStringArray(resId);
		int position = 0;
		if (arr != null && arr.length > 0) {
			for (String str : arr) {
				if (item.equals(str)) {
					index = position;
					break;
				}
				position++;
			}
		}
		return index;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityUtils.remove(this);
	}

	/**
	 * 创建配置功能键的菜单项
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// 从配置文件中读取菜单定义并将其转换为实际的视图
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_edit, menu);
		return true;
	}

	/**
	 * 功能键菜单中的选项被选中时执行的动作
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.list_book_from_edit:// 新增
			i = new Intent(this, BookListActivity.class);
			break;
		case R.id.back_main_from_edit:// 返回主界面
			i = new Intent(this, MainActivity.class);
			break;
		default:
			break;
		}
		if (i != null) {
			startActivity(i);
		}
		return false;
	}
}
