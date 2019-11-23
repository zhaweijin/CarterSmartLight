package com.lierda.utils;

import java.util.ArrayList;

import com.lierda.model.DeviceInfo;
import com.lierda.model.TaskInfo;
import com.lierda.model.WifiInfo;

public class CC3XParser {
	
	private DeviceInfo deviceInfo =null;
	private static CC3XParser instance = null;
	private int tag_ssid=0,tag_mode = 0,tag_signal=0;;
	
	public static CC3XParser getInstance() {
		if (null == instance) {
			instance = new CC3XParser();
		}
		return instance;
	}
	
	public DeviceInfo parseDevice(String data){
		
		deviceInfo = null;
		
		int index = data.indexOf(",");
		if(index != -1){
			deviceInfo  = new DeviceInfo();
			deviceInfo.setIpStr(data.substring(0, index));
			String macAddress = data.substring(index+1,data.length());
			byte[] mac = new byte[6];
			for(int i=0;i<macAddress.length()/2;i++){
				mac[i] = (byte) Integer.parseInt(macAddress.substring(i*2,i*2+2), 16);
			}
				
			deviceInfo.setMacAddress(mac, 0);
		}
		
		
		return deviceInfo;
	}
	
	public ArrayList<WifiInfo> parseWifi(String resultStr){
		
		if(StringUtils.isBlank(resultStr)){
			return null;
		}
		else{
			ArrayList<WifiInfo> wifiList = new ArrayList<WifiInfo>();
			int nStart = 0,nEnd=0;
			String wifiInfoStr="",s1="";
			if('|' != resultStr.charAt(resultStr.length()-1))
			{
				s1 = resultStr + "|";
			}
			else 
			{
				s1 = resultStr;
			}
			nStart = 0;
			nEnd = s1.indexOf("|");
			while(nEnd!=-1)
			{
				wifiInfoStr = s1.substring(nStart, nEnd);
				boolean isFormat = false;
				if(wifiInfoStr.indexOf("SSID")!=-1)
					isFormat = true;
				else
					isFormat = false;
				WifiInfo wifi = parserWifiInfo(wifiInfoStr,isFormat);
				
				if(isFormat == false && wifi!= null)
					wifiList.add(wifi);
				nStart = nEnd+1;
				s1 = s1.substring(nStart);
				nStart = 0;
				nEnd = s1.indexOf("|");
			}
			return wifiList;
		}
		
	}
	
	private WifiInfo parserWifiInfo(String wifiInfoStr,boolean isFormat)
	{
		try{
			
			int nStart = 0,nEnd=0,nTag = 0;
			String infoStr="",s1="";
			WifiInfo wifiInfo = new WifiInfo();
			
			if(',' != wifiInfoStr.charAt(wifiInfoStr.length()-1))
			{
				s1 = wifiInfoStr + ",";
			}
			else
			{
				s1 = wifiInfoStr;
			}
			nStart = 0;
			nTag = 0;
			nEnd = s1.indexOf(",");
			
			LogUtil.printInfo("wifiInfoStr = "+wifiInfoStr);
			while(nEnd!=-1)
			{
				infoStr = s1.substring(nStart, nEnd);
				
				if(isFormat){
					
					if(infoStr.indexOf("SSID") == 0){
						tag_ssid = nTag;
					}
					else if(infoStr.indexOf("Security") != -1){
						tag_mode = nTag;
					}
					else if(infoStr.indexOf("Siganl") != -1){
						tag_signal = nTag;
					}
				}
				else{
					if(nTag == tag_ssid)
					{
						wifiInfo.setSSID(infoStr);
					}
					else if(nTag == tag_mode)
					{
						int index = infoStr.indexOf("/");
						if(index != -1)
							wifiInfo.setChiperAlgorithm(infoStr.substring(index+1,infoStr.length()));
						else
							wifiInfo.setChiperAlgorithm(Constants.ENCRYPT_NONE);
						
						if(infoStr.indexOf("WPAPSK") != -1 || infoStr.indexOf("WPA1PSK") != -1 ){
							wifiInfo.setChiperMode(Constants.AUTH_MODE_WPAPSK);
						}
						else if(infoStr.indexOf("WPA2PSK") != -1){
							wifiInfo.setChiperMode(Constants.AUTH_MODE_WPA2PSK);
						}
						else if(infoStr.indexOf("WEP") != -1){
							wifiInfo.setChiperMode(Constants.AUTH_MODE_SHARED);
							if(Constants.ENCRYPT_NONE.equals(wifiInfo.getChiperAlgorithm()))
								wifiInfo.setChiperAlgorithm(Constants.ENCRYPT_WEP_A);
						}
						else{
							wifiInfo.setChiperMode(Constants.AUTH_MODE_OPEN); 
						}
					}
					else if(nTag == tag_signal){
						
						int signal =  Integer.parseInt(infoStr);
						wifiInfo.setSignal(signal);
					}
				}
				nStart = nEnd+1;
				s1 = s1.substring(nStart);
				nTag ++ ;
				nStart = 0;
				nEnd = s1.indexOf(",");
			}
			return wifiInfo;
		}
		catch(Exception e){
			return null;
		}
	}
	

}
