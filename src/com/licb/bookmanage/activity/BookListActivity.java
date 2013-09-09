package com.licb.bookmanage.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.licb.bookmanage.db.BookHelper;
import com.licb.bookmanage.domain.Item;
import com.licb.bookmanage.provider.BookProviderMetaData.BookTableMetaData;
import com.licb.bookmanage.util.ActivityUtils;

/**
 * 图书管理视图，包括图书列表查看、添加、修改和删除等等。
 * 
 * @author licb
 * 
 */
public class BookListActivity extends ListActivity implements
		DialogInterface.OnClickListener {
	private BookHelper bookHelper;
	private static String ORDER_BY = BookTableMetaData.INSERT_TIME + " desc";
	private ListView listView;
	// 删除按钮的标记
	private static final int MENU_DEL_ID = 0;
	// 修改按钮的标记
	private static final int MENU_MODIFY_ID = 1;
	// 游标适配器
	private SimpleCursorAdapter adapter;
	// Spinner下拉状态
	public static final String TYPE_TYPE = "sType";
	public static final String TYPE_BUY = "sBuy";
	public static final String TYPE_READ = "sRead";

	@SuppressWarnings({ "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.books);
		ActivityUtils.add(this);

		final Intent dataIntent = getIntent();
		int sType = dataIntent.getIntExtra(TYPE_TYPE, -1);
		int sBuy = dataIntent.getIntExtra(TYPE_BUY, -1);
		int sRead = dataIntent.getIntExtra(TYPE_READ, -1);

		OnItemSelectedListener listener = new MyOnItemSelectedListener();
		// 填充图书分类下拉框
		Spinner sBookType = (Spinner) findViewById(R.id.select_book_type);
		List<Item> types = new ArrayList<Item>();

		Item item1 = new Item(1, "人文");
		Item item2 = new Item(2, "自然");
		Item item3 = new Item(3, "历史");
		types.add(item1);
		types.add(item2);
		types.add(item3);
		ArrayAdapter<Item> arrAdapter = new ArrayAdapter<Item>(this,
				android.R.layout.simple_spinner_item, types);
		arrAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sBookType.setAdapter(arrAdapter);

		// ArrayAdapter<CharSequence> btAdapter =
		// ArrayAdapter.createFromResource(
		// this, R.array.book_type, android.R.layout.simple_spinner_item);
		// btAdapter
		// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// sBookType.setAdapter(btAdapter);
		// if (sType != -1) {
		// sBookType.setSelection(sType);
		// }
		sBookType.setOnItemSelectedListener(listener);

		// 填充图书阅读状态下拉框
		Spinner sBookRead = (Spinner) findViewById(R.id.select_book_read);
		ArrayAdapter<CharSequence> brAdapter = ArrayAdapter.createFromResource(
				this, R.array.book_read_status,
				android.R.layout.simple_spinner_item);
		brAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sBookRead.setAdapter(brAdapter);
		if (sRead != -1) {
			sBookRead.setSelection(sRead);
		}
		sBookRead.setOnItemSelectedListener(listener);

		// 填充图书购买状态下拉框
		Spinner sBookBuy = (Spinner) findViewById(R.id.select_book_buy);
		ArrayAdapter<CharSequence> buAdapter = ArrayAdapter.createFromResource(
				this, R.array.book_buy_status,
				android.R.layout.simple_spinner_item);
		buAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sBookBuy.setAdapter(buAdapter);
		if (sBuy != -1) {
			sBookBuy.setSelection(sBuy);
		}
		sBookBuy.setOnItemSelectedListener(listener);

		// 三个下拉选项的值
		String strType, strBuy, strRead = null;
		if (sType != -1) {
			sBookType.setSelection(sType);
			strType = getSpinnerItem(R.array.book_type, sType);
		} else {
			// sBookType.setSelection(getSpinnerPosition(R.array.book_type,
			// "不限"));
		}
		if (sBuy != -1) {
			sBookBuy.setSelection(sBuy);
			strBuy = getSpinnerItem(R.array.book_buy_status, sBuy);
		} else {
			sBookBuy.setSelection(getSpinnerPosition(R.array.book_buy_status,
					"不限"));
		}
		if (sRead != -1) {
			sBookRead.setSelection(sRead);
			strRead = getSpinnerItem(R.array.book_read_status, sRead);
		} else {
			sBookRead.setSelection(getSpinnerPosition(R.array.book_read_status,
					"不限"));
		}

		// 创建了一个数据库管理对象，通过这里对象，可以实现数据的增删改查的操作
		bookHelper = new BookHelper(this);
		try {
			Cursor cursor = getContentResolver().query(
					BookTableMetaData.CONTENT_URI, null, null, null, null);
			// 托管游标，游标将根据活动的生命周期动态改变
			startManagingCursor(cursor);
			adapter = new SimpleCursorAdapter(this, R.layout.items, cursor,
					BookTableMetaData.FROM, BookTableMetaData.TO_LIST);
			listView = (ListView) findViewById(android.R.id.list);
			// 设置显示的适配器
			refreshListView(adapter);
			// 初始化LitView中item的菜单项
			initListView();
		} catch (Exception e) {
		} finally {
			bookHelper.close();
		}
	}

	private class MyOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int index,
				long id) {
			int selectedId = arg0.getId();
			switch (selectedId) {
			case R.id.select_book_buy:
				System.out.println("购买Spinner 下标" + index + "被选中");
				break;
			case R.id.select_book_type:
				System.out.println("类别Spinner 下标" + index + "被选中");
				Spinner s = (Spinner) arg0;
				Item item = (Item) s.getItemAtPosition(index);
				System.out.println("=======" + item.getId() + ","
						+ item.getValue());
				break;
			case R.id.select_book_read:
				System.out.println("阅读进度Spinner 下标" + index + "被选中");
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	/**
	 * 单独提取出来这个方法是为了，删除图书后，刷新显示的列表时需要再次设置适配器
	 * 
	 * @param adapter
	 */
	private void refreshListView(ListAdapter adapter) {
		listView.setAdapter(adapter);
	}

	private void initListView() {
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("选择操作");
				menu.add(0, 0, 0, "修改");
				menu.add(0, 1, 1, "删除");
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long itemId) {
			}
		});
	}

	private long itemId;

	@SuppressWarnings({ "unused" })
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// 这个类可以获取到记录的ID和记录在列表中的下标
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		long dbId = info.id;// 数据库中记录ID
		itemId = dbId;
		int position = info.position;// 数据项列表中的下标

		switch (item.getItemId()) {
		case MENU_DEL_ID:// 修改
			Intent i = new Intent(this, BookEditActivity.class);
			i.putExtra("id", dbId);
			startActivity(i);
			return true;
		case MENU_MODIFY_ID:// 删除
			// 提示用户是否真正删除
			confirmDel();

			return true;
		default:
			throw new IllegalArgumentException("Unknown item "
					+ item.getItemId());
		}
	}

	private void confirmDel() {
		new AlertDialog.Builder(this).setTitle("确认删除？")
				.setPositiveButton("确认", this).setNegativeButton("取消", this)
				.create().show();
	}

	// 使用适配器链接视图与资源
	@SuppressWarnings({ "deprecation", "unused" })
	private void listBooks(Cursor cursor) {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.books, cursor, BookTableMetaData.FROM,
				BookTableMetaData.TO_LIST);
		setListAdapter(adapter);
	}

	@SuppressWarnings({ "deprecation", "unused" })
	private Cursor getBooks() {
		SQLiteDatabase db = bookHelper.getReadableDatabase();
		Cursor cursor = db.query(BookTableMetaData.TABLE_BOOK_NAME,
				BookTableMetaData.FROM, null, null, null, null, ORDER_BY);
		// 告诉活动根据活动声明周期管理光标生命周期
		startManagingCursor(cursor);
		return cursor;
	}

	/**
	 * 创建配置功能键的菜单项
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// 从配置文件中读取菜单定义并将其转换为实际的视图
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_list, menu);
		return true;
	}

	/**
	 * 功能键菜单中的选项被选中时执行的动作
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.addBook:// 新增
			i = new Intent(this, BookAddActivity.class);
			break;
		case R.id.backMain:// 返回主界面
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

	/**
	 * 实现这个方法的目的，是因为android中显示对话框是一个异步的过程，如果这里不继承DialogInterface.OnClickListener
	 * ， 那么主线程不会获取到用户选择的操作类型。
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// which下标不能直接引用，直接引用的话会报ID查找不到的错误。只可以用下面的方式判断是哪个按钮被点击
		if (which == DialogInterface.BUTTON_POSITIVE) {
			// 因为确认对话框的原因，真正的删除代码移植到了这里
			if (itemId != 0) {
				Uri deleteUri = ContentUris.withAppendedId(
						BookTableMetaData.CONTENT_URI, itemId);
				int num = getContentResolver().delete(deleteUri, null, null);
				// 重新加载数据
				Cursor cursor = getContentResolver().query(
						BookTableMetaData.CONTENT_URI, null, null, null, null);
				adapter = new SimpleCursorAdapter(this, R.layout.items, cursor,
						BookTableMetaData.FROM, BookTableMetaData.TO_LIST);
				refreshListView(adapter);
			}

		} else {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
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

	/**
	 * 根据下标获取Spinner下拉列表中某个资源的值
	 * 
	 * @param resId
	 * @param item
	 * @return
	 */
	private String getSpinnerItem(int resId, int position) {
		String item = null;
		String[] arr = getResources().getStringArray(resId);
		if (arr != null && arr.length > 0) {
			item = arr[position];
		}
		return item;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityUtils.remove(this);
	}
}
