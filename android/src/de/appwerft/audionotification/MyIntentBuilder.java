package de.appwerft.audionotification;

import android.content.Context;
import android.content.Intent;

public class MyIntentBuilder{
	Context ctx;
	String message;
	int commandId;
    public static MyIntentBuilder getInstance(Context context) {
        return new MyIntentBuilder(context);
    }
 
    public MyIntentBuilder(Context context) {
        this.ctx = context;
    }
 
    public MyIntentBuilder setMessage(String message) {
        this.message = message;
        return this;
    }
 
    public MyIntentBuilder setCommand(int command) {
        this.commandId = command;
        return this;
    }
 
    public Intent build() {
        
        Intent intent = new Intent(ctx, NotificationForegroundService.class);
        //if (commandId != Command.INVALID) {
            intent.putExtra(Constants.KEY.COMMAND, commandId);
        //}
        if (message != null) {
            intent.putExtra(Constants.KEY.MESSAGE, message);
        }
        return intent;
    }
 
}