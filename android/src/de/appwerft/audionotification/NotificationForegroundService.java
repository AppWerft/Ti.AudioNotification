package de.appwerft.audionotification;

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
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationForegroundService extends Service {
	private static final String PACKAGE_NAME = TiApplication.getInstance().getPackageName();
	static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
	private static final String LCAT = TiaudionotificationModule.LCAT;
	public static final String EXTRA_ACTION = "MYACTION";
	private final Context ctx;
	private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";

	private boolean changingConfiguration;
	private NotificationManager notificationManager;
	private KrollDict notificationOpts = new KrollDict();

	public NotificationForegroundService() {
		super();
		ctx = TiApplication.getInstance().getApplicationContext();

	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LCAT, "getSystemService inside onCreate");
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Android O requires a Notification Channel.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Create the channel for the notification
			NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION.CHANNELID,
					TiApplication.getInstance().getPackageName(), NotificationManager.IMPORTANCE_DEFAULT);
			// Set the Notification Channel for the Notification Manager.
			notificationManager.createNotificationChannel(channel);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		stopForeground(true);
		changingConfiguration = false;
		Log.d(LCAT, "onBind");
		return binder;// messenger.getBinder();
	}

	private final IBinder binder = new LocalBinder();

	public class LocalBinder extends Binder {
		NotificationForegroundService getService() {
			Log.d(LCAT, "LocalBinder");
			return NotificationForegroundService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		changingConfiguration = true;
	}

	@Override
	public void onRebind(Intent intent) {
		stopForeground(true);
		changingConfiguration = false;
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (!changingConfiguration) {
			startForeground(Constants.NOTIFICATION.ID, buildNotification());
			;
		} else
			Log.w(LCAT, "onUnbind: was only a confchanging");
		return true; // Ensures onRebind() is called when a client re-binds.
	}

	public void updateNotification(KrollDict opts) {
		notificationOpts = opts;
		if (opts.containsKeyAndNotNull(TiC.PROPERTY_TITLE)) {
		}
		
	}

	public void hideNotification() {
	}

	// https://willowtreeapps.com/ideas/mobile-notifications-part-2-some-useful-android-notifications
	/**
	 * Returns the {@link NotificationCompat} used as part of the foreground
	 * service.
	 */
	private Notification buildNotification() {
		Log.d(LCAT, "start buildNotification()!");

		Intent intent = new Intent(this, NotificationForegroundService.class);
		// Extra to help us figure out if we arrived in onStartCommand via the
		// notification or not.
		intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

		// The activityIntent calls the app
		Intent activityIntent = new Intent(Intent.ACTION_MAIN);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

		final String packageName = TiApplication.getInstance().getPackageName();
		activityIntent.setComponent(new ComponentName(packageName,
				packageName + "." + TiApplication.getAppRootOrCurrentActivity().getLocalClassName()));
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, activityIntent, 0);
		Log.d(LCAT,"intents ready, try build Builder ");
		Log.d(LCAT,notificationOpts.toString());
		
		// Building notification:
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
		Log.d(LCAT,"smallIcon"+R("applogo", "drawable"));
		builder.setSmallIcon(R("applogo", "drawable"));
		Log.d(LCAT,"Title");
		builder.setContentTitle(notificationOpts.containsKeyAndNotNull(TiC.PROPERTY_TITLE)
				? notificationOpts.getString(TiC.PROPERTY_TITLE)
				: "TEST");
		Log.d(LCAT,"Text");
		builder.setContentText(notificationOpts.containsKeyAndNotNull(TiC.PROPERTY_SUBTITLE)
				? notificationOpts.getString(TiC.PROPERTY_SUBTITLE)
				: "UNTERTEST");
		Log.d(LCAT,"largeIcon");
		builder.setLargeIcon(notificationOpts.containsKeyAndNotNull(Constants.LOGO.LOCAL)
				? (Bitmap) notificationOpts.get(Constants.LOGO.LOCAL)
				: null);
		Log.d(LCAT,pendingIntent.toString());
		builder.setContentIntent(pendingIntent);
		Log.d(LCAT, "Notification build => channel for OREO ");
		// Set the Channel ID for Android O.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
	//		buildersetChannel(Constants.NOTIFICATION.CHANNELID);
			builder.setChannelId(Constants.NOTIFICATION.CHANNELID); // Channel ID
		}
		return builder.build();
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

	final Messenger messenger = new Messenger(new IncomingHandler());

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