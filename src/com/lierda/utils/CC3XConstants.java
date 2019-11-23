/**

 * File:CC3XConstants.java

 * Copyright © 2013, Texas Instruments Incorporated - http://www.ti.com/

 * All rights reserved.

 */
package com.lierda.utils;

public class CC3XConstants 
{
	/**
	 * Dialog ID constant for wifi available
	 */
	public static final int DLG_NO_WIFI_AVAILABLE=1;
	/**
	 * Dialog ID constant for SSID invalid
	 */
	public static final int DLG_SSID_INVALID=2;
	/**
	 * Dialog ID constant for password invalid
	 */
	public static final int DLG_PASSWORD_INVALID=3;
	/**
	 * Dialog ID constant for gateway ip invalid
	 */
	public static final int DLG_GATEWAY_IP_INVALID=4;
	/**
	 * Dialog ID constant for encryption key invalid
	 */
	public static final int DLG_KEY_INVALID=5;
	/**
	 * Dialog ID constant for success callback alert
	 */
	public static final int DLG_CONNECTION_SUCCESS=6;
	/**
	 * Dialog ID constant for failure callback alert
	 */
	public static final int DLG_CONNECTION_FAILURE=7;
	/**
	 * Dialog ID constant for timeout alert
	 */
	public static final int DLG_CONNECTION_TIMEOUT=8;
	/**
	 * Dialog ID constant for time delay in showing splash screen
	 */
	public static final int CC3X_SPLASH_DELAY=1500;

	public static final String CMD_TYPE_SEARCH_DEVICE = "searchDevice";
	public static final String CMD_TYPE_TURN_ON_OFF_DEVICE = "turnOnAndOffDevice";
	public static final String CMD_TYPE_SET_TIME_DEVICE = "setTimeDevice";
	public static final String CMD_TYPE_SET_TIMER_DEVICE = "setTimerDevice";
	public static final String CMD_TYPE_READ_INFO_DEVICE = "readDeviceInfo";
	
	public static final byte CMD_START = 0x68;	
	public static final byte CMD_END = 0x16;
	
	
	public static final String ACTION_SWITCH = "Switch OnOrOff";
	public static final String ACTION_TIMER = "Switch Timer";
	public static final String ACTION_DETAIL = "Switch Detail";
	
	public static final String ACTION_SEARCH = "Device Search";

	public static final String LOCAL_DEVICE = "local device";
	public static final String SETTING_INFOS = "Setting_info";
	
}
