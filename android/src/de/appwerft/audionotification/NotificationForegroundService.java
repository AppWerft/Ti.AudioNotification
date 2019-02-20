package de.appwerft.audionotification;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;

import android.util.Log;

public class NotificationForegroundService extends Service {
	private static final String PACKAGE_NAME = TiApplication.getInstance().getPackageName();
	static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
	private static final String LCAT = "🎈TiAudioNot";
	private final IBinder binder = new LocalBinder();
	public static final String EXTRA_ACTION = "MYACTION";

	private final Context ctx;
	private final String packageName;
	private final String className;
	private boolean changingConfiguration;
	

	private static String contentTitle = null;
	private String contentText = null;
	private int priority = 102;
	private int interval = 10;

	public static final String NOTIFICATION_CHANNEL_ID = "1337";
	public static final String NOTIFICATION_CHANNEL_NAME = "backgroundradioplayer";
	private static final int NOTIFICATION_ID = 12345678;
	private NotificationManager notificationManager;

	public NotificationForegroundService() {
		super();
		ctx = TiApplication.getInstance().getApplicationContext();
		packageName = TiApplication.getInstance().getPackageName();
		className = packageName + "." + TiApplication.getAppRootOrCurrentActivity().getLocalClassName();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public class LocalBinder extends Binder {
		NotificationForegroundService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return NotificationForegroundService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(LCAT, "In onDestroy");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		changingConfiguration = true;
}
	
	@Override
	public IBinder onBind(Intent intent) {
		// Called when a client (MainActivity in case of this sample) comes to
				// the foreground
				// and binds with this service. The service should cease to be a
				// foreground service
				// when that happens.
				stopForeground(true);
				changingConfiguration = false;
				// EventBus.getDefault().register(this);
		return binder;
	}

	@Override
	public void onRebind(Intent intent) {
		// Called when a client (MainActivity in case of this sample) returns to
		// the foreground
		// and binds once again with this service. The service should cease to
		// be a foreground
		// service when that happens.
		Log.i(LCAT, "< ~~~~~ in onRebind()");
		stopForeground(true);
		changingConfiguration = false;
		super.onRebind(intent);
}
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(LCAT, "Last client unbound from service mChangingConfiguration="
				+ changingConfiguration );

		// Called when the last client (MainActivity in case of this sample)
		// unbinds from this
		// service. If this method is called due to a configuration change in
		// MainActivity, we
		// do nothing. Otherwise, we make this service a foreground service.
		if (!changingConfiguration ) {
			Log.i(LCAT, "Starting foreground service");
			/*
			 * // TODO(developer). If targeting O, use the following code. if
			 * (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
			 * notificationManager.startServiceInForeground(new Intent(this,
			 * LocationUpdatesService.class), NOTIFICATION_ID,
			 * getNotification()); } else { startForeground(NOTIFICATION_ID,
			 * getNotification()); }
			 */
			startForeground(NOTIFICATION_ID, getNotification());
		} else
			Log.w(LCAT, "onUnbind: was only a confchanging");
		// EventBus.getDefault().unregister(this);
		return true; // Ensures onRebind() is called when a client re-binds.
}
	
	public void updateNotification(KrollDict opts) {

	}

	public void hideNotification() {

	}

}