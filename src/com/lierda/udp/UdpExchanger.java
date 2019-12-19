package com.lierda.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.lierda.utils.Constants;
import com.lierda.utils.LogUtil;


public class UdpExchanger {

	private DatagramSocket udpSocket = null;
	private DatagramPacket dataPacket = null;
	private DatagramPacket recvPacket = null;
	int DEFAULT_PORT = 48899;
	int TARGET_PORT = 48899;
	int MAX_DATA_PACKET_LENGTH = 1024;
	byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	String log = "";
	private boolean IsAction = false;
	private static UdpExchanger instance = null;
	
	public static UdpExchanger getInstance() {
		if (null == instance) {
			instance = new UdpExchanger();
			
		}
		return instance;
	}
	
	
	private boolean initSocket(){
		
		
			if(dataPacket==null)
				dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
			if(recvPacket == null)
				recvPacket = new DatagramPacket( buffer, MAX_DATA_PACKET_LENGTH );
			
			if(udpSocket == null){
				LogUtil.printError("create object");
				try {
					udpSocket = new DatagramSocket(DEFAULT_PORT);				
					return true;
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			else{
				return true;
			}

	}
	
	private void closeSocket(){
		
			if(udpSocket!=null)
				udpSocket.close();
			
			udpSocket = null;
			recvPacket = null;
	}
	
	public void startSocket(){
		synchronized(this){
			closeSocket();
			initSocket();
		}
	}
	
	public boolean sendPackage(final byte[] data,final String ipAddress,final boolean isBroadcast){
		new Thread(new Runnable() {
			@Override
			public void run() {
				int tryTime = 3;
				boolean isResult = false;
				while(tryTime >0 ){


					tryTime--;
					isResult = sendCmd(data,ipAddress,isBroadcast);
					if(isResult){
						tryTime = 0;
						break;
					}
					else{
						startSocket();
					}
				}
			}
		}).start();
		return true;
		//return isResult;
	}
	
	
	private boolean sendCmd(byte[] data,String ipAddress,boolean isBroadcast){
				
		boolean isSuccess = false;
		
		//synchronized(udpSocket){
			
			//initSocket();
			
			try {
	
				  // DatagramPacket receivePacket = new DatagramPacket(new byte[MAX_DATA_PACKET_LENGTH], MAX_DATA_PACKET_LENGTH);
			       dataPacket.setData( data );
			       //LogUtil.printInfo("send data length="+Integer.toString(data.length));
			       dataPacket.setLength( data.length );
			       dataPacket.setPort( TARGET_PORT );   
			   
			       InetAddress broadcastAddr;	   
			       broadcastAddr = InetAddress.getByName(ipAddress); //"192.168.1.11"
			       dataPacket.setAddress(broadcastAddr);
			       //udpSocket.setBroadcast(isBroadcast);
			       udpSocket.send(dataPacket);
			       
			       
			       isSuccess = true;
			       LogUtil.printError("send udp success!");
			   } 
			    catch (Exception e) {
			    	e.getStackTrace();
			    	LogUtil.printError("send udp failed!");
			    	//udpSocket.close();
			    	isSuccess = false;
			   }
			
			//closeSocket();
		//}
		
		return isSuccess;
	}
	
	public String receivePackage()throws Exception{
		
		//synchronized(udpSocket){
			
			//initSocket();
		
			//LogUtil.printInfo("receivePackage");
			udpSocket.setSoTimeout(Constants.DEFAULT_TIMEOUT);
			udpSocket.receive(recvPacket);	
			byte[] data = recvPacket.getData();
			int length = recvPacket.getLength();
			
			/*
			String log = "";
			for(int i =0;i<length;i++)
				log += Integer.toHexString((data[i])&0xFF)+ " ";
			*/
			log = null;
			log = new String(data); 
			log = log.substring(0,length);
			LogUtil.printInfo("recv data0="+log);
			
			//start,end
			//&& buffer[buffer.length-1] == CC3XConstants.CMD_END
			/*
			if(data[0] == CC3XConstants.CMD_START ){
				if(data[8]==(byte)0x80 || data[8]==(byte)0xC1 || data[8]==(byte)0xC2
						|| data[8] == (byte)0xC3 || data[8] == (byte)0xFF){
					
					LogUtil.printInfo("success!");
					byte[] result = new byte[length];
					System.arraycopy(data, 0, result, 0, length);
					Arrays.fill(buffer,(byte)0);
					return result;
					
				}
			}
			*/
					
			//LogUtil.printInfo("failed!");
			//closeSocket();
		//}
		return log;
	}
}
