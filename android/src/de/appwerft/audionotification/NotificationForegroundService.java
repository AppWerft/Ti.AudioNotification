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
			notificationChannel.setVibrationPattern(new long[] { 0 });
			notificationChannel.enableVibration(true);
			notificationManager.createNotificationChannel(notificationChannel);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(LCAT, "onStartCommand with action " + intent.getAction());
		if (intent.getAction().equals("CREATE") || intent.getAction().equals("UPDATE")) {
			Log.d(LCAT, "Intent CREATE ");
			if (intent.hasExtra(TiC.PROPERTY_TITLE)) {
				notificationOpts.put(TiC.PROPERTY_TITLE, intent.getStringExtra(TiC.PROPERTY_TITLE));
			}
			if (intent.hasExtra(TiC.PROPERTY_SUBTITLE)) {
				notificationOpts.put(TiC.PROPERTY_SUBTITLE, intent.getStringExtra(TiC.PROPERTY_SUBTITLE));
			}
			if (intent.hasExtra(TiC.PROPERTY_ICON)) {
				notificationOpts.put(TiC.PROPERTY_ICON, intent.getStringExtra(TiC.PROPERTY_ICON));
			}
		}
		Log.d(LCAT, notificationOpts.toString());
		getNotification();
		return START_STICKY;
	}

	// https://willowtreeapps.com/ideas/mobile-notifications-part-2-some-useful-android-notifications
	/**
	 * Returns the {@link NotificationCompat} used as part of the foreground
	 * service.
	 */
	private Notification getNotification() {

		final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				ctx/*
					 * , Constants.NOTIFICATION.CHANNELID
					 */);
		notificationBuilder //
				.setAutoCancel(true).setSmallIcon(R("applogo", "drawable"))//
				.setDefaults(Notification.DEFAULT_ALL).setPriority(Notification.PRIORITY_HIGH) //
				.setWhen(System.currentTimeMillis()).setOngoing(true)
				.setContentTitle(notificationOpts.getString(TiC.PROPERTY_TITLE))
				.setContentText(notificationOpts.getString(TiC.PROPERTY_SUBTITLE)).setContentIntent(getPendingIntent());
		// Set the Channel ID for Android O.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// buildersetChannel(Constants.NOTIFICATION.CHANNELID);
			Log.d(LCAT, "setChannelId to " + Constants.NOTIFICATION.CHANNELID);
			notificationBuilder.setChannelId(Constants.NOTIFICATION.CHANNELID); // Channel ID
		}

		Notification notification = notificationBuilder.build();
		// notificationManager.notify(Constants.NOTIFICATION.ID, notification);
		startForeground(Constants.NOTIFICATION.ID, notification);
		return notification;
	}

	private PendingIntent getPendingIntent() {
		final String packageName = TiApplication.getInstance().getPackageName();
		final String className = packageName + "." + TiApplication.getAppRootOrCurrentActivity().getLocalClassName();
		Intent activityIntent = new Intent(Intent.ACTION_MAIN);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		activityIntent.setComponent(new ComponentName(packageName, className));
		PendingIntent activityPendingIntent = PendingIntent.getActivity(ctx, 1, activityIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return activityPendingIntent;
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