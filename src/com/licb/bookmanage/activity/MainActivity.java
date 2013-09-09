package com.licb.bookmanage.activity;

import com.licb.bookmanage.util.ActivityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 图书管理程序的入口
 * 
 * @author licb
 * 
 */
public class MainActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ActivityUtils.add(this);

		// 四个按钮添加监听事件
		// View addButton = findViewById(R.id.button_add);
		// addButton.setOnClickListener(this);
		//
		// View listButton = findViewById(R.id.button_list);
		// listButton.setOnClickListener(this);
		//
		// View scanButton = findViewById(R.id.button_scan);
		// scanButton.setOnClickListener(this);
		//
		// View configButton = findViewById(R.id.button_config);
		// configButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.button_list:
			// 读取数据库，展示结果
			i = new Intent(this, BookListActivity.class);
			startActivity(i);
			break;
		case R.id.button_add:
			// 手动添加图书
			i = new Intent(this, BookAddActivity.class);
			startActivity(i);
			break;
		case R.id.button_config:
			// 配置选项
			break;
		// case R.id.button_scan:
		// i = new Intent(this, CaptureActivity.class);
		// startActivity(i);
		// break;
		default:
			break;
		}
	}

	/**
	 * 创建配置功能键的菜单项
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// 从配置文件中读取菜单定义并将其转换为实际的视图
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	/**
	 * 功能键菜单中的选项被选中时执行的动作
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:// 关于PBMS
			Intent i = new Intent(this, AboutActivity.class);
			startActivity(i);
			break;
		case R.id.exitPBMS:// 退出应用程序
			// finish();
			// 将所有创建的Activity依次删除
			ActivityUtils.finishProgram();
			break;
		default:
			break;
		}

		return false;
	}

	/**
	 * 主界面按下回退按钮退出应用程序
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	protected void onDestroy() {
		super.onDestroy();
		// 这里是为了保证级联的删除Activity创建的list
		ActivityUtils.remove(this);
	}
}
