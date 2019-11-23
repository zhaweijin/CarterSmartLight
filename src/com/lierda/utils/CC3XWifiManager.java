/**

* File:CC3XWifiManager.java

* Copyright © 2013, Texas Instruments Incorporated - http://www.ti.com/

* All rights reserved.

*/
package com.lierda.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
/**
 * Wifi manager class for getting the WIFI details and SSID gateway parameters
 * @author raviteja
 *
 */
public class CC3XWifiManager 
{
	/**
	 * Default wifimanager instance
	 */
	private  WifiManager  mWifiManager=null;
	
	/**
	 * Wifi info instance 
	 */
	private  WifiInfo 	 mWifiInfo=null;
	/**
	 * Called activity context
	 */
	private  Context 	 mContext=null;

	/**
	 * Constructor for custom alert dialog.accepting context of called activity
	 * @param mContext
	 */
	public CC3XWifiManager(Context mContext) 
	{
		this.mContext=mContext;
		mWifiManager=(WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo=mWifiManager.getConnectionInfo();
	}

	/**
	 * returns BASE ssid 
	 * @return BASE ssid 
	 */
	public String getBaseSSID()
	{
		return mWifiInfo.getBSSID();
	}
	/**
	 * returns current  ssid  connected to
	 * @return current ssid 
	 */
	public  String getCurrentSSID()
	{
		return mWifiInfo.getSSID();
	}

	/**
	 *method to check wifi
	 *  
	 * @return true if wifi is connected in our device else false
	 */
	public boolean isWifiConnected()
	{
		ConnectivityManager connManager = (ConnectivityManager)mContext. getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected())
		{
			return true;
		}
		else
			return false;

	}

	/**
	 * Returns the current IP address connected to
	 * @return
	 */
	public  String getCurrentIpAddressConnected()
	{
		int ipval=	mWifiInfo.getIpAddress();
		String ipString = String.format("%d.%d.%d.%d", (ipval & 0xff),(ipval >> 8 & 0xff),(ipval >> 16 & 0xff),	(ipval >> 24 & 0xff));

		return ipString.toString();
	}

	/**
	 * Returns the current GatewayIP address connected to
	 * @return
	 */
	public  String getGatewayIpAddress()
	{
		int gatwayVal=	mWifiManager.getDhcpInfo().gateway;
		return (String.format("%d.%d.%d.%d", (gatwayVal & 0xff),(gatwayVal >> 8 & 0xff),(gatwayVal >> 16 & 0xff),	(gatwayVal >> 24 & 0xff))).toString();
	}


}
