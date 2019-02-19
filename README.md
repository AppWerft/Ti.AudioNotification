# Ti.AudioNotification


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
const Notification = AudioNotification.createNotification({
	icon : Ti.Android.R.drawable.ic_dialog_info,
	cover : '/assets/stationlogo.png',
	title : "Name of station",
	subtitle : "Message"
});
Notification.update({
	subtitle : 'new message'
});

Notification.remove();


```

### ES5

```js
const AudioNotification = require('de.appwerft.audionotification');

```

