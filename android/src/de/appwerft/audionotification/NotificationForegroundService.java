package de.appwerft.audionotification;

import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.*;
import android.util.Log;

public class NotificationForegroundService extends Service {
	private static final String PACKAGE_NAME = TiApplication.getInstance().getPackageName();
	static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
	private static final String LCAT = TiaudionotificationModule.LCAT + "_Service";
	public static final String EXTRA_ACTION = "MYACTION";
	private final Context ctx;
	private IBinder binder;
	private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";

	private boolean changingConfiguration;
	private NotificationManager notificationManager;
	private KrollDict notificationOpts = new KrollDict();

	public NotificationForegroundService() {
		super();
		ctx = TiApplication.getInstance().getApplicationContext();
		notificationOpts.put("title", "Title");
		notificationOpts.put("subtitle", "SubTitle");

	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LCAT, "getSystemService inside onCreate");
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Android O requires a Notification Channel.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Create the channel for the notification
			NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION.CHANNELID,
					TiApplication.getInstance().getPackageName(), NotificationManager.IMPORTANCE_MAX);
			notificationChannel.setDescription("HörDat");
			notificationManager.createNotificationChannel(notificationChannel);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	

	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(LCAT, "onStartCommand");
		HashMap<String, Object> notificationOpts = (HashMap<String, Object>)intent.getSerializableExtra("DICT");
		getNotification();
		return START_STICKY;
	}

		
	// https://willowtreeapps.com/ideas/mobile-notifications-part-2-some-useful-android-notifications
	/**
	 * Returns the {@link NotificationCompat} used as part of the foreground
	 * service.
	 */
	private Notification getNotification() {
			// The activityIntent calls the app
		Intent activityIntent = new Intent(Intent.ACTION_MAIN);
		//activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, activityIntent, 0);
		
		final String packageName = TiApplication.getInstance().getPackageName();
		activityIntent.setComponent(new ComponentName(packageName,
				packageName + "." + TiApplication.getAppRootOrCurrentActivity().getLocalClassName()));
		Log.d(LCAT, "intents ready, try build NotificationCompat.Builder\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		// Building notification:
		final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				ctx/*
					 * , Constants.NOTIFICATION.CHANNELID
					 */);
		notificationBuilder //
				.setAutoCancel(true)
				.setSmallIcon(R("applogo", "drawable"))//
				.setDefaults(Notification.DEFAULT_ALL)
				.setPriority(Notification.PRIORITY_HIGH) //
				.setWhen(System.currentTimeMillis()).setOngoing(true)
				.setContentTitle(notificationOpts.getString(TiC.PROPERTY_TITLE))
				.setContentText(notificationOpts.getString(TiC.PROPERTY_SUBTITLE))
				.setContentIntent(pendingIntent);
		// Set the Channel ID for Android O.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// buildersetChannel(Constants.NOTIFICATION.CHANNELID);
			Log.d(LCAT, "setChannelId to " + Constants.NOTIFICATION.CHANNELID);
			notificationBuilder.setChannelId(Constants.NOTIFICATION.CHANNELID); // Channel ID
		}
		
		Notification notification = notificationBuilder.build();
		//notificationManager.notify(Constants.NOTIFICATION.ID, notification);
		startForeground(Constants.NOTIFICATION.ID, notification);
		return notification;
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.NOTIFICATION.FOREGROUND_SERVICE:
				KrollDict opts = (KrollDict) msg.obj;
				updateNotification(opts);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}


	private int R(String name, String type) {
		int id = 0;
		try {
			id = this.getResources().getIdentifier(name, type, this.getPackageName());
		} catch (Exception e) {
			return id;
		}
		return id;
	}
}