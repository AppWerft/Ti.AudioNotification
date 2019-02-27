package de.appwerft.audionotification;

import java.io.FileInputStream;
import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.util.TiUIHelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private NotificationManager notificationManager;
	private KrollDict notificationOpts = new KrollDict();
	private long when = 0;

	public NotificationForegroundService() {
		super();
		ctx = TiApplication.getInstance().getApplicationContext();
		notificationOpts.put("title", "Title");
		notificationOpts.put("subtitle", "SubTitle");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LCAT, "onCreate");
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Android O requires a Notification Channel.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Create the channel for the notification
			NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION.CHANNELID,
					TiApplication.getInstance().getPackageName(), NotificationManager.IMPORTANCE_LOW);
			notificationChannel.setDescription("HörDat");
			notificationChannel.setName("hoerdat");
			notificationChannel.setVibrationPattern(new long[] { 0 });
			notificationChannel.enableVibration(true);
			notificationChannel.setSound(null, null);
			notificationManager.createNotificationChannel(notificationChannel);
		}
		Log.d(LCAT, "onCreate() started");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent==null) {
			Log.e(LCAT,"intent was null");
			return START_STICKY;
		}
		Log.d(LCAT, "onStartCommand with action " + intent.getAction());
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
			if (intent.hasExtra(TiC.PROPERTY_IMAGE)) {
				notificationOpts.put(TiC.PROPERTY_IMAGE, intent.getStringExtra(TiC.PROPERTY_IMAGE));
			}
			startForeground(Constants.NOTIFICATION.ID, getNotification());
			Log.d(LCAT, "startForeground() started");
		}
		if (intent.getAction().equals("CREATE")) {
			when = System.currentTimeMillis();
		}
		if (intent.getAction().equals("REMOVE")) {
			stopForeground(true);
			stopSelf();
		}
		Log.d(LCAT, notificationOpts.toString());

		return START_STICKY;
	}

	// https://willowtreeapps.com/ideas/mobile-notifications-part-2-some-useful-android-notifications
	/**
	 * Returns the {@link NotificationCompat} used as part of the foreground
	 * service.
	 */
	private Notification getNotification() {
		Log.d(LCAT, "getNotification start");
		final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				ctx/*
					 * , Constants.NOTIFICATION.CHANNELID
					 */);
		notificationBuilder //
				.setAutoCancel(true).setSmallIcon(R("applogo", "drawable"))//
				.setDefaults(Notification.DEFAULT_ALL).setPriority(Notification.PRIORITY_HIGH) //
				.setWhen(when).setOngoing(true).setContentTitle(notificationOpts.getString(TiC.PROPERTY_TITLE))
				.setContentText(notificationOpts.getString(TiC.PROPERTY_SUBTITLE)).setContentIntent(getPendingIntent());
		// Set the Channel ID for Android O.
		Log.d(LCAT, "getNotification adding ChannelId");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// buildersetChannel(Constants.NOTIFICATION.CHANNELID);
			notificationBuilder.setChannelId(Constants.NOTIFICATION.CHANNELID); // Channel ID
		}
		if (notificationOpts.containsKeyAndNotNull(TiC.PROPERTY_IMAGE)) {
			String filename = notificationOpts.getString(TiC.PROPERTY_IMAGE);
			try {
				FileInputStream is = this.openFileInput(filename);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
				notificationBuilder.setLargeIcon(bitmap);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Notification notification = notificationBuilder.build();
		// notificationManager.notify(Constants.NOTIFICATION.ID, notification);

		return notification;
	}

	private PendingIntent getPendingIntent() {
		final String packageName = TiApplication.getInstance().getPackageName();
		final String className = packageName + "." + TiApplication.getAppRootOrCurrentActivity().getLocalClassName();
		Intent activityIntent = new Intent(Intent.ACTION_MAIN);
		activityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		activityIntent.setComponent(new ComponentName(packageName, className));
		PendingIntent activityPendingIntent = PendingIntent.getActivity(ctx, 1, activityIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
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