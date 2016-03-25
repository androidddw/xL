package com.example.testupdateservice;

import java.io.File;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.Cancelable;
import org.xutils.http.RequestParams;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * 更新下载apk服务
 * 
 * @author xl
 * @date:2016-3-21下午4:44:54
 */
public class ZBYUpdateService extends Service {

	private Context mContext;
	/** 通知栏唯一标识 */
	private static final int ZBY_NOTIFICATION = 10008;
	/** 通知栏对象 */
	private Notification mNotification;
	/** 通知栏管理器 */
	private NotificationManager mNotificationManager;

	/** 延时意图 */
	private PendingIntent mPendingIntent;
	private static final String url = "http://www.zhubaoyi.cn/links/download/apk";

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		creatNotification();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPendingIntent = null;
		mNotification = null;
	}

	/**
	 * 创建通知
	 * 
	 * @author xl
	 * @date:2016-3-21下午4:47:38
	 * @description
	 */
	private void creatNotification() {
		Intent intent = new Intent(mContext, MainActivity.class);
		mPendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

		mNotification = new Notification.Builder(mContext).getNotification();
		mNotification.icon = R.drawable.ic_launcher;
		mNotification.tickerText = "珠宝易开始下载...";// 顶部小提示

		/** 通知栏管理器 */
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.notify(ZBY_NOTIFICATION, mNotification);

		RequestParams params = new RequestParams(url);
		params.setSaveFilePath(ApkUtils.getApkUrl());

		/** 刷新间隔1000毫秒(默认的是300) */
		params.setLoadingUpdateMaxTimeSpan(1000);
		Cancelable cancelable = x.http().get(params,
				new Callback.ProgressCallback<File>() {
					@Override
					public void onCancelled(CancelledException arg0) {
					}

					@Override
					public void onError(Throwable arg0, boolean arg1) {
					}

					@Override
					public void onFinished() {

						Log.e("XL", ApkUtils.getApkVersionCode(mContext)
								+ "..." + ApkUtils.getApkVersionName(mContext));
						stopSelf();
					}

					@Override
					public void onSuccess(File arg0) {
						Builder builder = new Notification.Builder(mContext);
						builder.setContentTitle("已下载完成apk文件");// 通知栏标题
						builder.setContentText("已下载" + "100%");// 通知栏文本
						builder.setSmallIcon(R.drawable.ic_launcher);
						mPendingIntent = ApkUtils
								.getInstallPendingIntent(mContext);
						builder.setContentIntent(mPendingIntent);
						mNotificationManager.notify(ZBY_NOTIFICATION,
								builder.getNotification());

						startActivity(ApkUtils.getInstallApkIntent());
					}

					@Override
					public void onLoading(long total, long current,
							boolean isDownloading) {
						if (isDownloading) {
							Builder builder = new Notification.Builder(mContext);
							builder.setContentTitle("正在下载apk文件	");
							builder.setContentText("已下载" + current * 100
									/ total + "%");
							builder.setSmallIcon(R.drawable.ic_launcher);
							mNotificationManager.notify(ZBY_NOTIFICATION,
									builder.getNotification());
						}
					}

					@Override
					public void onStarted() {

					}

					@Override
					public void onWaiting() {

					}
				});
	}
}
