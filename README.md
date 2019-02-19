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
AudioNotification.foo();
```

### ES5

```js
var AudioNotification = require('de.appwerft.audionotification');
AudioNotification.foo();
```

