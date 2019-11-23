package com.lierda.utils;


import java.util.ArrayList;

import android.os.Environment;


public class Constants {
	public static final boolean DEBUG = true; 
	public static final int RESULT_QUIT = 0x80;	
	
	public static final int DEFAULT_TIMEOUT = 15000;
	
	public static final int STATUS_OFFLINE = 500;
	public static final int STATUS_ONLINE = 501;
	public static final int STATUS_REMOTE = 502;
	
	public static final String DEFAULT_SERVER_IP = "www.anymilight.com";//"42.121.118.8";//
	public static final int DEFAULT_SERVER_PORT  = 38899;//8899;//
	
	
	public static final String CMD_TCP_ONLINE = "APP#{0}#ST\n";
	public static final String CMD_TCP_REMOTE_CONTROL = "APP#{0}#CMD#{1}\n";
	
	public static final String CMD_HEARTBEAT = "AT+W\r";
	public static final String CONFIGURE_HEAD = "Link_Wi-Fi"; //"HF-A11ASSISTHREAD";
	public static final String CONFIGURE_OK = "+ok";

	public static final String CONFIGURE_QUIT = "AT+Q\r";
	public static final String CONFIGURE_SCAN = "AT+WSCAN\r";
	public static final String CONFIGURE_QUERY_SSID = "AT+WSSSID\r";
	public static final String CONFIGURE_QUERY_WSKEY = "AT+WSKEY\r";
	public static final String CONFIGURE_SET_SSID = "AT+WSSSID={0}\r";
	public static final String CONFIGURE_SET_WSKEY = "AT+WSKEY={0},{1},{2}\r"; 
	
	public static final String CONFIGURE_ENABLE_SERVER = "AT+TCPB=on\r";
	public static final String CONFIGURE_GET_SERVER_ADDRESS = "AT+TCPADDB\r";
	public static final String CONFIGURE_GET_SERVER_PORT = "AT+TCPPTB\r";
	public static final String CONFIGURE_SET_SERVER_ADDRESS = "AT+TCPADDB={0}\r";
	public static final String CONFIGURE_SET_SERVER_PORT = "AT+TCPPTB={0}\r";
	
	public static final String CONFIGURE_GET_WORK_MODE = "AT+WMODE\r";
	public static final String CONFIGURE_SET_WORK_MODE = "AT+WMODE={0}\r";
	
	public static final String CMD_RESTART = "AT+Z\r";
	public static final String CMD_FACTORY_SETTING = "AT+RELD\r";
	
	public static final String CMD_SEARCH = "Link_Wi-Fi";
	public static final String ACTION_TIME_OUT = "timeOut";
	public static final String ACTION_ERROR = "error";
	public static final String ACTION_SEARCH = "search";
	//public static final String ACTION_CONNET = "to_connect";
	//public static final String ACTION_CONNETED = "connected";
	public static final int    CONFIGURE_PORT = 48899;
	
	
	public static final String AUTH_MODE_OPEN = "OPEN";
	public static final String AUTH_MODE_SHARED = "SHARED";
	public static final String AUTH_MODE_WPAPSK = "WPAPSK";
	public static final String AUTH_MODE_WPA2PSK = "WPA2PSK";
	
	public static final String ENCRYPT_NONE = "NONE";
	public static final String ENCRYPT_WEP_H = "WEP-H";
	public static final String ENCRYPT_WEP_A = "WEP-A";
	public static final String ENCRYPT_TKIP = "TKIP";
	public static final String ENCRYPT_AES = "AES";
	
	//configure
	public static final String SETTING_INFOS = "SETTINGINFOS";
	public static final String SERVER_ADDRESS = "server_address";
	public static final String SERVER_PORT = "server_port";
	public static final String LOCAL_PORT = "local_port";
	public static final String CONFIGURE_DEVICE_IP = "device_ip";
	public static final String ACTION_TYPE = "action_type";
	
	public static final String MODE3_1 = "mode3_1";
	public static final String MODE3_2 = "mode3_2";
	public static final String MODE3_3 = "mode3_3";
	public static final String MODE3_4 = "mode3_4";
	
	public static final String MODE4_1 = "mode4_1";
	public static final String MODE4_2 = "mode4_2";
	public static final String MODE4_3 = "mode4_3";
	public static final String MODE4_4 = "mode4_4";
	
	public static String imageCachePath_data;
	public static final String imageCachePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/lierda/imageCache/";
}
