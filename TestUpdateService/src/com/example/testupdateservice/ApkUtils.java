package com.example.testupdateservice;

import java.io.File;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

public class ApkUtils {

	/**
	 * 保存在根目录的文件夹
	 * 
	 * @author xl
	 * @date:2016-3-22上午9:54:24
	 * @description
	 * @return
	 */
	public static synchronized String getApkUrl() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/zhubaoyi/");
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		return new File(Environment.getExternalStorageDirectory()
				+ "/zhubaoyi/zhubaoyi.apk").getPath();

	}

	/**
	 * 安装apk
	 * 
	 * @author xl
	 * @date:2016-3-21下午7:16:57
	 * @description
	 */
	public static Intent getInstallApkIntent() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(getApkUrl())),
				"application/vnd.android.package-archive");
		return intent;
	}

	/**
	 * 安装apk的延时意图
	 * 
	 * @author xl
	 * @date:2016-3-21下午7:17:06
	 * @description
	 * @return
	 */
	public static PendingIntent getInstallPendingIntent(Context context) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(ApkUtils.getApkUrl())),
				"application/vnd.android.package-archive");
		return PendingIntent.getActivity(context, 0, intent, 0);
	}

	/**
	 * file versionCode
	 * 
	 * @author xl
	 * @date:2016-3-22上午10:19:03
	 * @description
	 * @param context
	 * @return
	 */
	public static int getApkVersionCode(Context context) {
		PackageInfo packageInfo = getPackageInfo(context);

		if (packageInfo == null) {
			return 0;
		}
		return packageInfo.versionCode;
	}

	/**
	 * file versionName
	 * 
	 * @author xl
	 * @date:2016-3-22上午10:23:05
	 * @description
	 * @param context
	 * @return
	 */
	public static String getApkVersionName(Context context) {
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo == null) {
			return "";
		} else {
			return packageInfo.versionName;
		}
	}

	public static String getApkPackageName(Context context) {
		PackageInfo packageInfo = getPackageInfo(context);
		if (packageInfo == null) {
			return "";
		} else {
			return packageInfo.packageName;
		}
	}

	/**
	 * 包信息
	 * 
	 * @author xl
	 * @date:2016-3-22上午10:21:50
	 * @description
	 * @param context
	 * @return
	 */
	private static PackageInfo getPackageInfo(Context context) {
		return context.getPackageManager().getPackageArchiveInfo(getApkUrl(),
				PackageManager.GET_ACTIVITIES);
	}
}
