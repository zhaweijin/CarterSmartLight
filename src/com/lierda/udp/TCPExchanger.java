package com.lierda.udp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.lierda.utils.LogUtil;


public class TCPExchanger {

	private Socket tcpSocket = null;
	private int MAX_DATA_PACKET_LENGTH = 1024;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	private SocketAddress remoteAddr = null;
	private String ipStr="";
	private int port = 0;
	
	private static TCPExchanger instance = null;
	
	public static TCPExchanger getInstance() {
		if (null == instance) {
			instance = new TCPExchanger();
		}
		return instance;
	}
	
	
	public boolean initSocket(String serverIp,int serverPort){
		
		if(serverIp.equals(this.ipStr) == false || serverPort != port){
			
			LogUtil.printInfo("tcp close!!!");
			closeSocket();
			remoteAddr = null;
			remoteAddr = new InetSocketAddress(serverIp,serverPort);
			tcpSocket = null;
		}
		
		if(tcpSocket == null){
			
			try {				
				LogUtil.printInfo("serverIp="+serverIp+"serverPort="+Integer.toString(serverPort));
				tcpSocket = new Socket(serverIp,serverPort);
				this.ipStr = serverIp;
				this.port = serverPort;
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				LogUtil.printInfo("new socket error");
				return false;
			}
		}
		else{
			return true;
		}
	}
	
	public void closeSocket(){
		if(tcpSocket!=null){
			try {
				tcpSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		tcpSocket = null;
	}
	
	
	public boolean sendPackage(byte[] data){
		
		
		boolean isSuccess = false;
		try {
			if(false == tcpSocket.isConnected())
				tcpSocket.connect(remoteAddr);
			OutputStream ops = tcpSocket.getOutputStream();
			ops.write(data);
			ops.flush();
		       
		       isSuccess = true;
		       LogUtil.printInfo("send tcp success!");
		   } 
		    catch (Exception e) {
		    	LogUtil.printInfo("send tcp failed!");
		    	//udpSocket.close();
		    	isSuccess = false;
		   }
		
		return isSuccess;
	}
	
	public String receivePackage()throws Exception{
		
		LogUtil.printInfo("recv tcp begin");
		InputStream ips = tcpSocket.getInputStream();
		ips.read(buffer);
		
		
		
		/*
		String log = "";
		for(int i =0;i<length;i++)
			log += Integer.toHexString((data[i])&0xFF)+ " ";
		*/
		String log = new String(buffer); 
		int index = log.indexOf("\n");
		if(index != -1){
			log = log.substring(0, index);
		}
		LogUtil.printInfo("recv tcp="+log);
		
		//start,end
		//&& buffer[buffer.length-1] == CC3XConstants.CMD_END
		/*
		if(data[0] == CC3XConstants.CMD_START ){
			if(data[8]==(byte)0x80 || data[8]==(byte)0xC1 || data[8]==(byte)0xC2
					|| data[8] == (byte)0xC3 || data[8] == (byte)0xFF){
				
				LogUtil.printInfo("success!");
				byte[] result = new byte[length];
				System.arraycopy(data, 0, result, 0, length);
				Arrays.fill(buffer,(byte)0);//���Buffer
				return result;
				
			}
		}
		*/
				
		//LogUtil.printInfo("failed!");
		return log;
	}
}
