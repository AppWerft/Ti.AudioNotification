package de.appwerft.audionotification;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiApplication;

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
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;

import android.os.Build.VERSION_CODES;

import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationForegroundService extends Service {
	private static final String PACKAGE_NAME = TiApplication.getInstance().getPackageName();
	static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
	private static final String LCAT = TiaudionotificationModule.LCAT;
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
	private KrollDict notificationOpts;

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
		Log.i(LCAT, "Last client unbound from service mChangingConfiguration=" + changingConfiguration);

		// Called when the last client (MainActivity in case of this sample)
		// unbinds from this
		// service. If this method is called due to a configuration change in
		// MainActivity, we
		// do nothing. Otherwise, we make this service a foreground service.
		if (!changingConfiguration) {
			Log.i(LCAT, "Starting foreground service");
			/*
			 * // TODO(developer). If targeting O, use the following code. if
			 * (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
			 * notificationManager.startServiceInForeground(new Intent(this,
			 * LocationUpdatesService.class), NOTIFICATION_ID, getNotification()); } else {
			 * startForeground(NOTIFICATION_ID, getNotification()); }
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

	@SuppressWarnings("deprecation")
	private Notification getNotification() {
		Log.i(LCAT, "getNotification started");
		Intent intent = new Intent(this, NotificationForegroundService.class);

		// The activityIntent calls the app
		Intent activityIntent = new Intent(Intent.ACTION_MAIN);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		activityIntent.setComponent(new ComponentName(packageName, className));
		PendingIntent activityPendingIntent = PendingIntent.getActivity(ctx, 1, activityIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// https://stackoverflow.com/questions/45462666/notificationcompat-builder-deprecated-in-android-o
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
		// Android O requires a Notification Channel.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			int importance = NotificationManager.IMPORTANCE_DEFAULT;

			NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
					NOTIFICATION_CHANNEL_NAME, importance);
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.BLUE);
			NotificationManager notificationManager = (NotificationManager) ctx
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.createNotificationChannel(notificationChannel);
		//	builder.setChannelId(NOTIFICATION_CHANNEL_ID);
		}

		// Uri defaultSoundUri = RingtoneManager
		// .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (notificationOpts.containsKeyAndNotNull("contentText")) {
			String contentText = notificationOpts.getString("contentText");
			builder.setContentText(contentText);
		}
		builder.setContentTitle(contentTitle).setOngoing(true).setPriority(Notification.FLAG_FOREGROUND_SERVICE)
				.setContentIntent(activityPendingIntent).setSmallIcon(Utils.R("ic_launcher", "mipmap")).setSound(null)
				.setSubText(notificationOpts.getString("subText"))

				.setContentTitle(notificationOpts.getString("contentTitle")).setVibrate(null)
				.setWhen(System.currentTimeMillis());
		/*
		 * if (notificationOpts.containsKeyAndNotNull("bigText")) { CharSequence bigText
		 * = notificationOpts.getString("bigText"); BigTextStyle style = new
		 * Notification.BigTextStyle() .bigText(bigText); builder.setStyle(style); }
		 */
		if (notificationOpts.containsKeyAndNotNull("lockscreenVisibility")) {
			builder.setVisibility(notificationOpts.getInt("lockscreenVisibility"));
		}
		if (notificationOpts.containsKeyAndNotNull("largeIcon")) {
			String largeIcon = notificationOpts.getString("largeIcon");
			/*final Target target = new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
					builder.setLargeIcon(bitmap);
				}

				@Override
				public void onBitmapFailed(Drawable errorDrawable) {
					Log.e(LCAT, "bitMap failed ");
				}

				@Override
				public void onPrepareLoad(Drawable placeHolderDrawable) {
					Log.d(LCAT, "onPrepareLoad");
				}
			};*/
			//Picasso.with(ctx).load(largeIcon).resize(150, 150).into(target);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// builder.setChannelId(CHANNEL_ID); // Channel ID
		}
		return builder.build();
	}
	
	private void startInForeground() {
        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
        PendingIntent pendingIntent=PendingIntent.getActivity(ctx,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
            //    .setSmallIcon(R.drawable.shsl_notification)
                .setContentTitle("TEST")
                .setContentText("HELLO")
                .setTicker("TICKER") 
                .setContentIntent(pendingIntent);
        Notification notification=builder.build();
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(
            		Constants.NOTIFICATION.CHANNELID, 
            		Constants.NOTIFICATION.CHANNELNAME, 
            		
            		NotificationManager.IMPORTANCE_DEFAULT);
          //  channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, notification);
}
}