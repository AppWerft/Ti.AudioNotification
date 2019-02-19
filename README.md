# Ti.AudioNotification

## Intro
[Android Oreo switches off the internet connection](https://developer.android.com/about/versions/oreo/background) after a couple of minutes of inactivity. For playing a podcast or radio stream you need a foreground service with notification to avoid this action. 

There are some guidelines around creating and managing foreground services. For all API levels, a persistent notification with at least PRIORITY\_LOW must be shown while the service is created. When targeting API 26+ you will also need to set the notification channel to at least IMPORTANCE\_LOW. The notification must have a way for the user to cancel the work, this cancellation can be tied to the action itself: for example, stopping a music track can also stop the music-playback service. Last, the title and description of the foreground service notification must show an accurate description of what the foreground service is doing. 

In manifest we need:



```xml
<application>
	<service android:name=".NotificationForegroundService" >
</application>
```
old example
// https://www.truiton.com/2014/10/android-foreground-service-example/
 
```
private void startInForeground() {
        Intent notificationIntent = new Intent(this, WorkoutActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.shsl_notification)
                .setContentTitle("TEST")
                .setContentText("HELLO")
                .setTicker("TICKER") 
                .setContentIntent(pendingIntent);
        Notification notification=builder.build();
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, notification);
}
```



## Project Usage

Register your module with your application by editing `tiapp.xml` and adding your module.
Example:

<modules>
  <module version="1.0.0">de.appwerft.audionotification</module>
</modules>

When you run your project, the compiler will combine your module along with its dependencies
and assets into the application.

## Example Usage

To use your module in code, you will need to require it.

### ES6+ (recommended)

```javascript
import AudioNotification from 'de.appwerft.audionotification';

AudioNotification.create({
	icon : Ti.Android.R.drawable.ic_dialog_info,
});

AudioNotification.show({
	cover : '/assets/stations/ndrkultur.png',
	title : "ndr kulturradio",
	subtitle : 'Beethoven: 9. Symfonie'
});

AudioNotification.hide();


```

### ES5

```js
const AudioNotification = require('de.appwerft.audionotification');

```

