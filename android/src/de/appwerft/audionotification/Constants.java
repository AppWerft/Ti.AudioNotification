package de.appwerft.audionotification;

public class Constants {
	public interface KEY {
		public static String COMMAND ="de.appwerft.foregroundservice.command";
		public static String MESSAGE ="de.appwerft.foregroundservice.message";
	}
	public interface ACTION {
		public static String MAIN_ACTION = "de.appwerft.foregroundservice.action.main";
		public static String PREV_ACTION = "de.appwerft.foregroundservice.action.prev";
		public static String PLAY_ACTION = "de.appwerft.foregroundservice.action.play";
		public static String NEXT_ACTION = "de.appwerft.foregroundservice.action.next";
		public static String CREATE = "de.appwerft.foregroundservice.action.create";
		public static String UPDATE = "de.appwerft.foregroundservice.action.update";
		public static String REMOVE = "de.appwerft.foregroundservice.action.remove";
		
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
		public static int ID = 1;
		public static String CHANNELID = "2345";
		public static String CHANNELNAME = "Hoerdatplayer";
		public static String CHANNEL_DESC = "Hördatplayer";
	}

}
