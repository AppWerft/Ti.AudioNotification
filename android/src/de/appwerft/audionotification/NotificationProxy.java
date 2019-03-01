package de.appwerft.audionotification;

import java.io.FileOutputStream;
import java.io.IOException;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.util.TiUIHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;

@Kroll.proxy(creatableInModule = TiaudionotificationModule.class)
public class NotificationProxy extends KrollProxy {
	// A reference to the service used to get location updates.
	private NotificationForegroundService notificationForegroundService = null;
	// Standard Debugging variables
	public static final String LCAT = TiaudionotificationModule.LCAT + "_Proxy";
	private Context ctx;
	// Tracks the bound state of the service.

	private KrollDict notificationOpts = new KrollDict();
	
	private boolean notificationactive = false;

	public NotificationProxy() {
		super();
		ctx = TiApplication.getInstance().getApplicationContext();

	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {

	}

	// Handle creation options
	@Override
	public void handleCreationDict(KrollDict opts) {
		Log.d(LCAT, "handleCreationDict: " + opts.toString());
		if (opts.containsKeyAndNotNull(TiC.PROPERTY_TITLE))
			notificationOpts.put(TiC.PROPERTY_TITLE, opts.getString(TiC.PROPERTY_TITLE));
		if (opts.containsKeyAndNotNull(TiC.PROPERTY_SUBTITLE))
			notificationOpts.put(TiC.PROPERTY_SUBTITLE, opts.getString(TiC.PROPERTY_SUBTITLE));
		if (opts.containsKeyAndNotNull(TiC.PROPERTY_ICON))
			notificationOpts.put(TiC.PROPERTY_ICON, opts.getString(TiC.PROPERTY_ICON));
		if (opts.containsKeyAndNotNull(TiC.PROPERTY_IMAGE)) {
			notificationOpts.put(TiC.PROPERTY_IMAGE, getImagePath(opts.getString(TiC.PROPERTY_IMAGE)));
		}	
		super.handleCreationDict(opts);
	}


	@Kroll.method
	public void update() {
		this.start();
	}

	@Kroll.method
	public void start() {
		Intent serviceIntent = new Intent(ctx, NotificationForegroundService.class);
		Log.d(LCAT,"show::" +notificationOpts.toString());
		if (notificationOpts.containsKey(TiC.PROPERTY_TITLE))
			serviceIntent.putExtra(TiC.PROPERTY_TITLE, notificationOpts.getString(TiC.PROPERTY_TITLE));
		if (notificationOpts.containsKey(TiC.PROPERTY_SUBTITLE))
			serviceIntent.putExtra(TiC.PROPERTY_SUBTITLE, notificationOpts.getString(TiC.PROPERTY_SUBTITLE));
		if (notificationOpts.containsKey(TiC.PROPERTY_ICON))
			serviceIntent.putExtra(TiC.PROPERTY_ICON, notificationOpts.getString(TiC.PROPERTY_ICON));
		if (notificationOpts.containsKey(TiC.PROPERTY_IMAGE)) {
			serviceIntent.putExtra(TiC.PROPERTY_IMAGE, notificationOpts.getString(TiC.PROPERTY_IMAGE));
		}	
		serviceIntent.setAction("CREATE");
		ctx.startForegroundService(serviceIntent);
		Log.d("LCAT", "startForegroundService(serviceIntent)");
		notificationactive=true;
	}


	@Kroll.method
	public void stop() {
		if (notificationactive) {
		Intent serviceIntent = new Intent(ctx, NotificationForegroundService.class);
		serviceIntent.setAction("REMOVE");
		ctx.startForegroundService(serviceIntent);
		notificationactive=false;
	}
	}
	

	@Kroll.method
	public void setTitle(String title) {
		notificationOpts.put(TiC.PROPERTY_TITLE,title);
		update();
	}

	@Kroll.method
	public void setSubtitle(String subtitle) {
		notificationOpts.put(TiC.PROPERTY_SUBTITLE,subtitle);
		update();
	}
	
	@Kroll.method
	public void setLargeIcon(String path) {
		notificationOpts.put(TiC.PROPERTY_IMAGE,  cacheImage(path));
		update();
	}
	

	private Bitmap loadImage(String imageName) {
		Bitmap bitmap = null;
		try {
			TiBaseFile file = TiFileFactory.createTitaniumFile(new String[] { resolveUrl(null, imageName) }, false);
			bitmap = TiUIHelper.createBitmap(file.getInputStream());
			return bitmap;
		} catch (IOException e) {
			return null;
		}
	}
	private String cacheImage(String imageName) {
		Bitmap bmp = loadImage(imageName);
		try {
		    //Write file
		    String filename = "logo-bitmap.png";
		    FileOutputStream stream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
		    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		    //Cleanup
		    stream.close();
		    bmp.recycle();
		    return filename;
		    //Pop intent
		} catch (Exception e) {
		//    e.printStackTrace();
		    return null;
		}
		
		
	}
	
	private String getImagePath(String imageName) {
		TiBaseFile file = TiFileFactory.createTitaniumFile(new String[] { resolveUrl(null, imageName) }, false);
		return file.nativePath();	
	}

	@Kroll.method
	public void hide() {

	}

	private static String getApplicationName(Context context) {
		ApplicationInfo applicationInfo = context.getApplicationInfo();
		int stringId = applicationInfo.labelRes;
		return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
	}

	@Override
	public void onStart(Activity activity) {
		super.onStart(activity);
		Log.d(LCAT, ">>>>>>> onStart called");
	}

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		Log.d(LCAT, ">>>>>> onResume called");
	}

	@Override
	public void onPause(Activity activity) {
		Log.d(LCAT, "<<<<<< onPause called");
		// LocalBroadcastManager.getInstance(ctx).unregisterReceiver(receiver);
		super.onPause(activity);
	}

	@Override
	public void onStop(Activity activity) {
		Log.d(LCAT, "<<<<<< onStop called");

		super.onStop(activity);
	}

	@Override
	public void onDestroy(Activity activity) {
		Log.d(LCAT, "<<<<<< onDestroy called");
		remove();
		super.onDestroy(activity);
	}

}
