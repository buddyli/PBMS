package com.licb.bookmanage.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
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

import com.licb.bookmanage.domain.Book;
import com.licb.bookmanage.provider.BookProviderMetaData.BookTableMetaData;
import com.licb.bookmanage.util.ActivityUtils;
import com.licb.bookmanage.util.BookInfofromDouban;

/**
 * 图书添加活动
 * 
 * @author licb
 * 
 */
public class BookAddActivity extends Activity implements OnClickListener {
	// private BookHelper bookHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_add);
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
		// 如果是从扫码界面转过来的，如果扫码成功，这个值会传过来
		// String isbn = getIntent().getStringExtra("isbn");
		// if (isbn != null) {
		// // TODO
		// // 参考《基础教程》第117页，在主线程外新建一个线程，用来请求接口处理数据。防止主线程因为处理时间过长导致的用户界面响应失败
		// Book book = BookInfofromDouban.getBookInfofromDouban(isbn);
		// if (book != null) {
		// EditText bookName = (EditText) findViewById(R.id.bookName);
		// ((EditText) findViewById(R.id.bookIsbn))
		// .setText(book.getIsbn());
		// bookName.setText(book.getName());
		// // 以下三行代码设置书名输入框多行显示，而且不左右滑动书名
		// bookName.setGravity(Gravity.TOP);
		// bookName.setSingleLine(false);
		// bookName.setHorizontallyScrolling(false);
		// ((EditText) findViewById(R.id.bookAuthor)).setText(book
		// .getAuthor());
		// ((EditText) findViewById(R.id.bookTotalPages)).setText(String
		// .valueOf(book.getPages()));
		// } else {
		// ((EditText) findViewById(R.id.bookIsbn)).setText(isbn);
		// Toast.makeText(this, "提示：ISBN: " + isbn + " 的图书信息没有找到，请手动录入！",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
	}

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void addNewBook() {
		// 分类
		Spinner sbt = (Spinner) findViewById(R.id.spinner_book_type);
		String btStr = processSpinner(sbt.getSelectedItem().toString());
		// 购买状态
		Spinner sbb = (Spinner) findViewById(R.id.spinner_book_buy);
		String bbStr = processSpinner(sbb.getSelectedItem().toString());
		// 阅读状态
		Spinner sbr = (Spinner) findViewById(R.id.spinner_book_read);
		String brStr = (sbr.getSelectedItem().toString());

		String name = ((EditText) findViewById(R.id.bookName)).getText()
				.toString();
		String author = ((EditText) findViewById(R.id.bookAuthor)).getText()
				.toString();
		String isbn = ((EditText) findViewById(R.id.bookIsbn)).getText()
				.toString();
		long time = System.currentTimeMillis();
		String pages = ((EditText) findViewById(R.id.bookTotalPages)).getText()
				.toString();
		String readed = ((EditText) findViewById(R.id.bookReaded)).getText()
				.toString();

		ContentValues values = new ContentValues();
		values.put(BookTableMetaData.NAME, name);
		values.put(BookTableMetaData.AUTHOR, author);
		values.put(BookTableMetaData.ISBN, isbn);
		values.put(BookTableMetaData.INSERT_TIME, time);
		values.put(BookTableMetaData.PAGES, pages);
		values.put(BookTableMetaData.READED, readed);
		values.put(BookTableMetaData.BOOK_TYPE, btStr);
		values.put(BookTableMetaData.BOOK_READ_STATUS, brStr);
		values.put(BookTableMetaData.BOOK_BUY_STATUS, bbStr);
		String fDate = sdf.format(Calendar.getInstance().getTime());
		values.put(BookTableMetaData.FORMATTED_INSERT_TIME, fDate);

		getContentResolver().insert(BookTableMetaData.CONTENT_URI, values);
		Toast.makeText(this, "提示：添加成功" + "," + fDate, Toast.LENGTH_SHORT)
				.show();
	}

	private String processSpinner(String text) {
		if (text.startsWith("不限"))
			return "";
		else
			return text;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addBook:
			// 添加之前，先检查图书是否存在，如果已经存在，那么不再允许用户添加。
			String isbn = ((EditText) findViewById(R.id.bookIsbn)).getText()
					.toString();
			if (isBookExist(isbn)) {
				Toast.makeText(this, "提示：ISBN: " + isbn + " 图书已经录入，请勿重复录入！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 如果图书没有添加过，那么可以新增
			addNewBook();
			// 添加成功后，提示用户进行下一步操作
			operationAfterSuccessAddDialog();
			break;
		case R.id.scanBook:// 启动扫码界面
			Intent i = new Intent(this, CaptureActivity.class);
			// startActivity(i);
			startActivityForResult(i, 1);
			break;
		default:
			System.out.println("不能理解按的什么键，丢弃");
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			String isbn = data.getStringExtra("isbn");
			fillAddWindowByIsbn(isbn);
			break;
		default:
			break;
		}
	}

	private void fillAddWindowByIsbn(String isbn) {
		if (isbn != null) {
			// TODO
			// 参考《基础教程》第117页，在主线程外新建一个线程，用来请求接口处理数据。防止主线程因为处理时间过长导致的用户界面响应失败
			Book book = BookInfofromDouban.getBookInfofromDouban(isbn);
			if (book != null) {
				EditText bookName = (EditText) findViewById(R.id.bookName);
				((EditText) findViewById(R.id.bookIsbn))
						.setText(book.getIsbn());
				bookName.setText(book.getName());
				// 以下三行代码设置书名输入框多行显示，而且不左右滑动书名
				bookName.setGravity(Gravity.TOP);
				bookName.setSingleLine(false);
				bookName.setHorizontallyScrolling(false);
				((EditText) findViewById(R.id.bookAuthor)).setText(book
						.getAuthor());
				((EditText) findViewById(R.id.bookTotalPages)).setText(String
						.valueOf(book.getPages()));
			} else {
				((EditText) findViewById(R.id.bookIsbn)).setText(isbn);
				Toast.makeText(this, "提示：ISBN: " + isbn + " 的图书信息没有找到，请手动录入！",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 判断图书是不是已经存在
	 * 
	 * @param isbn
	 * @return
	 */
	private boolean isBookExist(String isbn) {
		Cursor cursor = getContentResolver().query(
				BookTableMetaData.CONTENT_URI, null, "isbn=?",
				new String[] { isbn }, null);
		return cursor != null && cursor.getCount() > 0;
	}

	/**
	 * 图书添加成功后的提示操作类型对话框
	 */
	private void operationAfterSuccessAddDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.operation_title)
				.setItems(R.array.operation,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = null;
								switch (which) {
								case 0:// 加载图书列表
									i = new Intent(BookAddActivity.this,
											BookListActivity.class);
									break;
								// case 1:// 继续扫码添加图书
								// i = new Intent(BookAddActivity.this,
								// CaptureActivity.class);
								// break;
								case 1:// 继续添加图书
									i = new Intent(BookAddActivity.this,
											BookAddActivity.class);
									break;
								default:
									throw new IllegalArgumentException(
											"Unknown opertion type " + which);
								}
								startActivity(i);
							}
						}).create().show();
	}

	/**
	 * 防止添加完了图书，按回退键的时候，重新回到了扫码界面。
	 * 
	 * 但是这样会有一个ｂｕｇ，如果从列表页面进入的添加页面，按回退键时，同样会进入主界面
	 * 
	 * 知道这方法的作用就可以了（可以用来临时保存持久话的数据）。这里的实现不是解决之道
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// Intent i = new Intent(this, MainActivity.class);
		// startActivity(i);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 创建配置功能键的菜单项
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// 从配置文件中读取菜单定义并将其转换为实际的视图
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_add, menu);
		return true;
	}

	/**
	 * 功能键菜单中的选项被选中时执行的动作
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.list_book_from_add:// 新增
			i = new Intent(this, BookListActivity.class);
			break;
		case R.id.back_main_from_add:// 返回主界面
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityUtils.remove(this);
	}
}
