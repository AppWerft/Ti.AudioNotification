package de.appwerft.audionotification;

public class Constants {

	public interface ACTION {
		public static String MAIN_ACTION = "de.appwerft.foregroundservice.action.main";
		public static String PREV_ACTION = "de.appwerft.foregroundservice.action.prev";
		public static String PLAY_ACTION = "de.appwerft.foregroundservice.action.play";
		public static String NEXT_ACTION = "de.appwerft.foregroundservice.action.next";
		public static String STARTFOREGROUND_ACTION = "de.appwerft.foregroundservice.action.startforeground";
		public static String STOPFOREGROUND_ACTION = "de.appwerft.foregroundservice.action.stopforeground";
	}

	public interface NOTIFICATION_ID {
		public static int FOREGROUND_SERVICE = 101;
	}

}
