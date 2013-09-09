package com.licb.bookmanage.activity;

import com.licb.bookmanage.util.ActivityUtils;

import android.app.Activity;
import android.os.Bundle;

/**
 * 关于对话框。这里没有使用Dialog,而是使用了一个用户Theme.DialogTheme的活动来展示关于的内容。
 * 
 * @author licb
 * 
 */
public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		ActivityUtils.add(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityUtils.remove(this);
	}
}
