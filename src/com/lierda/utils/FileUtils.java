package com.lierda.utils;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.lierda.model.DeviceInfo;
import com.lierda.model.TaskInfo;

public class FileUtils {
	
	public static final String TAG_MAC_ADDRESS = "mac_address";
	//public static final String TAG_DEVICE_TYPE = "device_type";
	//public static final String TAG_MANUFR_INFO = "manufr_info";
	//public static final String TAG_SERIAL_NUM = "serial_number";
	//public static final String TAG_VERSION = "version";
	//public static final String TAG_STATUS = "status";
	public static final String TAG_DEVICE_NAME = "device_name";
	//public static final String TAG_DEVICE_TASK = "device_task"; 
	
	//public static final String TAG_TASK_DATE = "task_date";
	//public static final String TAG_TASK_HOURS = "task_hours";
	//public static final String TAG_TASK_MINS = "task_mins";
	//public static final String TAG_TASK_ISOPEN = "task_isOpen";
	
	public static final String TAG_DEVICE_IMG = "device_image";
	
	private static ArrayList<DeviceInfo> deviceList = null;
	
	private static String toHexString(byte[] bytes, String separator) {
		String s1 = "", s2 = "";
		for (int i = 0; i < bytes.length; i++) {
			s1 = Integer.toHexString(0xFF & bytes[i]);
			if (s1.length() == 1) {
				s2 += "0";
			}
			s2 += s1;
			s2 += separator;
		}
		return s2;
	}
	
	private static byte[] hexToBytes(String hexStr, String separator){
		
		if(StringUtils.isBlank(hexStr))
			return null;
		else{
			String s1 = hexStr.replaceAll(separator, "");
			String s2 = "";
			
			LogUtil.printInfo("s1 ex="+s1);
			int length = s1.length()/2;
			byte[] data = new byte[length];
			for(int i=0;i<length;i++){
				s2 = s1.substring(i*2,i*2+2);
				data[i] = (byte) Integer.parseInt(s2, 16);
			}
					
			return data;
		}
	}
	
	public static String SaveDeviceInfo(ArrayList<DeviceInfo>  arrayList){
		
		
		
		//
		try{
			if(arrayList != null){
				JSONObject jsonObject=new JSONObject(); 
				JSONArray array=new JSONArray();
				for(int i=0;i<arrayList.size();i++){
					
					DeviceInfo info=arrayList.get(i); 
					JSONObject deviceObject=new JSONObject();
					
					
					deviceObject.put(TAG_MAC_ADDRESS, toHexString(info.getMacAddress(),""));
					//deviceObject.put(TAG_DEVICE_TYPE, toHexString(info.getDeviceType(),""));
					//deviceObject.put(TAG_MANUFR_INFO, toHexString(info.getManufrInfo(),""));
					//deviceObject.put(TAG_SERIAL_NUM, toHexString(info.getSerialNum(),""));
					//deviceObject.put(TAG_VERSION, Integer.toString(info.getVersion()));
					//deviceObject.put(TAG_STATUS, Integer.toString(info.getStatus()));
					deviceObject.put(TAG_DEVICE_NAME, info.getDeviceName());
					if(info.getImageContent() != null)
						deviceObject.put(TAG_DEVICE_IMG, info.getImageContent());
					
					deviceObject.put(Constants.MODE3_1, info.getModeParam(3, 1));
					deviceObject.put(Constants.MODE3_2, info.getModeParam(3, 2));
					deviceObject.put(Constants.MODE3_3, info.getModeParam(3, 3));
					deviceObject.put(Constants.MODE3_4, info.getModeParam(3, 4));
					
					deviceObject.put(Constants.MODE4_1, info.getModeParam(4, 1));
					deviceObject.put(Constants.MODE4_2, info.getModeParam(4, 2));
					deviceObject.put(Constants.MODE4_3, info.getModeParam(4, 3));
					deviceObject.put(Constants.MODE4_4, info.getModeParam(4, 4));
					
					/*
					if(arrayList.get(i).getTaskList().size()>0){
						
						JSONArray taskArray=new JSONArray();
						for(int j=0;j<arrayList.get(i).getTaskList().size();j++){
							
							TaskInfo taskInfo = arrayList.get(i).getTaskList().get(j);
							JSONObject taskObject=new JSONObject();
							taskObject.put(TAG_TASK_DATE, taskInfo.getDate());
							taskObject.put(TAG_TASK_HOURS, Integer.toString(taskInfo.getHours()));
							taskObject.put(TAG_TASK_MINS, Integer.toString(taskInfo.getMins()));
							taskObject.put(TAG_TASK_ISOPEN, Boolean.toString(taskInfo.getAction()));
							
							taskArray.add(taskObject);
						}
						deviceObject.put(TAG_DEVICE_TASK, taskArray);
					}
					*/
					
					//folksData.add(jsonObject.toString());
					array.add(deviceObject);  
					
				} 
				
				//mSettings.edit().putString(Constants.NOTICE_CODE+et_phoneNumber.getText().toString(), noticeCode).commit();
				
				jsonObject.put("resultBean", array);
				String log = jsonObject.toString();
				
				//getLocalInfo(log);
				//LogUtil.printInfo(log);
				return log;
			}
		}
		catch(Exception e){
			e.getStackTrace();
			return null;
		}
		
		return null;
	}
	
	public static ArrayList<DeviceInfo> getLocalInfo(String jsonStr){
		
		JSONParser parser =  new JSONParser();;
		if(deviceList == null)
			deviceList = new ArrayList<DeviceInfo>();
		else
			deviceList.clear();
		
		try{
			JSONObject obj = (JSONObject) parser.parse(jsonStr);
			JSONArray devicesArrs = (JSONArray) obj.get("resultBean");
			if (devicesArrs == null) {
				LogUtil.printError("null");
				throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
			}
			else{
				for (int i = 0; i < devicesArrs.size(); i++) {
					JSONObject device = (JSONObject) devicesArrs.get(i);
								
					DeviceInfo d= new DeviceInfo();
					d.setStatus(Constants.STATUS_OFFLINE);
					if(null != hexToBytes((String)device.get(TAG_MAC_ADDRESS),""))
						d.setMacAddress(hexToBytes((String)device.get(TAG_MAC_ADDRESS),""),0);
					
					//if(null != hexToBytes((String)device.get(TAG_DEVICE_TYPE),""))
					//	d.setDeviceType(hexToBytes((String)device.get(TAG_DEVICE_TYPE),""),0);
					
					//if(null != hexToBytes((String)device.get(TAG_MANUFR_INFO),""))
					//	d.setManufrInfo(hexToBytes((String)device.get(TAG_MANUFR_INFO),""),0);
					
					//if(null != hexToBytes((String)device.get(TAG_SERIAL_NUM),""))
					//	d.setSerialNum(hexToBytes((String)device.get(TAG_SERIAL_NUM),""),0);
					//d.setVersion((byte) Integer.parseInt((String)device.get(TAG_VERSION)));
					//d.setStatus((byte) Integer.parseInt((String)device.get(TAG_STATUS)));
					d.setDeviceName((String)device.get(TAG_DEVICE_NAME));
					if(device.containsKey(TAG_DEVICE_IMG))
						d.setImageContent((byte[])device.get(TAG_DEVICE_IMG));
						
					if(device.containsKey(Constants.MODE3_1))
						d.setModeParam(3, 1, (String)device.get(Constants.MODE3_1));
					if(device.containsKey(Constants.MODE3_2))
						d.setModeParam(3, 2, (String)device.get(Constants.MODE3_2));
					if(device.containsKey(Constants.MODE3_3))
						d.setModeParam(3, 3, (String)device.get(Constants.MODE3_3));
					if(device.containsKey(Constants.MODE3_4))
						d.setModeParam(3, 4, (String)device.get(Constants.MODE3_4));
					
					if(device.containsKey(Constants.MODE4_1))
						d.setModeParam(4, 1, (String)device.get(Constants.MODE4_1));
					if(device.containsKey(Constants.MODE4_2))
						d.setModeParam(4, 2, (String)device.get(Constants.MODE4_2));
					if(device.containsKey(Constants.MODE4_3))
						d.setModeParam(4, 3, (String)device.get(Constants.MODE4_3));
					if(device.containsKey(Constants.MODE4_4))
						d.setModeParam(4, 4, (String)device.get(Constants.MODE4_4));
					/*
					JSONArray tasksArrs = (JSONArray) device.get(TAG_DEVICE_TASK);
					if (tasksArrs == null) {
						LogUtil.printError("null");
						throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
					}
					else{
						for (int j = 0; j < tasksArrs.size(); j++) {
							JSONObject taskObj = (JSONObject) tasksArrs.get(j);
							TaskInfo task = new TaskInfo();
							
							task.setDate((String)taskObj.get(TAG_TASK_DATE));
							task.setHours(Integer.parseInt((String)taskObj.get(TAG_TASK_HOURS)));
							task.setMins(Integer.parseInt((String)taskObj.get(TAG_TASK_MINS)));
							task.setAction(Boolean.parseBoolean((String)taskObj.get(TAG_TASK_ISOPEN)));
							
							d.getTaskList().add(task);
						}
					}
					*/
					deviceList.add(d);			
				}
			}
		}
		catch(Exception e){
			e.getStackTrace();
		}
		
		return deviceList;
	}

}
