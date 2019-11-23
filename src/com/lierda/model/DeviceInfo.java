package com.lierda.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;


public class DeviceInfo implements Serializable {

	//private static Map<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
	
	private String ipStr = null;
	//private String serialNo = null;
	//private byte [] ipAddress = null;
	//private byte [] port = null;
	private byte [] macAddress = null;
	//private byte [] device_type = null;
	//private byte [] manufr_info = null;
	//private byte [] serial_number = null;
	private byte [] img_content = null;

	//private byte version = 0;
	private int status = Constants.STATUS_ONLINE;
	private int pre_status = Constants.STATUS_OFFLINE;
	
	private String name = "Kapego RF 2.0";
	//private boolean isCheck = false;
	private boolean isOn = false;

	//private boolean isOnline = true;
	//private ArrayList<TaskInfo> taskList = null;
	
	
	private String mode3_1 = "1";
	private String mode3_2 = "2";
	private String mode3_3 = "3";
	private String mode3_4 = "4";
	
	private String mode4_1 = "1";
	private String mode4_2 = "2";
	private String mode4_3 = "3";
	private String mode4_4 = "4";
	
	public DeviceInfo(){
		//ipAddress = new byte[4];
		//port = new byte[2];
		macAddress = new byte[6];
		//device_type = new byte[2];
		//manufr_info = new byte[2];
		//serial_number = new byte[6];
	}
	
	public void setIpStr(String ip){
		this.ipStr = ip;
	}
	public String getIpStr2(){
		return this.ipStr;
	}
	
	public String getModeParam(int mode,int index){
		if(mode == 3){
			if(index == 1){
				return mode3_1;
			}
			else if(index == 2){
				return mode3_2;
			}
			else if(index == 3){
				return mode3_3;
			}
			else{
				return mode3_4;
			}
		}
		else if(mode == 4){
			if(index == 1){
				return mode4_1;
			}
			else if(index == 2){
				return mode4_2;
			}
			else if(index == 3){
				return mode4_3;
			}
			else{
				return mode4_4;
			}
		}
		else{
			return null;
		}
	}
	
	public void setModeParam(int mode,int index,String param){
		if(mode == 3){
			if(index == 1){
				mode3_1 = param;
			}
			else if(index == 2){
				mode3_2 = param;
			}
			else if(index == 3){
				mode3_3 = param;
			}
			else{
				mode3_4 = param;
			}
		}
		else if(mode == 4){
			if(index == 1){
				mode4_1 = param;
			}
			else if(index == 2){
				mode4_2 = param;
			}
			else if(index == 3){
				mode4_3 = param;
			}
			else{
				mode4_4 = param;
			}
		}
	}
	

	public void setMacAddress(byte[] macAddress1,int offset){
		System.arraycopy(macAddress1, offset, this.macAddress, 0, this.macAddress.length);
	}
	public byte[] getMacAddress(){
		return this.macAddress;
	}
	public String getMacStr(){
		String str1 = "";
		String mac = ""; 
		
		str1 = Integer.toHexString(macAddress[0]&0xFF);	
		if(str1.length() == 1){
			mac+="0";
		}
		mac += str1;
		mac += ":";
		
		str1 = Integer.toHexString(macAddress[1]&0xFF);
		if(str1.length() == 1){
			mac+="0";
		}
		mac += str1;
		mac += ":";
		
		str1 = Integer.toHexString(macAddress[2]&0xFF);
		if(str1.length() == 1){
			mac+="0";
		}
		mac += str1;
		mac += ":";
		
		str1 = Integer.toHexString(macAddress[3]&0xFF);
		if(str1.length() == 1){
			mac+="0";
		}
		mac += str1;
		mac += ":";
		
		str1 = Integer.toHexString(macAddress[4]&0xFF);
		if(str1.length() == 1){
			mac+="0";
		}
		mac += str1;
		mac += ":";
		
		str1 = Integer.toHexString(macAddress[5]&0xFF);
		if(str1.length() == 1){
			mac+="0";
		}
		mac += str1;
		return mac.toUpperCase();				
	}
	
	/*
	public void setSerialNo(String serialNo){
		this.serialNo = serialNo;
	}
	public String getSerialNo(){
		return this.serialNo;
	}
	
	public void setIpAddress(byte[] ip,int offset){
		System.arraycopy(ip, offset, ipAddress, 0, 4);
	}
	public byte[] getIpAddress(){
		return this.ipAddress;
	}
	public String getIpStr(){
		String ip = Integer.toString((int)(ipAddress[3]&0xFF));
		ip += ".";
		ip += Integer.toString((int)(ipAddress[2]&0xFF));
		ip += ".";
		ip += Integer.toString((int)(ipAddress[1]&0xFF));
		ip += ".";
		ip += Integer.toString((int)(ipAddress[0]&0xFF));
		LogUtil.printInfo(ip);
		return ip;
	}
	
	public void setPort(byte[] port,int offset){
		System.arraycopy(port, offset, this.port, 0, 2);
	}
	public byte[] getPort(){
		return this.port;
	}

	public void setDeviceType(byte[] deviceType,int offset){
		System.arraycopy(deviceType, offset, this.device_type, 0, this.device_type.length);
	}
	public byte[] getDeviceType(){
		return this.device_type;
	}
	
	public void setManufrInfo(byte[] manufrInfo,int offset){
		System.arraycopy(manufrInfo, offset, this.manufr_info, 0, this.manufr_info.length);
	}
	public byte[] getManufrInfo(){
		return this.manufr_info;
	}
	
	public void setVersion(byte ver){
		this.version = ver;
	}
	public byte getVersion(){
		return this.version;
	}

	public void setSerialNum(byte[] serialNum,int offset){
		System.arraycopy(serialNum, offset, this.serial_number, 0, this.serial_number.length);
	}
	public byte[] getSerialNum(){
		return this.serial_number;
	}
	*/
	
	public void setStatus(int status){
		this.status = status;
	}
	public int getStatus(){
		return this.status;
	}

	public void setPreStatus(int pre_status){
		this.pre_status = pre_status;
	}
	public int getPreStatus(){
		return this.pre_status;
	}
	
	public void setDeviceName(String deviceName){
		this.name = deviceName;
	}
	public String getDeviceName(){
		return this.name;
	}
	
	/*
	public void setTask(String task){
		this.task = task;
	}
	public String getTask(){
		return this.task;
	}
	
	
	public boolean getCheck(){
		return this.isCheck;
	}
	public void setCheck(boolean isCheck){
		this.isCheck = isCheck;
	}
	
	public ArrayList<TaskInfo> getTaskList(){
		if(taskList == null)
			taskList = new ArrayList<TaskInfo>();
		
		return this.taskList;
	}	
	
	public void setIsOnline(boolean isOnline){
		this.isOnline = isOnline;
	}
	public boolean getIsOnline(){
		return this.isOnline;
	}
	*/
	
	public boolean getIsOn(){
		return this.isOn;
	}
	public void setIsOn(boolean on){
		this.isOn = on;
	}

	public void setImageContent(byte[] content){
		this.img_content = content;
	}
	public byte[] getImageContent(){
		return this.img_content;
	}
	
	public void saveImg(Bitmap bitmap){
		String imgPath = getMacStr().replaceAll(":", "") + ".jpg";
		savePic(bitmap,imgPath);
	}
	
	public String getImgPath(){
		String imgPath = getMacStr().replaceAll(":", "") + ".jpg";
		//return loadImage(imgPath);
		return imgPath;
	}
	
	public Bitmap getImg(){
		String imgPath = getMacStr().replaceAll(":", "") + ".jpg";
		return loadImage(imgPath);
		//return imgPath;
	}
	
	public Bitmap loadImage(String imageUrl){
		Bitmap drawable = null;
		//drawable = imageCache.get(imageUrl);
		//if(drawable!=null){
//			LogUtil.printInfo("get bitmap from cache");
			//return drawable;
		//}
		if (avaiableSdcard()) {// ??????sd????????sd??
			drawable = getPicByPath(Constants.imageCachePath, imageUrl);
			if (drawable != null) {
				LogUtil.printInfo("get bitmap from disk");
				//imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable));
				//return drawable;
			}
		} else {// ????????sd????????????????
			drawable = getPicByPath(Constants.imageCachePath_data, imageUrl);
			if (drawable != null) {
				LogUtil.printInfo("get bitmap from disk");
				// imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable));
				//return drawable;
			}
		}
		return drawable;
	}
	
	/**
	 * ????????
	 * 
	 * @param picName
	 * @return
	 */
	public Bitmap getPicByPath(String path, String picName) {
		if(!"".equals(picName)&&picName!=null){
			picName = picName.substring(picName.lastIndexOf("/") + 1);
			String filePath = path + picName;
			
			//????????????????
				File file = new File(filePath);
				if(!file.exists()){//??????????
					return null;
				}
			
			Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			return bitmap;
		}else{
			return null;
		}		
	}
	
	
	/**
	 * ????????????sd??
	 * 
	 * @return
	 */
	public boolean avaiableSdcard() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void savePic(Bitmap bitmap, String imageUrl) {
		if (bitmap != null && imageUrl != null && !"".equals(imageUrl)) {
			if (avaiableSdcard()) {// ??????sd??????????sd??
				savePicToSdcard(bitmap, imageUrl);
			} else {// ????????sd??????????????????
				saveToDataDir(bitmap, imageUrl);
			}
		}
	}

	/**
	 * ????????????sd??
	 * 
	 * @param bitmap
	 *            ????
	 * @param picName
	 *            ????????????????id????
	 */
	private void savePicToSdcard(Bitmap bitmap, String picName) {

		//picName = picName.substring(picName.lastIndexOf("/") + 1);
		String path = Constants.imageCachePath;
		LogUtil.printInfo(path + picName);
		File file = new File(path + picName);
		FileOutputStream outputStream;
		if (!file.exists()) {
			
		}
		else{
			file.delete();
		}
		
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int options = 100;
			while ( baos.toByteArray().length / 1024>100) {
				baos.reset();//????baos??????baos  
			    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
			    options -= 10;//??????????10  
			} 
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			outputStream.write(baos.toByteArray());
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			// Log.e("", e.toString());
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param bitmap
	 * @param fileName
	 *            ????????
	 */
	private void saveToDataDir(Bitmap bitmap, String fileName) {
		//fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		String path = Constants.imageCachePath_data;
		LogUtil.printInfo(path + fileName);
		File file = new File(path + fileName);
		FileOutputStream outputStream;
		if (!file.exists()) {
			
		}
		else{
			file.delete();
		}
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			outputStream.close();
		} catch (Exception e) {
			Log.e("", e.toString());
		}
	}
}
