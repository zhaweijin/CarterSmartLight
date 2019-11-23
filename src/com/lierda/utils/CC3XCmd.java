package com.lierda.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.Enumeration;

import android.util.Log;


public class CC3XCmd {
	
	private static final int port = 7000;
	
	//private byte[] test_cmd = {0x68,0x65,0x01,(byte)0xa8,(byte)0xc0,0x58,0x1b,0x0c,0x00,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,0x00,0x01,0x08,0x00,0x79,(byte)0x7c,0x16};
	//com
	private byte dataLen = 0;
	
	private byte ci = 0;
	private String ipAddressStr = "";
	private byte[] ipAddress = {0x00,0x00,0x00,0x00};
	private byte[] macAddress = {0x00,0x00,0x00,0x00,0x00,0x00};
	private byte[] device_type = {0x00,0x00};

	//for device only
	//private byte[] mamufr = {0x00,0x00};
	//private byte version = 0;
	//private byte status = 0;
	//private byte[] serialNum = {0x00,0x00,0x00,0x00,0x00,0x00};

	//check byte
	private int check = 0;
	
	//cmd
	private static byte[] cmd = null;
	//data
	private static byte[] data = null;
	
	private static CC3XCmd instance = null;
	
	public String getLocalIpAddress() {     
        try {     
            for (Enumeration<NetworkInterface> en = NetworkInterface     
                    .getNetworkInterfaces(); en.hasMoreElements();) {     
                NetworkInterface intf = en.nextElement();     
                for (Enumeration<InetAddress> enumIpAddr = intf     
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {     
                    InetAddress inetAddress = enumIpAddr.nextElement();     
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  
                    	LogUtil.printInfo("host ip="+inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();     
                    }     
                }     
            }     
        } catch (SocketException ex) {     
            Log.e("WifiPreference IpAddress", ex.toString());     
        }     
        return null;     
    }     
	
	public static CC3XCmd getInstance() {
		if (null == instance) {
			instance = new CC3XCmd();
			cmd = new byte[255];
			data = new byte[128];
		}
		return instance;
	}
	
	public void configDevice(byte[] macAddress,byte[] device_type,String ipStr){
		
		byte[] ip2 = new byte[4];
		InetAddress localMachine=null;
        try {
        	
        	//String ipStr = getLocalIpAddress();
        	if(ipStr != null){	   
        		localMachine = InetAddress.getByName(ipStr); 
        		System.arraycopy(localMachine.getAddress(), 0, ip2, 0, 4);
        	}
        } catch (Exception e) {
       	 // TODO Auto-generated catch block
       	 e.printStackTrace();
        }
               		
        ipAddress[0] = ip2[3];
        ipAddress[1] = ip2[2];
        ipAddress[2] = ip2[1];
        ipAddress[3] = ip2[0];
		System.arraycopy(macAddress, 0, this.macAddress, 0, 6);
		System.arraycopy(device_type, 0, this.device_type, 0, 2);
	}
	
	public byte[] searchDevice(){
		dataLen = 3;
		data[0] = 0x08;
		data[1] = 0x00;
		data[2] = 0x79;
		return packageCmd(CC3XConstants.CMD_TYPE_SEARCH_DEVICE);
	}
	
	public byte[] switchDevice(boolean on){
		dataLen = 4;
		data[0] = 0x01;
		data[1] = 0x00;
		data[2] = 0x79;
		if(on)
			data[3] = (byte) 0xFF;
		else
			data[3] = 0x00;
		return packageCmd(CC3XConstants.CMD_TYPE_TURN_ON_OFF_DEVICE);
	}
	
	public byte[] setTimeDevice(byte[] time){
		dataLen = 3+8;
		data[0] = 0x0C;
		data[1] = 0x00;
		data[2] = 0x6D;
		
		//set BCD data��YYYYMMDDhhmmssww
		System.arraycopy(time, 0, data, 3, 8);
		
		return packageCmd(CC3XConstants.CMD_TYPE_SET_TIME_DEVICE);
	}
	
	public byte[] setTimerDevice(int index,boolean isOn,String timer,byte hour,byte min){
		
		dataLen = 6;
	    data[0] = 0x03;
	    data[1] = (byte)index;
	    data[2] = 0x7D;
	    
	    data[3] = (byte)0xFF;
	    if(isOn == false)
	        data[3] = (byte)(data[3]&0x7F);
	    
	    if(timer.indexOf("����") == -1){
	        data[3] = (byte)(data[3]&0xBF);
	    }
	    if(timer.indexOf("����") == -1){
	        data[3] = (byte)(data[3]&0xDF);
	    }
	    if(timer.indexOf("����") == -1){
	        data[3] = (byte)(data[3]&0xEF);
	    }
	    if(timer.indexOf("����") == -1){
	        data[3] = (byte)(data[3]&0xF7);
	    }
	    if(timer.indexOf("�ܶ�") == -1){
	        data[3] = (byte)(data[3]&0xFB);
	    }
	    if(timer.indexOf("��һ") == -1){
	        data[3] = (byte)(data[3]&0xFD);
	    }
	    if(timer.indexOf("����") == -1){
	        data[3] = (byte)(data[3]&0xFE);
	    }
	    
	    String hourStr = Integer.toString(hour);
	    data[4]= (byte)Integer.parseInt(hourStr, 16);
	    String minStr = Integer.toString(min);
	    data[5]= (byte)Integer.parseInt(minStr, 16);

		return packageCmd(CC3XConstants.CMD_TYPE_SET_TIMER_DEVICE);
	}
	
	public byte[] readInfoDevice(){
		dataLen = 0;
		return packageCmd(CC3XConstants.CMD_TYPE_READ_INFO_DEVICE);
	}
	
	
	private byte[] packageCmd(String cmdType){
		
		int offset = 1;

		if(cmdType.equals(CC3XConstants.CMD_TYPE_SEARCH_DEVICE)){
			ci = 0x00;			
		}
		else if(cmdType.equals(CC3XConstants.CMD_TYPE_TURN_ON_OFF_DEVICE)){
			ci = 0x41;
		}
		else if(cmdType.equals(CC3XConstants.CMD_TYPE_SET_TIME_DEVICE)){
			ci = 0x43;
		}
		else if(cmdType.equals(CC3XConstants.CMD_TYPE_SET_TIMER_DEVICE)){
			ci = 0x42;
		}
		else if(cmdType.equals(CC3XConstants.CMD_TYPE_READ_INFO_DEVICE)){
			ci = (byte)0xFF;
		}
		
		//start byte
		cmd[offset] = CC3XConstants.CMD_START;
		offset++;
		//ip address
		System.arraycopy(ipAddress, 0, cmd, offset, 4);
		//cmd[offset] = 0x66;
		//cmd[offset+1] = 0x01;
		//cmd[offset+2] = (byte)0xa8;
		//cmd[offset+3] = (byte)0xc0;
		offset += 4;
		//port
		System.arraycopy(toByteArray(port,2), 0, cmd, offset, 2);
		//byte temp = cmd[offset+1];
		//cmd[offset+1] = cmd[offset];
		//cmd[offset] = temp;
		offset += 2;
		//len,include ci mac device_type,data
		cmd[offset] = (byte) (9+dataLen);
		offset ++;
		//ci
		cmd[offset] = ci;
		offset ++;
		//mac address
		System.arraycopy(macAddress, 0, cmd, offset, 6);
		offset += 6;
		//device_type
		System.arraycopy(device_type, 0, cmd, offset, 2);
		offset += 2;
		//data
		System.arraycopy(data, 0, cmd, offset, dataLen);
		offset += dataLen;
		//check
		check = 0;
		for(int i=9;i<offset;i++){
			LogUtil.printInfo(Integer.toHexString(cmd[i]&0xFF));
			check += cmd[i];
		}
		cmd[offset]  = (byte)(check&0xFF);
		offset++;
		//end
		cmd[offset] = CC3XConstants.CMD_END;
		
		cmd[0] = (byte) offset; //total len
		
		
		
		byte[] result = new byte[cmd[0]];
		System.arraycopy(cmd, 1, result, 0, cmd[0]);
		
		String log = "";
		for(int i =0;i<result.length;i++)
			log += Integer.toHexString((result[i])&0xFF)+ " ";
		LogUtil.printInfo("send data="+log);
		
		//return test_cmd;
		return result;
	}
	
    public  byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
       for ( int i = 0; (i < 4) && (i < iArrayLen); i++) {
             bLocalArr[i] = (byte)( iSource>>8*i & 0xFF );

        }
         return bLocalArr;
    }

     // ��byte����bRefArrתΪһ������,�ֽ�����ĵ�λ�����͵ĵ��ֽ�λ
     public  int toInt(byte[] bRefArr) {
       int iOutcome = 0;
         byte bLoop;

         for ( int i =0; i<bRefArr.length ; i++) {
            bLoop = bRefArr[i];
             iOutcome+= (bLoop & 0xFF) << (8 * i);

       }

        return iOutcome;
     }

     //configure
     public String configureBroadcastID(){
    	 
    	 return Constants.CONFIGURE_HEAD;
     }
     
     public String configureOK(){
    	
    	 return Constants.CONFIGURE_OK;
     }
     
     public String configureSETSSID(String ssid){
    	 
    	 return MessageFormat.format(Constants.CONFIGURE_SET_SSID,ssid);
     }
     
     public String configureSETWSKEY(String auth, String encrypt,String key){
    	 
    	 return MessageFormat.format(Constants.CONFIGURE_SET_WSKEY, auth,encrypt,key);
     }
     
     public String configureGETSSID(){
    	 
    	 return Constants.CONFIGURE_QUERY_SSID;
     }

     public String configureGETWSKEY(){
    	 
    	 return Constants.CONFIGURE_QUERY_WSKEY;
     }
     
     public String configureKeetAlive(){
    	 
    	 return Constants.CMD_HEARTBEAT;
     }
     
     public String configureQuit(){
    	 
    	 return Constants.CONFIGURE_QUIT;
     }
     
     public String scan(){
    	 return Constants.CONFIGURE_SCAN;
     }
     
     //reset and restart
     public String restart(){
    	 return Constants.CMD_RESTART;
     }
     
     public String reset(){
    	 return Constants.CMD_FACTORY_SETTING;
     }
     
     //server
     public String getServerAddress(){
    	 return Constants.CONFIGURE_GET_SERVER_ADDRESS;
     }
     public String getServerPort(){
    	 return Constants.CONFIGURE_GET_SERVER_PORT;
     }
     public String setServerAddress(String address){
    	 return MessageFormat.format(Constants.CONFIGURE_SET_SERVER_ADDRESS, address);
     }
     public String setServerPort(String port){
    	 return MessageFormat.format(Constants.CONFIGURE_SET_SERVER_PORT, port);
     }
     
     //mode
     public String getMode(){
    	 return Constants.CONFIGURE_GET_WORK_MODE;
     }
     public String setMode(String mode){
    	 return MessageFormat.format(Constants.CONFIGURE_SET_WORK_MODE, mode);
     }
     
     //check remote device
     public String searchRemoteDevice(String macAddress){
    	 return MessageFormat.format(Constants.CMD_TCP_ONLINE, macAddress);
     }
     
     public String remoteControl(String macAddress,String cmdStr){
    	 return MessageFormat.format(Constants.CMD_TCP_REMOTE_CONTROL, macAddress,cmdStr);
     }
}
