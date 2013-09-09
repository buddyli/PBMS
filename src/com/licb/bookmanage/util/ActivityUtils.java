package com.licb.bookmanage.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

/**
 * 保存所有已经创建的Activity，当系统退出按钮被点击时，遍历这些Activity并且依次将其关闭
 * 
 * @author licb
 * 
 */
public class ActivityUtils {
	private static List<Activity> activityList = new LinkedList<Activity>();

	public static void remove(Activity activity) {
		activityList.remove(activity);
	}

	public static void add(Activity activity) {
		activityList.add(activity);
	}

	public static void finishProgram() {
		for (Activity activity : activityList) {
			activity.finish();
		}

		// 关闭系统进程
		android.os.Process.killProcess(android.os.Process.myPid());

	}
}
