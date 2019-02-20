# Ti.AudioNotification

## Intro
[Android Oreo switches off the internet connection](https://developer.android.com/about/versions/oreo/background) after a couple of minutes of inactivity. For playing a podcast or radio stream you need a foreground service with notification to avoid this action. 

There are some guidelines around creating and managing foreground services. For all API levels, a persistent notification with at least PRIORITY\_LOW must be shown while the service is created. When targeting API 26+ you will also need to set the notification channel to at least IMPORTANCE\_LOW. The notification must have a way for the user to cancel the work, this cancellation can be tied to the action itself: for example, stopping a music track can also stop the music-playback service. Last, the title and description of the foreground service notification must show an accurate description of what the foreground service is doing. 

In manifest we need:



```xml
<application>
	<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
	<service android:name=".NotificationForegroundService" >
</application>
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
	lifetimeContainer : win,
	icon : Ti.Android.R.drawable.ic_dialog_info,
	style : Ti.Android
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

