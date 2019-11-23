package com.lierda.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.lierda.utils.LogUtil;


public class UdpSender {

	DatagramSocket udpSocket = null;
	DatagramPacket dataPacket = null;
	int DEFAULT_PORT = 8899;
	int TARGET_PORT = 8899;
	int MAX_DATA_PACKET_LENGTH = 255;
	byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	
	
	private static UdpSender instance = null;
	
	public static UdpSender getInstance() {
		if (null == instance) {
			instance = new UdpSender();
		}
		return instance;
	}
	
	
	
	public boolean sendPackage(byte[] data,String ipAddress){
		
		
		boolean isSuccess = false;
		try {
				dataPacket = null;
				udpSocket = new DatagramSocket(DEFAULT_PORT);	
				
				if(dataPacket == null)
					dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);      

			  // DatagramPacket receivePacket = new DatagramPacket(new byte[MAX_DATA_PACKET_LENGTH], MAX_DATA_PACKET_LENGTH);
		       dataPacket.setData( data );
		       LogUtil.printInfo("send data length="+Integer.toString(data.length));
		       dataPacket.setLength( data.length );
		       dataPacket.setPort( TARGET_PORT );   
		   
		       InetAddress broadcastAddr;	   
		       broadcastAddr = InetAddress.getByName(ipAddress); //"192.168.1.11"
		       dataPacket.setAddress(broadcastAddr);
		       udpSocket.setBroadcast(true);
		       udpSocket.send(dataPacket);
		       udpSocket.close();
		       
		       isSuccess = true;
		       LogUtil.printInfo("send success!");
		   } 
		    catch (Exception e) {
		    	LogUtil.printInfo("send failed!");
		    	udpSocket.close();
		    	isSuccess = false;
		   }
		
		return isSuccess;
	}
}
