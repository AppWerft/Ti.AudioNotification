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
	public interface MSG {
		public static int UPDATE = 13;
	}
	public interface LOGO {
		public static String LOCAL = "LOCAL";
		public static String REMOTE = "REMOTE";
	}
	public interface NOTIFICATION {
		public static int FOREGROUND_SERVICE = 101;
		public static String ID = "121234";
		public static String CHANNELID= "2345";
		public static String CHANNELNAME= "Hoerdatplayer";
	}
  
}
